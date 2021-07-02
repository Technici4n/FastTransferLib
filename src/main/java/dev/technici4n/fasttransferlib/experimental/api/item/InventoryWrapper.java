package dev.technici4n.fasttransferlib.experimental.api.item;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;

public interface InventoryWrapper extends Storage<ItemVariant> {
	SingleSlotStorage<ItemVariant> getSlot(int index);
}
