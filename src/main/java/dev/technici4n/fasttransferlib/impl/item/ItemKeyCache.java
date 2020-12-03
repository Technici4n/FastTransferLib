package dev.technici4n.fasttransferlib.impl.item;

import dev.technici4n.fasttransferlib.api.item.ItemKey;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundTag;

class ItemKeyCache {
	static ItemKey get(Item item, @Nullable CompoundTag tag) {
		// TODO: actually cache things
		return new ItemKeyImpl(item, tag);
	}
}
