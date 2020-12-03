package dev.technici4n.fasttransferlib.impl.fluid.compat.lba;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import dev.technici4n.fasttransferlib.api.fluid.FluidExtractable;
import dev.technici4n.fasttransferlib.impl.compat.LbaUtil;
import net.minecraft.fluid.Fluid;

import java.math.RoundingMode;

import static dev.technici4n.fasttransferlib.api.Simulation.ACT;
import static dev.technici4n.fasttransferlib.api.Simulation.SIMULATE;

class LbaExtractable implements alexiil.mc.lib.attributes.fluid.FluidExtractable {
    private final FluidExtractable extractable;

    LbaExtractable(FluidExtractable extractable) {
        this.extractable = extractable;
    }

    @Override
    public FluidVolume attemptExtraction(FluidFilter filter, FluidAmount maxAmount, Simulation simulation) {
        for (int i = 0; i < extractable.getFluidSlotCount(); ++i) {
            Fluid fluid = extractable.getFluid(i);
            FluidKey key = FluidKeys.get(fluid);
            if (!filter.matches(key)) continue;
            long extracted = extractable.extract(i, fluid, maxAmount.asLong(81000, RoundingMode.DOWN), LbaUtil.getSimulation(simulation));
            if (extracted > 0) {
                return key.withAmount(FluidAmount.of(extracted, 81000));
            }
        }
        return FluidVolumeUtil.EMPTY;
    }
}
