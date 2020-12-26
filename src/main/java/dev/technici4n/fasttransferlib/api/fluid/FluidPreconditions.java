package dev.technici4n.fasttransferlib.api.fluid;

public class FluidPreconditions {
	public static void checkSingleSlot(int slot) {
		if (slot != 0) {
			throw new IllegalArgumentException("Only 1 Slot In This Fluid Storage");
		}
	}
}
