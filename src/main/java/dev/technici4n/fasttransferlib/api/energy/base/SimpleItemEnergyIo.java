package dev.technici4n.fasttransferlib.api.energy.base;

import dev.technici4n.fasttransferlib.api.ContainerItemContext;
import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.energy.EnergyIo;
import dev.technici4n.fasttransferlib.api.item.ItemKey;
import dev.technici4n.fasttransferlib.api.item.ItemKeyApiLookup;

import net.minecraft.nbt.CompoundTag;

/**
 * An energy io for an item. Energy is stored in the "energy" key of the tag if there is some, or it's 0 if there is no tag.
 * This allows just-crafted empty batteries and emptied batteries to stack correctly.
 * Supports stackable items, and energy will be evenly distributed among them.
 */
public class SimpleItemEnergyIo implements EnergyIo {
	private final double capacity;
	private final double maxInsertion;
	private final double maxExtraction;
	private final double energy;
	private final ItemKey key;
	private final ContainerItemContext ctx;

	public static ItemKeyApiLookup.ItemKeyApiProvider<EnergyIo, ContainerItemContext> getProvider(double capacity, double maxInsertion, double maxExtraction) {
		return (key, ctx) -> new SimpleItemEnergyIo(capacity, maxInsertion, maxExtraction, key, ctx);
	}

	private SimpleItemEnergyIo(double capacity, double maxInsertion, double maxExtraction, ItemKey key, ContainerItemContext ctx) {
		this.capacity = capacity;
		this.maxInsertion = simplify(maxInsertion);
		this.maxExtraction = simplify(maxExtraction);
		this.energy = Math.min(key.hasTag() ? simplify(key.copyTag().getDouble("energy")) : 0, capacity);
		this.key = key;
		this.ctx = ctx;
	}

	@Override
	public double getEnergy() {
		return energy;
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
		if (ctx.getCount() <= 0) return amount;
		double inserted = Math.min(Math.min(amount, maxInsertion), capacity - energy);
		double newAmount = simplify(energy + inserted / ctx.getCount());
		return changeStoredEnergy(newAmount, simulation) ? simplify(amount - inserted) : amount;
	}

	@Override
	public boolean supportsExtraction() {
		return maxExtraction > 0;
	}

	@Override
	public double extract(double maxAmount, Simulation simulation) {
		if (ctx.getCount() <= 0) return 0;
		double extracted = Math.min(Math.min(maxAmount, maxExtraction), energy);
		double newAmount = simplify(energy - extracted / ctx.getCount());
		return changeStoredEnergy(newAmount, simulation) ? simplify(extracted) : 0;
	}

	private boolean changeStoredEnergy(double energy, Simulation simulation) {
		CompoundTag tag = key.copyTag();

		if (energy < 1e-9) {
			if (tag != null) {
				tag.remove("amount");

				if (tag.isEmpty()) {
					tag = null;
				}
			}
		} else {
			if (tag == null) {
				tag = new CompoundTag();
			}

			tag.putDouble("amount", energy);
		}

		ItemKey targetKey = ItemKey.of(key.getItem(), tag);
		return ctx.transform(ctx.getCount(), targetKey, simulation);
	}

	private static double simplify(double energy) {
		return Math.abs(energy) < 1e-9 ? 0 : energy;
	}
}
