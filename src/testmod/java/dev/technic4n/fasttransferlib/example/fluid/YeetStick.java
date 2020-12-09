package dev.technic4n.fasttransferlib.example.fluid;

import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.fluid.FluidApi;
import dev.technici4n.fasttransferlib.api.fluid.FluidConstants;
import dev.technici4n.fasttransferlib.api.fluid.FluidExtractable;
import dev.technici4n.fasttransferlib.api.fluid.FluidTextHelper;
import dev.technici4n.fasttransferlib.api.fluid.FluidView;

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
		FluidView fluidIO = FluidApi.SIDED_VIEW.get(context.getWorld(), context.getBlockPos(), context.getSide());

		if (fluidIO instanceof FluidExtractable && !context.getWorld().isClient) {
			FluidExtractable extractable = (FluidExtractable) fluidIO;

			if (extractable.getFluidSlotCount() > 0) {
				Fluid fluid = extractable.getFluid(0);
				long extractedAmount = extractable.extract(fluid, 2 * FluidConstants.BOTTLE, Simulation.ACT);

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
