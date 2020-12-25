package dev.technici4n.fasttransferlib.api.transfer;

import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.impl.item.ItemImpl;

public interface ResourceIo<K extends ResourceKey> {
	int getSlotCount();
	K getResourceKey(int slot);
	long getAmount(int slot);
	default int getVersion() { return ItemImpl.version++; }
	default boolean supportsInsertion() { return false; }
	default long insert(K key, long amount, Simulation simulation) { return amount; }
	default boolean supportsExtraction() { return false; }
	default long extract(int slot, K key, long maxAmount, Simulation simulation) { return 0; }
	default long extract(K key, long maxAmount, Simulation simulation) {
		if (!supportsExtraction()) return 0;

		for (int i = 0; i < getSlotCount(); ++i) {
			long extracted = extract(i, key, maxAmount, simulation);

			if (extracted > 0) {
				return extracted;
			}
		}

		return 0;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	static <K extends ResourceKey> Class<ResourceIo<K>> asClass() {
		return (Class<ResourceIo<K>>)(Class) ResourceIo.class;
	}
}
