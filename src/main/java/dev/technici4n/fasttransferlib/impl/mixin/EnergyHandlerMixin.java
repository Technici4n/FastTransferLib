package dev.technici4n.fasttransferlib.impl.mixin;

import static dev.technici4n.fasttransferlib.api.Simulation.ACT;
import static dev.technici4n.fasttransferlib.api.Simulation.SIMULATE;

import dev.technici4n.fasttransferlib.api.energy.EnergyIo;
import dev.technici4n.fasttransferlib.impl.energy.compat.ftl_from_tr.EnergyIoWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import team.reborn.energy.EnergyHandler;
import team.reborn.energy.EnergySide;
import team.reborn.energy.EnergyStorage;

@Mixin(value = EnergyHandler.class, remap = false)
public abstract class EnergyHandlerMixin {
	@Shadow
	private EnergyStorage holder;
	@Shadow
	private boolean simulate;
	@Shadow
	private EnergySide side;

	@Inject(at = @At("HEAD"), method = "extract", cancellable = true)
	public void hookExtract(double amount, CallbackInfoReturnable<Double> cir) {
		if (holder instanceof EnergyIoWrapper) {
			EnergyIoWrapper wrapper = (EnergyIoWrapper) holder;
			double extracted = wrapper.getIo(side).extract(amount, simulate ? SIMULATE : ACT);
			cir.setReturnValue(extracted);
		}
	}

	@Inject(at = @At("HEAD"), method = "insert", cancellable = true)
	public void hookInsert(double amount, CallbackInfoReturnable<Double> cir) {
		if (holder instanceof EnergyIoWrapper) {
			EnergyIoWrapper wrapper = (EnergyIoWrapper) holder;
			double leftover = wrapper.getIo(side).insert(amount, simulate ? SIMULATE : ACT);
			cir.setReturnValue(amount - leftover);
		}
	}

	@Inject(at = @At("HEAD"), method = "set")
	public void hookSet(double amount, CallbackInfo ci) {
		if (holder instanceof EnergyIoWrapper) {
			throw new UnsupportedOperationException();
		}
	}

	@Inject(at = @At("HEAD"), method = "getMaxInput", cancellable = true)
	public void hookGetMaxInput(CallbackInfoReturnable<Double> cir) {
		if (holder instanceof EnergyIoWrapper) {
			EnergyIoWrapper wrapper = (EnergyIoWrapper) holder;
			double maxInjected = 1e9;
			double leftover = wrapper.getIo(side).insert(maxInjected, SIMULATE);
			cir.setReturnValue(maxInjected - leftover);
		}
	}

	@Inject(at = @At("HEAD"), method = "getMaxOutput", cancellable = true)
	public void hookGetMaxOutput(CallbackInfoReturnable<Double> cir) {
		if (holder instanceof EnergyIoWrapper) {
			EnergyIoWrapper wrapper = (EnergyIoWrapper) holder;
			double extracted = wrapper.getIo(side).extract(wrapper.getIo(side).getEnergy(), SIMULATE);
			cir.setReturnValue(extracted);
		}
	}

	@Shadow
	public abstract double getMaxInput();

	@Inject(at = @At("HEAD"), method = "getMaxStored", cancellable = true)
	public void hookGetMaxStored(CallbackInfoReturnable<Double> cir) {
		if (holder instanceof EnergyIoWrapper) {
			EnergyIoWrapper wrapper = (EnergyIoWrapper) holder;
			EnergyIo io = wrapper.getIo(side);

			if (io.getEnergyCapacity() > 0) {
				cir.setReturnValue(io.getEnergyCapacity());
			} else {
				cir.setReturnValue(io.getEnergy() + getMaxInput());
			}
		}
	}

	@Inject(at = @At("HEAD"), method = "use", cancellable = true)
	public void hookUse(double amount, CallbackInfoReturnable<Boolean> cir) {
		if (holder instanceof EnergyIoWrapper) {
			EnergyIoWrapper wrapper = (EnergyIoWrapper) holder;
			EnergyIo io = wrapper.getIo(side);

			boolean canUse = io.extract(amount, SIMULATE) == amount;

			if (canUse && !simulate) {
				io.extract(amount, ACT);
				// TODO: check that it returns 0?
			}

			cir.setReturnValue(canUse);
		}
	}
}
