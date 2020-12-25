package dev.technici4n.fasttransferlib.api.base;

import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.fluid.FluidKey;
import dev.technici4n.fasttransferlib.api.transfer.ResourceIo;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BaseSingleFluidStorage implements ResourceIo<FluidKey> {
	protected FluidKey fluidKey = FluidKey.EMPTY;
	protected long fluidVolume = 0;
	protected final long maxCapacity;
	protected int version = 0;

	public BaseSingleFluidStorage(long maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

	@Override
	public int getSlotCount() {
		return 1;
	}

	@Override
	public FluidKey getResourceKey(int slot) {
		if (slot != 0) throw new IllegalArgumentException("Only 1 Slot In This Fluid Storage");
		return fluidKey;
	}

	@Override
	public long getAmount(int slot) {
		if (slot != 0) throw new IllegalArgumentException("Only 1 Slot In This Fluid Storage");
		return fluidVolume;
	}

	@Override
	public boolean supportsInsertion() {
		return true;
	}

	@Override
	public long insert(FluidKey fluid, long amount, Simulation simulation) {
		if (this.fluidKey == FluidKey.EMPTY || this.fluidKey == fluid) {
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
	public boolean supportsExtraction() {
		return true;
	}

	@Override
	public long extract(int slot, FluidKey fluid, long maxAmount, Simulation simulation) {
		if (slot != 0) throw new IllegalArgumentException("Only 1 Slot In This Fluid Storage");

		if (fluid == this.fluidKey) {
			long extract = Math.max(maxAmount, fluidVolume);

			if (simulation.isActing()) {
				version++;
				fluidVolume -= extract;
				if (fluidVolume == 0) this.fluidKey = FluidKey.EMPTY;
			}
		}

		return 0;
	}

	@Override
	public int getVersion() {
		return version;
	}

	public CompoundTag toTag() {
		CompoundTag result = new CompoundTag();
		result.putString("fluid", Registry.FLUID.getId(fluidKey.getFluid()).toString());
		result.putLong("volume", fluidVolume);
		return result;
	}

	public void fromTag(CompoundTag tag) {
		fluidKey = FluidKey.of(Registry.FLUID.get(new Identifier(tag.getString("fluid"))));
		fluidVolume = tag.getLong("volume");
	}

	public void toPacket(PacketByteBuf buf) {
		buf.writeInt(Registry.FLUID.getRawId(fluidKey.getFluid()));
		buf.writeLong(fluidVolume);
	}

	public void fromPacket(PacketByteBuf buf) {
		fluidKey = FluidKey.of(Registry.FLUID.get(buf.readInt()));
		fluidVolume = buf.readLong();
	}
}
