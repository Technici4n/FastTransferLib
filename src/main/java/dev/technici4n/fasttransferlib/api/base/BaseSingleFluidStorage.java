package dev.technici4n.fasttransferlib.api.base;

import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.fluid.FluidIo;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BaseSingleFluidStorage implements FluidIo {
	protected static final Fluid[] EMPTY_FLUID_ARRAY = {};
	protected static final long[] EMPTY_LONG_ARRAY = {};
	protected Fluid fluidKey = Fluids.EMPTY;
	protected long fluidVolume = 0;
	protected final long maxCapacity;
	protected int version = 0;

	public BaseSingleFluidStorage(long maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

	@Override
	public Fluid[] getFluids() {
		if (fluidKey == Fluids.EMPTY) return EMPTY_FLUID_ARRAY;
		Fluid[] result = new Fluid[1];
		result[0] = fluidKey;
		return result;
	}

	@Override
	public long[] getFluidAmounts() {
		if (fluidKey == Fluids.EMPTY) return EMPTY_LONG_ARRAY;
		long[] result = new long[1];
		result[0] = fluidVolume;
		return result;
	}

	@Override
	public boolean supportsFluidInsertion() {
		return true;
	}

	@Override
	public long insert(Fluid fluid, long amount, Simulation simulation) {
		if (this.fluidKey == Fluids.EMPTY || this.fluidKey == fluid) {
			long newQuantity = Math.min(fluidVolume + amount, maxCapacity);
			long inserted = newQuantity - this.fluidVolume;
			long leftover = amount - inserted;

			if (simulation.isActing()) {
				version++;
				this.fluidKey = fluid;
				fluidVolume = newQuantity;
			}

			return leftover;
		} else {
			return amount;
		}
	}

	@Override
	public boolean supportsFluidExtraction() {
		return true;
	}

	@Override
	public long extract(Fluid fluid, long maxAmount, Simulation simulation) {
		long extract = 0;

		if (fluid == this.fluidKey) {
			extract = Math.max(maxAmount, fluidVolume);

			if (simulation.isActing()) {
				version++;
				fluidVolume -= extract;
				if (fluidVolume == 0) this.fluidKey = Fluids.EMPTY;
			}
		}

		return extract;
	}

	@Override
	public int getVersion() {
		return version;
	}

	public CompoundTag toTag() {
		CompoundTag result = new CompoundTag();
		result.putString("fluid", Registry.FLUID.getId(fluidKey).toString());
		result.putLong("volume", fluidVolume);
		return result;
	}

	public void fromTag(CompoundTag tag) {
		fluidKey = Registry.FLUID.get(new Identifier(tag.getString("fluid")));
		fluidVolume = tag.getLong("volume");
	}

	public void toPacket(PacketByteBuf buf) {
		buf.writeInt(Registry.FLUID.getRawId(fluidKey));
		buf.writeLong(fluidVolume);
	}

	public void fromPacket(PacketByteBuf buf) {
		Registry.FLUID.get(buf.readInt());
		fluidVolume = buf.readLong();
	}
}
