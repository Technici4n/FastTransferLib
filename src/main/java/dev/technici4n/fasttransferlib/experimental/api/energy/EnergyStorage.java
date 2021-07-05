package dev.technici4n.fasttransferlib.experimental.api.energy;

import dev.technici4n.fasttransferlib.experimental.api.context.ContainerItemContext;
import dev.technici4n.fasttransferlib.experimental.impl.FtlExperimental;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

/**
 * Transaction-based prototype energy storage.
 */
public interface EnergyStorage {
	BlockApiLookup<EnergyStorage, Direction> SIDED = BlockApiLookup.get(new Identifier("ftl:sided_energy_storage"), EnergyStorage.class, Direction.class);
	ItemApiLookup<EnergyStorage, ContainerItemContext> ITEM = ItemApiLookup.get(new Identifier("ftl:energy_storage"), EnergyStorage.class, ContainerItemContext.class);

	static EnergyStorage empty() {
		return FtlExperimental.EMPTY_ENERGY_STORAGE;
	}

	static double move(@Nullable EnergyStorage from, @Nullable EnergyStorage to, double maxAmount, @Nullable TransactionContext transaction) {
		if (from == null || to == null) return 0;

		try (Transaction outerTransaction = (transaction == null ? Transaction.openOuter() : transaction.openNested())) {
			double maxExtracted;

			// Simulate extraction.
			try (Transaction extractionTestTransaction = outerTransaction.openNested()) {
				maxExtracted = from.extract(maxAmount, extractionTestTransaction);
				// Aborts.
			}

			double inserted = to.insert(maxExtracted, outerTransaction);
			double extracted = from.extract(inserted, outerTransaction);

			// Only commit transfer if the amounts match.
			if (Math.abs(inserted - extracted) < 1e-9) {
				outerTransaction.commit();
				return inserted;
			}
		}

		return 0;
	}

	double getAmount();

	double getCapacity();

	default boolean supportsExtraction() {
		return true;
	}

	double extract(double maxAmount, TransactionContext transaction);

	default boolean supportsInsertion() {
		return true;
	}

	double insert(double maxAmount, TransactionContext transaction);
}
