package dev.technic4n.fasttransferlib.example.fluid;

import dev.technici4n.fasttransferlib.api.ContainerItemContext;
import dev.technici4n.fasttransferlib.api.fluid.FluidApi;
import dev.technici4n.fasttransferlib.api.fluid.FluidConstants;
import dev.technici4n.fasttransferlib.api.fluid.FluidMovement;
import dev.technici4n.fasttransferlib.api.fluid.FluidTextHelper;
import dev.technici4n.fasttransferlib.api.item.ItemKey;
import dev.technici4n.fasttransferlib.api.fluid.FluidIo;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.text.LiteralText;
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
		FluidIo view = FluidApi.SIDED.get(world, pos, hit.getSide());
		FluidIo itemView = FluidApi.ITEM.get(ItemKey.of(player.getStackInHand(hand)), ContainerItemContext.ofPlayerHand(player, hand));

		if (view != null && view.supportsFluidExtraction() && itemView != null) {
			FluidMovement.move(itemView, view, FluidConstants.BUCKET * 10);
			Fluid[] fluids = view.getFluids();
			long[] amounts = view.getFluidAmounts();

			if (fluids.length > 0 && amounts.length > 0) {
				player.sendMessage(new LiteralText(String.format("Tank Now At %s millibuckets of %s", FluidTextHelper.getUnicodeMillibuckets(amounts[0], true), Registry.FLUID.getId(fluids[0]).toString())), false);
				player.sendMessage(new LiteralText(String.format("Tank Now At %s millibuckets of %s", FluidTextHelper.getUnicodeMillibuckets(amounts[0], false), Registry.FLUID.getId(fluids[0]).toString())), false);
			}

			return ActionResult.CONSUME;
		}

		return ActionResult.PASS;
	}
}
