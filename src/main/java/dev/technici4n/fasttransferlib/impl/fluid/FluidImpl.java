package dev.technici4n.fasttransferlib.impl.fluid;

import net.fabricmc.loader.api.FabricLoader;

import java.lang.reflect.Method;

public class FluidImpl {
    public static int version = 0;

    public static void loadLbaCompat() {
        if (FabricLoader.getInstance().isModLoaded("libblockattributes_fluids")) {
            try {
                Class<?> clazz = Class.forName("dev.technici4n.fasttransferlib.impl.fluid.compat.lba.LbaCompat");
                Method init = clazz.getMethod("init");
                init.invoke(null);
            } catch (Exception ex) {
                throw new RuntimeException("LBA was detected, but fluid compat loading failed", ex);
            }
        }
    }
}
