package dev.technici4n.fasttransferlib.impl.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;

@Mixin(HopperBlockEntity.class)
public interface HopperBlockEntityAccessor extends Inventory {
	@Invoker("setCooldown")
	void ftl_callSetCooldown(int cooldown);

	@Accessor("lastTickTime")
	long ftl_getLastTickTime();
}
