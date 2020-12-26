package dev.technici4n.fasttransferlib.api.fluid.base;

import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.fluid.FluidIo;
import dev.technici4n.fasttransferlib.api.fluid.FluidPreconditions;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * A base implementation of a simple fluid io with only one slot.
 * Consider overriding {@link SingleSlotFluidIo#afterChange()} to call markDirty if that makes sense for you.
 */
public class SingleSlotFluidIo implements FluidIo {
	protected Fluid fluidKey = Fluids.EMPTY;
	protected long fluidAmount = 0;
	protected final long maxCapacity;
	protected int version = 0;

	public SingleSlotFluidIo(long maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

	/**
	 * Called after every change.
	 */
	protected void afterChange() {
		++version;
	}

	@Override
	public int getFluidSlotCount() {
		return 1;
	}

	@Override
	public Fluid getFluid(int slot) {
		FluidPreconditions.checkSingleSlot(slot);
		return fluidKey;
	}

	@Override
	public long getFluidAmount(int slot) {
		FluidPreconditions.checkSingleSlot(slot);
		return fluidAmount;
	}

	@Override
	public boolean supportsFluidInsertion() {
		return true;
	}

	@Override
	public long insert(Fluid fluid, long amount, Simulation simulation) {
		if (this.fluidKey == Fluids.EMPTY || this.fluidKey == fluid) {
			long newQuantity = Math.min(fluidAmount + amount, maxCapacity);
			long inserted = newQuantity - this.fluidAmount;
			long leftover = amount - inserted;

			if (simulation.isActing()) {
				this.fluidKey = fluid;
				fluidAmount = newQuantity;
				afterChange();
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
	public long extract(int slot, Fluid fluid, long maxAmount, Simulation simulation) {
		FluidPreconditions.checkSingleSlot(slot);

		if (fluid == this.fluidKey) {
			long extract = Math.max(maxAmount, fluidAmount);

			if (simulation.isActing()) {
				fluidAmount -= extract;
				if (fluidAmount == 0) this.fluidKey = Fluids.EMPTY;
				afterChange();
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
		result.putString("fluid", Registry.FLUID.getId(fluidKey).toString());
		result.putLong("amount", fluidAmount);
		return result;
	}

	public void fromTag(CompoundTag tag) {
		fluidKey = Registry.FLUID.get(new Identifier(tag.getString("fluid")));
		fluidAmount = tag.getLong("amount");
	}

	public void toPacket(PacketByteBuf buf) {
		buf.writeInt(Registry.FLUID.getRawId(fluidKey));
		buf.writeLong(fluidAmount);
	}

	public void fromPacket(PacketByteBuf buf) {
		Registry.FLUID.get(buf.readInt());
		fluidAmount = buf.readLong();
	}
}
