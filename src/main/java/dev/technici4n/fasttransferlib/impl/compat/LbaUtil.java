package dev.technici4n.fasttransferlib.impl.compat;

import dev.technici4n.fasttransferlib.api.Simulation;

public class LbaUtil {
    public static Simulation getSimulation(alexiil.mc.lib.attributes.Simulation simulation) {
        return simulation.isAction() ? Simulation.ACT : Simulation.SIMULATE;
    }

    public static alexiil.mc.lib.attributes.Simulation getSimulation(Simulation simulation) {
        return simulation.isActing() ? alexiil.mc.lib.attributes.Simulation.ACTION : alexiil.mc.lib.attributes.Simulation.SIMULATE;
    }
}
