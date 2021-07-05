package dev.technici4n.fasttransferlib.experimental.impl.fluid;

import java.util.Iterator;
import java.util.function.Function;

import dev.technici4n.fasttransferlib.experimental.api.context.ContainerItemContext;
import dev.technici4n.fasttransferlib.experimental.api.item.ItemVariant;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ExtractionOnlyStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleViewIterator;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public class SimpleFluidContainingItem implements ExtractionOnlyStorage<FluidVariant>, StorageView<FluidVariant> {
	private final ContainerItemContext ctx;
	private final ItemVariant sourceKey;
	private final FluidVariant fluid;
	private final long amount;
	private final Function<ItemVariant, ItemVariant> keyMapping;

	public SimpleFluidContainingItem(ContainerItemContext ctx, ItemVariant sourceKey, FluidVariant fluid, long amount, Function<ItemVariant, ItemVariant> keyMapping) {
		this.ctx = ctx;
		this.sourceKey = sourceKey;
		this.fluid = fluid;
		this.amount = amount;
		this.keyMapping = keyMapping;
	}

	@Override
	public FluidVariant getResource() {
		return fluid;
	}

	@Override
	public long getAmount() {
		return amount;
	}

	@Override
	public long getCapacity() {
		return amount;
	}

	@Override
	public boolean isResourceBlank() {
		return getResource().isBlank();
	}

	@Override
	public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notBlankNotNegative(resource, maxAmount);

		if (maxAmount >= amount && resource == fluid && ctx.getCount(transaction) > 0) {
			if (ctx.transform(1, keyMapping.apply(sourceKey), transaction)) {
				return amount;
			}
		}

		return 0;
	}

	@Override
	public Iterator<StorageView<FluidVariant>> iterator(TransactionContext transaction) {
		return SingleViewIterator.create(this, transaction);
	}
}
