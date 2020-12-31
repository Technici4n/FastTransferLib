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
		try (Transaction tx = Transaction.open()) {
			assertEquals(50, storage.insertionFunction().apply(Fluids.LAVA, 50, 10, tx));
			tx.commit();
		}
		ensureState(storage, Fluids.LAVA, 50);

		// test extraction inside a transaction
		try (Transaction tx = Transaction.open()) {
			assertEquals(50, storage.extractionFunction().apply(Fluids.LAVA, 50, 10, tx));
			// test that the storage is now empty
			ensureEmpty(storage);
		}

		// test that it rolled back correctly
		ensureState(storage, Fluids.LAVA, 50);
	}

	@Test
	void testNestedTransactions() {
		SimpleFluidStorage storage = new SimpleFluidStorage(10, 100);
		ensureEmpty(storage);

		try (Transaction tx1 = Transaction.open()) {
			try (Transaction tx2 = Transaction.open()) {
				// insert water
				assertEquals(50, storage.insertionFunction().apply(Fluids.WATER, 50, 10, tx2));
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

	@Test
	void testDenoms() {
		SimpleFluidStorage storage = new SimpleFluidStorage(8, 8);

		try (Transaction tx = Transaction.open()) {
			// trying to insert 5/6 should insert 3/6 and fail to insert 2/6.
			assertEquals(3, storage.insertionFunction().apply(Fluids.WATER, 5, 6, tx));
			// make sure 4/8 is now in the storage
			ensureState(storage, Fluids.WATER, 4);
			// now, trying to insert 7/6 should try to insert 6/6 first, but in the end only 3/6 should be inserted
			assertEquals(3, storage.insertionFunction().apply(Fluids.WATER, 7, 6, tx));
			// make sure 8/8 is now in the storage
			ensureState(storage, Fluids.WATER, 8);
			// commit
			tx.commit();
		}

		// make sure that after a committed transaction 8/8 is still stored
		ensureState(storage, Fluids.WATER, 8);
	}
}
