package dev.technici4n.fasttransferlib.impl.fluid.compat.lba;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import dev.technici4n.fasttransferlib.api.fluid.FluidInsertable;
import net.minecraft.fluid.Fluid;

import java.math.RoundingMode;

import static dev.technici4n.fasttransferlib.api.Simulation.ACT;
import static dev.technici4n.fasttransferlib.api.Simulation.SIMULATE;

class LbaInsertable implements alexiil.mc.lib.attributes.fluid.FluidInsertable {
    private final FluidInsertable insertable;

    LbaInsertable(FluidInsertable insertable) {
        this.insertable = insertable;
    }

    @Override
    public FluidVolume attemptInsertion(FluidVolume fluidVolume, Simulation simulation) {
        Fluid fluid = fluidVolume.getFluidKey().getRawFluid();
        if (fluid == null) {
            return fluidVolume;
        }
        long inserted = fluidVolume.getAmount_F().asLong(81000, RoundingMode.DOWN);
        inserted -= insertable.insert(fluid, inserted, simulation.isAction() ? ACT : SIMULATE);
        return fluidVolume.getFluidKey().withAmount(fluidVolume.getAmount_F().sub(FluidAmount.of(inserted, 81000)));
    }
}
