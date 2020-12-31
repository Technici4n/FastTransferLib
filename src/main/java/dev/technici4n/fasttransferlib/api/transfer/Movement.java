package dev.technici4n.fasttransferlib.api.transfer;

import java.util.function.Predicate;

import dev.technici4n.fasttransferlib.api.transaction.Transaction;

public class Movement {
	public static <T> long move(Storage<T> from, Storage<T> to, Predicate<T> filter, long maxAmount) {
		long[] totalMoved = new long[] { 0 };
		from.forEach(view -> {
			T resource = view.resource();
			if (!filter.test(resource)) return false; // keep iterating
			long maxExtracted;

			// check how much can be extracted
			try (Transaction tx = Transaction.open()) {
				maxExtracted = view.extractionFunction().apply(resource, maxAmount - totalMoved[0], tx);
				tx.rollback();
			}

			try (Transaction tx = Transaction.open()) {
				// check how much can be inserted
				long accepted = to.insertionFunction().apply(resource, maxExtracted, tx);

				// extract it, or rollback if the amounts don't match
				if (from.extractionFunction().apply(resource, accepted, tx) == accepted) {
					totalMoved[0] += accepted;
					tx.commit();
				}
			}

			return maxAmount == totalMoved[0]; // stop iteration if nothing can be moved anymore
		});
		return totalMoved[0];
	}
}
