package dev.technic4n.fasttransferlib.example.fluid;

import dev.technici4n.fasttransferlib.api.transfer.Storage;

import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

public class FluidExample implements ModInitializer {
	public SimpleTankBlock SIMPLE_TANK_BLOCK = new SimpleTankBlock(FabricBlockSettings.of(Material.METAL).hardness(4.0f));
	public static BlockEntityType<SimpleTankBlockEntity> SIMPLE_TANK_BLOCK_ENTITY;

	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier("fasttransferlib-testmod", "yeet_stick"), new YeetStick(new Item.Settings()));
		SIMPLE_TANK_BLOCK = Registry.register(Registry.BLOCK, new Identifier("fasttransferlib-testmod", "simple_tank_block"), SIMPLE_TANK_BLOCK);
		SIMPLE_TANK_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "modid:demo", BlockEntityType.Builder.create(SimpleTankBlockEntity::new, SIMPLE_TANK_BLOCK).build(null));
		SimpleTankBlockEntity.init();
	}
}
