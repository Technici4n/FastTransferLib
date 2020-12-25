package dev.technic4n.fasttransferlib.example.fluid;

import dev.technici4n.fasttransferlib.api.ContainerItemContext;
import dev.technici4n.fasttransferlib.api.fluid.FluidApi;
import dev.technici4n.fasttransferlib.api.fluid.FluidConstants;
import dev.technici4n.fasttransferlib.api.fluid.FluidKey;
import dev.technici4n.fasttransferlib.api.fluid.FluidTextHelper;
import dev.technici4n.fasttransferlib.api.item.ItemKey;
import dev.technici4n.fasttransferlib.api.transfer.ResourceIo;
import dev.technici4n.fasttransferlib.api.transfer.ResourceMovement;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
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
		ResourceIo<FluidKey> view = FluidApi.SIDED.get(world, pos, hit.getSide());
		ResourceIo<FluidKey> itemView = FluidApi.ITEM.get(ItemKey.of(player.getStackInHand(hand)), ContainerItemContext.ofPlayerHand(player, hand));

		if (view != null && view.getSlotCount() >= 1 && itemView != null) {
			ResourceMovement.moveMultiple(itemView, view, FluidConstants.BUCKET * 10L);
			player.sendMessage(new LiteralText(String.format("Tank Now At %s millibuckets of %s", FluidTextHelper.getUnicodeMillibuckets(view.getAmount(0), true), Registry.FLUID.getId(view.getResourceKey(0).getFluid()).toString())), false);
			player.sendMessage(new LiteralText(String.format("Tank Now At %s millibuckets of %s", FluidTextHelper.getUnicodeMillibuckets(view.getAmount(0), false), Registry.FLUID.getId(view.getResourceKey(0).getFluid()).toString())), false);
		}

		return ActionResult.CONSUME;
	}
}
