package dev.technici4n.fasttransferlib.experimental.impl.context;

import com.google.common.base.Preconditions;
import dev.technici4n.fasttransferlib.experimental.api.context.ContainerItemContext;
import dev.technici4n.fasttransferlib.experimental.api.item.ItemKey;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

public class StorageContainerItemContext implements ContainerItemContext {
	private final ItemKey boundKey;
	private final Storage<ItemKey> storage;

	public StorageContainerItemContext(ItemKey boundKey, Storage<ItemKey> storage) {
		this.boundKey = boundKey;
		this.storage = storage;
	}

	@Override
	public ItemKey getBoundKey() {
		return boundKey;
	}

	@Override
	public long getCount(Transaction tx) {
		try (Transaction nested = tx.openNested()) {
			return storage.extract(boundKey, Long.MAX_VALUE, nested);
		}
	}

	@Override
	public boolean transform(long count, ItemKey into, Transaction tx) {
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
