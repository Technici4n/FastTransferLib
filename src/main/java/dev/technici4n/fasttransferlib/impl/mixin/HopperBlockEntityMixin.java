package dev.technici4n.fasttransferlib.impl.mixin;

import dev.technici4n.fasttransferlib.experimental.api.item.InventoryWrappers;
import dev.technici4n.fasttransferlib.experimental.api.item.ItemVariant;
import dev.technici4n.fasttransferlib.experimental.api.item.ItemStorage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;

@Mixin(HopperBlockEntity.class)
public class HopperBlockEntityMixin {
	@Inject(
			at = @At("HEAD"),
			method = "insert(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/inventory/Inventory;)Z",
			cancellable = true
	)
	private static void hookInsert(World world, BlockPos pos, BlockState state, Inventory inventory, CallbackInfoReturnable<Boolean> cir) {
		Direction direction = state.get(HopperBlock.FACING);
		BlockPos targetPos = pos.offset(direction);
		BlockEntity targetBe = world.getBlockEntity(targetPos);
		Storage<ItemVariant> target = ItemStorage.SIDED.find(world, targetPos, null, targetBe, direction.getOpposite());

		if (target != null) {
			cir.setReturnValue(doTransfer(InventoryWrappers.of(inventory, direction), target, inventory, targetBe));
		}
	}

	@Inject(
			at = @At("HEAD"),
			method = "extract(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;)Z",
			cancellable = true
	)
	private static void hookExtract(World world, Hopper hopper, CallbackInfoReturnable<Boolean> cir) {
		BlockPos sourcePos = new BlockPos(hopper.getHopperX(), hopper.getHopperY() + 1.0D, hopper.getHopperZ());
		BlockEntity sourceBe = world.getBlockEntity(sourcePos);
		Storage<ItemVariant> source = ItemStorage.SIDED.find(world, sourcePos, null, sourceBe, Direction.DOWN);

		if (source != null) {
			cir.setReturnValue(doTransfer(source, InventoryWrappers.of(hopper, Direction.UP), sourceBe, hopper));
		}
	}

	private static boolean doTransfer(Storage<ItemVariant> from, Storage<ItemVariant> to, @Nullable Object invFrom, @Nullable Object invTo) {
		if (invFrom instanceof HopperBlockEntityAccessor hopperFrom && invTo instanceof HopperBlockEntityAccessor hopperTo) {
			// Hoppers have some special interactions (see HopperBlockEntity#transfer)
			boolean wasEmpty = hopperTo.isEmpty();
			boolean moved = StorageUtil.move(from, to, k -> true, 1, null) == 1;

			if (moved && wasEmpty && hopperTo.ftl_getLastTickTime() >= hopperFrom.ftl_getLastTickTime()) {
				hopperTo.ftl_callSetCooldown(7);
			}

			return moved;
		} else {
			return StorageUtil.move(from, to, k -> true, 1, null) == 1;
		}
	}
}
