package dev.technici4n.fasttransferlib.api.transaction;

import dev.technici4n.fasttransferlib.impl.transaction.TransactionImpl;

/**
 * A global operation where {@linkplain Participant participants} guarantee atomicity:
 * either the whole operation succeeds, or it is completely cancelled.
 * <p>Transactions may only happen on the server thread, and they are global.
 * If a transaction is already open, opening a new transaction will create a nested transaction.
 * <ul>
 *     <li>Transaction state can be viewed as a stack.</li>
 *     <li>Nested transactions can be committed or rolled back like a regular transaction,
 *     but if a transaction is rolled back all its nested transactions will be rolled back as well,
 *     even if they were committed.</li>
 *     <li>In practice, this means that when a participant enlists itself in a transaction,
 *     the transaction manager ensures that it's enlisted in all the transactions in the stack.
 *     It is guaranteed that {@link Participant#onEnlist} will be called for a parent transaction
 *     before it's called for a child transaction.</li>
 *     <li>{@link Participant#onClose} will be called for every closed transaction,
 *     but a committed nested transaction may be aborted later.
 *     As such, it is better to defer irreversible success until {@link Participant#onFinalSuccess} is called,
 *     which will only be called on success for the root transaction.</li>
 * </ul></p>
 */
public interface Transaction extends AutoCloseable {
	/**
	 * Rollback all changes that happened during this transaction.
	 */
	void rollback();

	/**
	 * Validate all changes that happened during this transaction.
	 */
	void commit();

	/**
	 * Rollback, called automatically at the end of try-with-resources.
	 */
	@Override
	void close();

	/**
	 * Open a new transaction.
	 * It must always be used in a try-with-resources block.
	 * If the transaction is not rolled back or committed when it is closed, it will be rolled back.
	 */
	static Transaction open() {
		return TransactionImpl.open();
	}

	/**
	 * Enlist the participant in the current transaction and all its parents if there is an open transaction,
	 * and if the participant isn't yet enlisted.
	 * {@link Participant#onEnlist} will be called for every transaction in the transaction stack in which it is not enlisted yet,
	 * from the oldest transaction to the most recent one.
	 */
	static void enlistIfOpen(Participant participant) {
		TransactionImpl.enlistIfOpen(participant);
	}

	/**
	 * @return True if a transaction is currently open, false otherwise.
	 */
	static boolean isOpen() {
		return TransactionImpl.isOpen();
	}

	/**
	 * Call {@link Participant#onFinalSuccess} if no transaction is currently open.
	 */
	static void successIfNotOpen(Participant participant) {
		if (!isOpen()) {
			participant.onFinalSuccess();
		}
	}

	static void wrapModification(Participant participant, Runnable runnable) {
		enlistIfOpen(participant);
		runnable.run();
		successIfNotOpen(participant);
	}
}
