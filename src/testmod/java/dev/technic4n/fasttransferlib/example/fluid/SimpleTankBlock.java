package dev.technic4n.fasttransferlib.example.fluid;

import dev.technici4n.fasttransferlib.api.ItemInteractionContext;
import dev.technici4n.fasttransferlib.api.fluid.FluidApi;
import dev.technici4n.fasttransferlib.api.fluid.FluidConstants;
import dev.technici4n.fasttransferlib.api.fluid.FluidExtractable;
import dev.technici4n.fasttransferlib.api.fluid.FluidInsertable;
import dev.technici4n.fasttransferlib.api.fluid.FluidMovement;
import dev.technici4n.fasttransferlib.api.fluid.FluidView;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SimpleTankBlock extends Block implements BlockEntityProvider {
	public SimpleTankBlock(Settings settings) {
		super(settings);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new SimpleTankBlockEntity();
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) return ActionResult.CONSUME;
		FluidView view = FluidApi.SIDED_VIEW.get(world, pos, hit.getSide());
		FluidView itemView = FluidApi.ITEM_VIEW.get(player.getStackInHand(hand), ItemInteractionContext.of(player, hand));

		if (view instanceof FluidInsertable && view.getFluidSlotCount() >= 1 && itemView instanceof FluidExtractable) {
			FluidInsertable insertable = (FluidInsertable) view;
			FluidExtractable extractable = (FluidExtractable) itemView;
			FluidMovement.moveRange(extractable, insertable, FluidConstants.BUCKET * 10);
			System.out.printf("Tank Now At %d + %d/81 millibuckets %s%n", view.getFluidAmount(0) / 81, view.getFluidAmount(0) % 81, Registry.FLUID.getId(view.getFluid(0)).toString());
		}

		return ActionResult.CONSUME;
	}
}
