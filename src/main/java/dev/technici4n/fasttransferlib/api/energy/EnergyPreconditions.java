package dev.technici4n.fasttransferlib.api.energy;

public final class EnergyPreconditions {
	private EnergyPreconditions() {
	}

	/**
	 * Throw an {@link IllegalArgumentException} if the passed amount is negative.
	 */
	public static void notNegative(double amount) {
		if (amount + 1e-9 < 0) {
			throw new IllegalArgumentException("Energy amount may not be negative, but it is: " + amount);
		}
	}
}
