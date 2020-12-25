package dev.technici4n.fasttransferlib.impl.mixin;

import dev.technici4n.fasttransferlib.api.fluid.FluidKey;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.fluid.Fluid;

@Mixin(Fluid.class)
public class FluidMixin implements FluidKey {
	@Override
	@SuppressWarnings("all")
	public Fluid getFluid() {
		return (Fluid)(Object) this;
	}
}
