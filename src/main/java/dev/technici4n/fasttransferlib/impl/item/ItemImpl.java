package dev.technici4n.fasttransferlib.impl.item;

import net.fabricmc.loader.api.FabricLoader;

import java.lang.reflect.Method;

public class ItemImpl {
    public static int version = 0;

    public static void loadLbaCompat() {
        if (FabricLoader.getInstance().isModLoaded("libblockattributes_items")) {
            try {
                Class<?> clazz = Class.forName("dev.technici4n.fasttransferlib.impl.item.compat.lba.LbaCompat");
                Method init = clazz.getMethod("init");
                init.invoke(null);
            } catch (Exception ex) {
                throw new RuntimeException("LBA was detected, but item compat loading failed", ex);
            }
        }
    }
}
