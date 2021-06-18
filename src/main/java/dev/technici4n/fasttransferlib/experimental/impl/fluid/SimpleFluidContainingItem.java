package dev.technici4n.fasttransferlib.experimental.impl.fluid;

import java.util.Iterator;
import java.util.function.Function;

import dev.technici4n.fasttransferlib.experimental.api.context.ContainerItemContext;
import dev.technici4n.fasttransferlib.experimental.api.item.ItemKey;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidKey;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidPreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ExtractionOnlyStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleViewIterator;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

public class SimpleFluidContainingItem implements ExtractionOnlyStorage<FluidKey>, StorageView<FluidKey> {
	private final ContainerItemContext ctx;
	private final ItemKey sourceKey;
	private final FluidKey fluid;
	private final long amount;
	private final Function<ItemKey, ItemKey> keyMapping;

	public SimpleFluidContainingItem(ContainerItemContext ctx, ItemKey sourceKey, FluidKey fluid, long amount, Function<ItemKey, ItemKey> keyMapping) {
		this.ctx = ctx;
		this.sourceKey = sourceKey;
		this.fluid = fluid;
		this.amount = amount;
		this.keyMapping = keyMapping;
	}

	@Override
	public FluidKey resource() {
		return fluid;
	}

	@Override
	public long amount() {
		return amount;
	}

	@Override
	public long capacity() {
		return amount;
	}

	@Override
	public boolean isEmpty() {
		return resource().isEmpty();
	}

	@Override
	public long extract(FluidKey resource, long maxAmount, Transaction transaction) {
		FluidPreconditions.notEmptyNotNegative(resource, maxAmount);

		if (maxAmount >= amount && resource == fluid && ctx.getCount(transaction) > 0) {
			if (ctx.transform(1, keyMapping.apply(sourceKey), transaction)) {
				return amount;
			}
		}

		return 0;
	}

	@Override
	public Iterator<StorageView<FluidKey>> iterator(Transaction transaction) {
		return SingleViewIterator.create(this, transaction);
	}
}
