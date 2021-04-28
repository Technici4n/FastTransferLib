package dev.technici4n.fasttransferlib.api.energy.base;

import com.google.common.base.Preconditions;
import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.energy.EnergyIo;

import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;

/**
 * An energy io for an item. Energy is stored in the "energy" key of the tag if there is some, or it's 0 if there is no tag.
 * This allows just-crafted empty batteries and emptied batteries to stack correctly.
 * Supports stackable items, and energy will be evenly distributed among them.
 */
public class SimpleItemEnergyIo implements EnergyIo {
	private final double capacity;
	private final double maxInsertion;
	private final double maxExtraction;
	private final ItemStack stack;

	public static ItemApiLookup.ItemApiProvider<EnergyIo, Void> getProvider(double capacity, double maxInsertion, double maxExtraction) {
		return (stack, void_) -> new SimpleItemEnergyIo(capacity, maxInsertion, maxExtraction, stack);
	}

	private SimpleItemEnergyIo(double capacity, double maxInsertion, double maxExtraction, ItemStack stack) {
		Preconditions.checkArgument(stack.getCount() >= 1, "Stack must have a count of at least 1");

		this.capacity = capacity;
		this.maxInsertion = simplify(maxInsertion);
		this.maxExtraction = simplify(maxExtraction);
		this.stack = stack;
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public double getEnergy() {
		if (stack.hasTag()) {
			return simplify(stack.getTag().getDouble("energy") * stack.getCount());
		} else {
			return 0;
		}
	}

	@Override
	public double getEnergyCapacity() {
		return capacity;
	}

	@Override
	public boolean supportsInsertion() {
		return maxInsertion > 0;
	}

	@Override
	public double insert(double amount, Simulation simulation) {
		double inserted = Math.min(Math.min(amount, maxInsertion), capacity - getEnergy());
		incrementEnergy(inserted, simulation);
		return simplify(amount - inserted);
	}

	@Override
	public boolean supportsExtraction() {
		return maxExtraction > 0;
	}

	@Override
	public double extract(double maxAmount, Simulation simulation) {
		double extracted = Math.min(Math.min(maxAmount, maxExtraction), getEnergy());
		incrementEnergy(-extracted, simulation);
		return simplify(extracted);
	}

	private void incrementEnergy(double increment, Simulation simulation) {
		double newEnergy = (getEnergy() + increment) / stack.getCount();

		if (simulation.isActing()) {
			if (newEnergy < 1e-9) {
				stack.removeSubTag("energy");
			} else {
				stack.getOrCreateTag().putDouble("energy", newEnergy);
			}
		}
	}

	private static double simplify(double energy) {
		return Math.abs(energy) < 1e-9 ? 0 : energy;
	}
}
