package dev.technici4n.fasttransferlib.api.transfer;

import dev.technici4n.fasttransferlib.base.AggregateStorage;
import dev.technici4n.fasttransferlib.impl.FtlImpl;

/**
 * An object that can store resources.
 * <p><ul>
 *     <li>See {@link AggregateStorage} for a wrapper around multiple {@code Storage}s.</li>
 * </ul></p>
 * @param <T> The type of the stored resources.
 */
public interface Storage<T> {
	default StorageFunction<T> insertionFunction() {
		return StorageFunction.empty();
	}
	default StorageFunction<T> extractionFunction() {
		return StorageFunction.empty();
	}

	// if true is returned, the visit was stopped
	boolean forEach(Visitor<T> visitor);

	/**
	 * The current version of the storage.
	 * It <em>must</em> change if the state of the storage has changed,
	 * but it may also change even if the state of the storage hasn't changed.
	 * <p>Note: It is not valid to call this during a transaction,
	 * and implementations are encouraged to throw an exception if that happens.</p>
	 */
	default int getVersion() {
		return FtlImpl.version++;
	}

	/**
	 * A storage visitor, for use with {@link #forEach}.
	 */
	@FunctionalInterface
	interface Visitor<T> {
		/**
		 * Read and modify the target view if necessary, and return whether to stop the visit.
		 * References to {@code StorageView}s should never be retained.
		 * @return True to stop the visit, false to keep visiting.
		 */
		boolean visit(StorageView<T> storageView);
	}
}
