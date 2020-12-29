package dev.technici4n.fasttransferlib.impl;

import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.transfer.ResourceFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FtlImpl {
	public static final Logger LOGGER = LogManager.getLogger("FastTransferLib");
	public static int version = 0;
	@SuppressWarnings("rawtypes")
	public static final ResourceFunction EMPTY = new ResourceFunction() {
		@Override
		public long apply(Object resource, long count, Simulation simulation) {
			return 0;
		}

		@Override
		public long apply(Object resource, long numerator, long denominator, Simulation simulation) {
			return 0;
		}

		@Override
		public boolean canApply() {
			return false;
		}
	};
}
