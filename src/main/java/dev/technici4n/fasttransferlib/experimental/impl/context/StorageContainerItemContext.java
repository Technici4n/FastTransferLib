package dev.technici4n.fasttransferlib.experimental.impl.context;

import com.google.common.base.Preconditions;
import dev.technici4n.fasttransferlib.experimental.api.context.ContainerItemContext;
import dev.technici4n.fasttransferlib.experimental.api.item.ItemVariant;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public class StorageContainerItemContext implements ContainerItemContext {
	private final ItemVariant boundKey;
	private final Storage<ItemVariant> storage;

	public StorageContainerItemContext(ItemVariant boundKey, Storage<ItemVariant> storage) {
		this.boundKey = boundKey;
		this.storage = storage;
	}

	@Override
	public ItemVariant getBoundKey() {
		return boundKey;
	}

	@Override
	public long getCount(TransactionContext tx) {
		try (Transaction nested = tx.openNested()) {
			return storage.extract(boundKey, Long.MAX_VALUE, nested);
		}
	}

	@Override
	public boolean transform(long count, ItemVariant into, TransactionContext tx) {
		Preconditions.checkArgument(count <= getCount(tx));

		try (Transaction nested = tx.openNested()) {
			if (storage.extract(boundKey, count, nested) != count) {
				throw new AssertionError("Bad implementation.");
			}

			if (storage.insert(into, count, nested) == count) {
				nested.commit();
				return true;
			}
		}

		return false;
	}
}
