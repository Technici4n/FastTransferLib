package dev.technic4n.fasttransferlib.example.fluid;

import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FluidExample implements ModInitializer {

	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier("e", "yeet_stick"), new YeetStick(new Item.Settings()));
	}
    
}
