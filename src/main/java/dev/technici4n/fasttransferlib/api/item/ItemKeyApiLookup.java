package dev.technici4n.fasttransferlib.api.item;

import org.jetbrains.annotations.Nullable;

import net.minecraft.item.ItemConvertible;

public interface ItemKeyApiLookup<T, C> {
	@Nullable
	T get(ItemKey stack, C context);

	void register(ItemKeyApiProvider<T, C> provider, ItemConvertible... items);

	void registerFallback(ItemKeyApiProvider<T, C> provider);

	@FunctionalInterface
	interface ItemKeyApiProvider<T, C> {
		@Nullable
		T get(ItemKey itemKey, C context);
	}
}
