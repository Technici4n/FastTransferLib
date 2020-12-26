package dev.technic4n.fasttransferlib.example.fluid;

import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.fluid.FluidApi;
import dev.technici4n.fasttransferlib.api.fluid.FluidConstants;
import dev.technici4n.fasttransferlib.api.fluid.FluidTextHelper;
import dev.technici4n.fasttransferlib.api.fluid.FluidIo;
import dev.technici4n.fasttransferlib.api.item.ItemApi;
import dev.technici4n.fasttransferlib.api.item.ItemIo;
import dev.technici4n.fasttransferlib.api.item.ItemKey;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.registry.Registry;

public class YeetStick extends Item {
	public YeetStick(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		FluidIo fluidIo = FluidApi.SIDED.get(context.getWorld(), context.getBlockPos(), context.getSide());

		if (fluidIo != null && !context.getWorld().isClient) {
			if (fluidIo.getFluidSlotCount() > 0 && fluidIo.getFluid(0) != Fluids.EMPTY) {
				Fluid fluid = fluidIo.getFluid(0);
				long extractedAmount = fluidIo.extract(fluid, 2 * FluidConstants.BOTTLE, Simulation.ACT);

				if (extractedAmount > 0) {
					context.getPlayer().sendMessage(new LiteralText(String.format("Extracted %s millibuckets of %s", FluidTextHelper.getUnicodeMillibuckets(extractedAmount, true), Registry.FLUID.getId(fluid).toString())), false);
					context.getPlayer().sendMessage(new LiteralText(String.format("Extracted %s millibuckets of %s", FluidTextHelper.getUnicodeMillibuckets(extractedAmount, false), Registry.FLUID.getId(fluid).toString())), false);
				}

				return ActionResult.SUCCESS;
			}
		}

		ItemIo itemIo = ItemApi.SIDED.get(context.getWorld(), context.getBlockPos(), context.getSide());

		if (itemIo != null && !context.getWorld().isClient) {
			if (itemIo.getItemSlotCount() > 0 && !itemIo.getItemKey(0).isEmpty()) {
				ItemKey key = itemIo.getItemKey(0);
				int extractedCount = itemIo.extract(key, 10, Simulation.ACT);

				if (extractedCount > 0) {
					context.getPlayer().sendMessage(new LiteralText(String.format("Extracted %d of %s", extractedCount, key)), false);
				}
			}
		}

		return ActionResult.PASS;
	}
}
