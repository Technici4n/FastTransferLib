package dev.technici4n.fasttransferlib.impl;

import dev.technici4n.fasttransferlib.api.transaction.Transaction;
import dev.technici4n.fasttransferlib.api.transfer.StorageFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FtlImpl {
	public static final Logger LOGGER = LogManager.getLogger("FastTransferLib");
	public static int version = 0;
	@SuppressWarnings("rawtypes")
	public static final StorageFunction EMPTY = new StorageFunction() {
		@Override
		public long apply(Object resource, long amount, Transaction tx) {
			return 0;
		}

		@Override
		public long apply(Object resource, long numerator, long denominator, Transaction tx) {
			return 0;
		}

		@Override
		public boolean isEmpty() {
			return true;
		}
	};
}
