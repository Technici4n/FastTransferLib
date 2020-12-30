package dev.technici4n.fasttransferlib;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.technici4n.fasttransferlib.api.transaction.Transaction;
import dev.technici4n.fasttransferlib.base.fluid.SimpleFluidStorage;
import dev.technici4n.fasttransferlib.impl.transaction.TransactionImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.minecraft.Bootstrap;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;

public class SimpleFluidStorageTests {
	@BeforeAll
	public static void loadMinecraft() {
		Bootstrap.initialize();
	}

	@BeforeEach
	void setupServerThread() {
		TransactionImpl.setServerThread(Thread.currentThread());
	}

	private static void ensureState(SimpleFluidStorage storage, Fluid fluid, long amount) {
		assertEquals(fluid, storage.fluid);
		assertEquals(amount, storage.amount);
	}

	private static void ensureEmpty(SimpleFluidStorage storage) {
		ensureState(storage, Fluids.EMPTY, 0);
	}

	@Test
	void testSimpleStorage() {
		SimpleFluidStorage storage = new SimpleFluidStorage(10, 100);
		// test initial state
		ensureEmpty(storage);
		// test insertion
		assertEquals(50, storage.insertionFunction().apply(Fluids.LAVA, 50, 10));
		ensureState(storage, Fluids.LAVA, 50);

		// test extraction inside a transaction
		try (Transaction ignored = Transaction.open()) {
			assertEquals(50, storage.extractionFunction().apply(Fluids.LAVA, 50, 10));
			// test that the storage is now empty
			ensureEmpty(storage);
		}

		// test that it rollbacked correctly
		ensureState(storage, Fluids.LAVA, 50);
	}

	@Test
	void testNestedTransactions() {
		SimpleFluidStorage storage = new SimpleFluidStorage(10, 100);
		ensureEmpty(storage);

		try (Transaction tx1 = Transaction.open()) {
			try (Transaction tx2 = Transaction.open()) {
				// insert water
				assertEquals(50, storage.insertionFunction().apply(Fluids.WATER, 50, 10));
				// make sure it was inserted
				ensureState(storage, Fluids.WATER, 50);
				// commit
				tx2.commit();
			}

			// make sure it's still inserted
			ensureState(storage, Fluids.WATER, 50);
		}

		// but tx1 was reverted, so the storage should be empty again
		ensureEmpty(storage);
	}
}
