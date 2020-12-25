package dev.technic4n.fasttransferlib.example.fluid;

import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.fluid.FluidApi;
import dev.technici4n.fasttransferlib.api.fluid.FluidConstants;
import dev.technici4n.fasttransferlib.api.fluid.FluidKey;
import dev.technici4n.fasttransferlib.api.fluid.FluidTextHelper;
import dev.technici4n.fasttransferlib.api.transfer.ResourceIo;

import net.minecraft.fluid.Fluid;
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
		ResourceIo<FluidKey> fluidIo = FluidApi.SIDED.get(context.getWorld(), context.getBlockPos(), context.getSide());

		if (fluidIo != null && !context.getWorld().isClient) {
			if (fluidIo.getSlotCount() > 0) {
				Fluid fluid = fluidIo.getResourceKey(0).getFluid();
				long extractedAmount = fluidIo.extract(FluidKey.of(fluid), 2 * FluidConstants.BOTTLE, Simulation.ACT);

				if (extractedAmount > 0) {
					context.getPlayer().sendMessage(new LiteralText(String.format("Extracted %s millibuckets of %s", FluidTextHelper.getUnicodeMillibuckets(extractedAmount, true), Registry.FLUID.getId(fluid).toString())), false);
					context.getPlayer().sendMessage(new LiteralText(String.format("Extracted %s millibuckets of %s", FluidTextHelper.getUnicodeMillibuckets(extractedAmount, false), Registry.FLUID.getId(fluid).toString())), false);
				}

				return ActionResult.SUCCESS;
			}
		}

		return ActionResult.PASS;
	}
}
