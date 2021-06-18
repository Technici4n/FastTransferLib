package dev.technici4n.fasttransferlib.experimental.api.storage;

import java.util.Iterator;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleViewIterator;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

/**
 * A storage that is also its only storage view.
 * It can be used in APIs for storages that are wrappers around a single "slot", or for slightly more convenient implementation.
 *
 * @param <T> The type of the stored resource.
 */
public interface SingleSlotStorage<T> extends Storage<T>, StorageView<T> {
	@Override
	default Iterator<StorageView<T>> iterator(Transaction transaction) {
		return SingleViewIterator.create(this, transaction);
	}
}
