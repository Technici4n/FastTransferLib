package dev.technici4n.fasttransferlib.experimental.impl.fluid;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

import dev.technici4n.fasttransferlib.experimental.api.context.ContainerItemContext;
import dev.technici4n.fasttransferlib.experimental.api.fluid.ItemFluidStorage;
import dev.technici4n.fasttransferlib.experimental.api.item.ItemVariant;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.InsertionOnlyStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

public class EmptyItemsRegistry {
	private static final Map<Item, EmptyItemProvider> PROVIDERS = new IdentityHashMap<>();

	public static synchronized void registerEmptyItem(Item emptyItem, FluidVariant fluid, long amount, Function<ItemVariant, ItemVariant> keyMapping) {
		PROVIDERS.computeIfAbsent(emptyItem, item -> {
			EmptyItemProvider provider = new EmptyItemProvider();
			ItemFluidStorage.ITEM.registerForItems(provider, emptyItem);
			return provider;
		});
		EmptyItemProvider provider = PROVIDERS.get(emptyItem);

		// We use a copy-on-write strategy to register the fluid filling if possible
		Map<FluidVariant, FillInfo> copy = new IdentityHashMap<>(provider.acceptedFluids);
		copy.putIfAbsent(fluid, new FillInfo(amount, keyMapping));
		provider.acceptedFluids = copy;
	}

	private static class EmptyItemProvider implements ItemApiLookup.ItemApiProvider<Storage<FluidVariant>, ContainerItemContext> {
		private volatile Map<FluidVariant, FillInfo> acceptedFluids = new IdentityHashMap<>();

		@Override
		public @Nullable Storage<FluidVariant> find(ItemStack stack, ContainerItemContext context) {
			return new EmptyItemStorage(ItemVariant.of(stack), context);
		}

		private class EmptyItemStorage implements InsertionOnlyStorage<FluidVariant> {
			private final ItemVariant initialKey;
			private final ContainerItemContext ctx;

			private EmptyItemStorage(ItemVariant initialKey, ContainerItemContext ctx) {
				this.initialKey = initialKey;
				this.ctx = ctx;
			}

			@Override
			public long insert(FluidVariant fluid, long maxAmount, Transaction transaction) {
				StoragePreconditions.notEmptyNotNegative(fluid, maxAmount);

				if (ctx.getCount(transaction) == 0) return 0;
				FillInfo fillInfo = acceptedFluids.get(fluid);
				if (fillInfo == null) return 0;

				if (maxAmount >= fillInfo.amount) {
					if (ctx.transform(1, fillInfo.keyMapping.apply(initialKey), transaction)) {
						return fillInfo.amount;
					}
				}

				return 0;
			}

			@Override
			public Iterator<StorageView<FluidVariant>> iterator(Transaction transaction) {
				return Collections.emptyIterator();
			}
		}
	}

	private static class FillInfo {
		private final long amount;
		private final Function<ItemVariant, ItemVariant> keyMapping;

		private FillInfo(long amount, Function<ItemVariant, ItemVariant> keyMapping) {
			this.amount = amount;
			this.keyMapping = keyMapping;
		}
	}
}
