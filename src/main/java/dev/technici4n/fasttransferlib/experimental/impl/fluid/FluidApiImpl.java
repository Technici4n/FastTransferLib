package dev.technici4n.fasttransferlib.experimental.impl.fluid;

import dev.technici4n.fasttransferlib.experimental.api.fluid.ItemFluidStorage;
import dev.technici4n.fasttransferlib.impl.mixin.BucketItemAccessor;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;

public class FluidApiImpl {
	public static void init() {
		// load static, called by the mod initializer
	}

	private static void onFluidRegistered(Fluid fluid) {
		if (fluid == null) return;
		Item item = fluid.getBucketItem();

		if (item instanceof BucketItem) {
			BucketItem bucketItem = (BucketItem) item;
			Fluid bucketFluid = ((BucketItemAccessor) bucketItem).ftl_getFluid();

			if (fluid == bucketFluid) {
				ItemFluidStorage.registerEmptyAndFullItems(Items.BUCKET, FluidVariant.of(fluid), FluidConstants.BUCKET, bucketItem);
			}
		}
	}

	static {
		// register bucket compat
		Registry.FLUID.forEach(FluidApiImpl::onFluidRegistered);
		RegistryEntryAddedCallback.event(Registry.FLUID).register((rawId, id, fluid) -> onFluidRegistered(fluid));
	}
}
