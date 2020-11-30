package dev.technic4n.fasttransferlib.example.fluid;

import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.fluid.FluidApi;
import dev.technici4n.fasttransferlib.api.fluid.FluidConstants;
import dev.technici4n.fasttransferlib.api.fluid.FluidExtractable;
import dev.technici4n.fasttransferlib.api.fluid.FluidView;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
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
                long extractedAmmount = extractable.extract(fluid, FluidConstants.BOTTLE * 2, Simulation.ACT);
                if (extractedAmmount > 0) {
                    System.out.printf("Extracted %d %s%n", extractedAmmount, Registry.FLUID.getId(fluid).toString());
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

}
