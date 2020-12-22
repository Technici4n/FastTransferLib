package dev.technici4n.fasttransferlib.impl.item;

import dev.technici4n.fasttransferlib.api.item.ItemKeyApiLookup;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.lookup.v1.ApiLookupMap;

public final class ItemKeyApiLookupRegistryImpl {
	private static final ApiLookupMap<ItemKeyApiLookupImpl<?, ?>> PROVIDERS = ApiLookupMap.create(ItemKeyApiLookupImpl::new);

	@SuppressWarnings("unchecked")
	public static <T, C> ItemKeyApiLookup<T, C> getLookup(Identifier lookupId, Class<T> apiClass, Class<C> contextClass) {
		return (ItemKeyApiLookup<T, C>) PROVIDERS.getLookup(lookupId, apiClass, contextClass);
	}

	private ItemKeyApiLookupRegistryImpl() {
	}
}
