package dev.technici4n.fasttransferlib.impl.item;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import dev.technici4n.fasttransferlib.api.item.ItemKey;
import dev.technici4n.fasttransferlib.api.item.ItemKeyApiLookup;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.lookup.v1.custom.ApiProviderMap;

public class ItemKeyApiLookupImpl<A, C> implements ItemKeyApiLookup<A, C> {
	private static final Logger LOGGER = LogManager.getLogger();
	private final ApiProviderMap<Item, ItemKeyApiProvider<A, C>> providerMap = ApiProviderMap.create();
	private final List<ItemKeyApiProvider<A, C>> fallbackProviders = new CopyOnWriteArrayList<>();

	@SuppressWarnings("unused")
	public ItemKeyApiLookupImpl(Class<?> apiClass, Class<?> contextClass) {
	}

	@Nullable
	@Override
	public A get(ItemKey itemKey, C context) {
		Objects.requireNonNull(itemKey, "ItemKey cannot be null");
		// Providers have the final say whether a null context is allowed.

		@Nullable
		final ItemKeyApiProvider<A, C> provider = providerMap.get(itemKey.getItem());

		if (provider != null) {
			A instance = provider.get(itemKey, context);

			if (instance != null) {
				return instance;
			}
		}

		for (ItemKeyApiProvider<A, C> fallbackProvider : fallbackProviders) {
			A instance = fallbackProvider.get(itemKey, context);

			if (instance != null) {
				return instance;
			}
		}

		return null;
	}

	@Override
	public void register(ItemKeyApiProvider<A, C> provider, ItemConvertible... items) {
		Objects.requireNonNull(provider, "ItemApiProvider cannot be null");

		for (ItemConvertible item : items) {
			Objects.requireNonNull(item, "Passed item convertible cannot be null");
			Objects.requireNonNull(item.asItem(), "Item convertible in item form cannot be null: " + item.toString());

			if (providerMap.putIfAbsent(item.asItem(), provider) != null) {
				LOGGER.warn("Encountered duplicate API provider registration for item: " + Registry.ITEM.getId(item.asItem()));
			}
		}
	}

	@Override
	public void registerFallback(ItemKeyApiProvider<A, C> provider) {
		Objects.requireNonNull(provider, "ItemApiProvider cannot be null");

		fallbackProviders.add(provider);
	}
}
