package dev.technici4n.fasttransferlib.api.item;

import java.util.Objects;

import dev.technici4n.fasttransferlib.impl.item.ItemKeyApiLookupRegistryImpl;

import net.minecraft.util.Identifier;

public final class ItemKeyApiLookupRegistry {
	public static <T, C> ItemKeyApiLookup<T, C> getLookup(Identifier lookupId, Class<T> apiClass, Class<C> contextClass) {
		Objects.requireNonNull(apiClass, "Id of API cannot be null");
		Objects.requireNonNull(contextClass, "Context key cannot be null");

		return ItemKeyApiLookupRegistryImpl.getLookup(lookupId, apiClass, contextClass);
	}

	private ItemKeyApiLookupRegistry() {
	}
}
