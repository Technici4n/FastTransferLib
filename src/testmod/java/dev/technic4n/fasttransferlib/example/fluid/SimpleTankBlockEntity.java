package dev.technic4n.fasttransferlib.example.fluid;

import dev.technici4n.fasttransferlib.api.base.BaseSingleFluidStorage;
import dev.technici4n.fasttransferlib.api.fluid.FluidApi;
import dev.technici4n.fasttransferlib.api.fluid.FluidConstants;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;

public class SimpleTankBlockEntity extends BlockEntity {
	public BaseSingleFluidStorage storage = new BaseSingleFluidStorage(FluidConstants.BUCKET * 10);

	public SimpleTankBlockEntity() {
		super(FluidExample.SIMPLE_TANK_BLOCK_ENTITY);
	}

	public static void init() {
		FluidApi.SIDED.registerForBlockEntities((blockentity, side) -> ((SimpleTankBlockEntity) blockentity).storage, FluidExample.SIMPLE_TANK_BLOCK_ENTITY);
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);
		tag.put("storage", storage.toTag());
		return tag;
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		storage.fromTag(tag.getCompound("storage"));
	}
}
