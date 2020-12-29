package dev.technici4n.fasttransferlib;

import dev.technici4n.fasttransferlib.api.transaction.Transaction;
import dev.technici4n.fasttransferlib.base.fluid.SimpleFluidStorage;
import dev.technici4n.fasttransferlib.impl.transaction.TransactionImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import net.minecraft.Bootstrap;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;

public class SimpleFluidStorageTests {
	private SimpleFluidStorage storage;

	@BeforeAll
	public static void loadMinecraft() {
		Bootstrap.initialize();
	}

	@BeforeEach
	void setupServerThread() {
		TransactionImpl.setServerThread(Thread.currentThread());
	}

	private void ensureState(Fluid fluid, long amount) {
		assertEquals(fluid, storage.fluid);
		assertEquals(amount, storage.amount);
	}

	private void ensureEmpty() {
		ensureState(Fluids.EMPTY, 0);
	}

	@Test
	void testSimpleStorage() {
		storage = new SimpleFluidStorage(10, 100);
		// test initial state
		ensureEmpty();
		// test insertion
		assertEquals(50, storage.insertionFunction().apply(Fluids.LAVA, 50, 10));
		ensureState(Fluids.LAVA, 50);
		// test extraction inside a transaction
		try (Transaction ignored = Transaction.open()) {
			assertEquals(50, storage.extractionFunction().apply(Fluids.LAVA, 50, 10));
			// test that the storage is now empty
			ensureEmpty();
		}
		// test that it rollbacked correctly
		ensureState(Fluids.LAVA, 50);
	}
}
