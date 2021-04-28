package dev.technici4n.fasttransferlib.impl.energy.compat;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import dev.technici4n.fasttransferlib.api.energy.EnergyApi;
import dev.technici4n.fasttransferlib.impl.mixin.EnergyAccess;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.EnergyHandler;
import team.reborn.energy.EnergyStorage;

public class TrEnergyCompat {
	public static void init() {
		// initialize static
	}

	@Nullable
	private static EnergyHandler getHandlerFast(Object object) {
		if (object == null) return null;

		for (Map.Entry<Predicate<Object>, Function<Object, EnergyStorage>> holder : EnergyAccess.getHolders().entrySet()) {
			if (holder.getKey().test(object)) {
				return createEnergyHandler(holder.getValue().apply(object));
			}
		}

		return null;
	}

	private static final Constructor<EnergyHandler> handlerConstructor;

	private static EnergyHandler createEnergyHandler(EnergyStorage storage) {
		try {
			return handlerConstructor.newInstance(storage);
		} catch (Exception e) {
			throw new RuntimeException("Failed to create EnergyHandler in FTL compat", e);
		}
	}

	static {
		// Block compat
		EnergyApi.SIDED.registerFallback((world, pos, state, blockEntity, direction) -> {
			@Nullable
			EnergyHandler handler = getHandlerFast(blockEntity);

			if (handler != null) {
				return new TrWrappedEnergyHandler(handler, direction);
			}

			return null;
		});

		// Item compat
		EnergyApi.ITEM.registerFallback((itemStack, void_) -> {
			@Nullable
			EnergyHandler handler = getHandlerFast(itemStack);

			if (handler != null) {
				return new TrWrappedEnergyHandler(handler, null);
			}

			return null;
		});

		try {
			handlerConstructor = EnergyHandler.class.getDeclaredConstructor(EnergyStorage.class);
			handlerConstructor.setAccessible(true);
		} catch (Exception e) {
			throw new RuntimeException("Failed to load TR compat for FTL", e);
		}
	}
}
