package dev.technici4n.fasttransferlib.experimental.api.item;

import com.google.common.base.Preconditions;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

/**
 * Preconditions for item transfer.
 */
public final class ItemPreconditions {
	public static void notEmpty(ItemVariant key) {
		if (key == null || key.isEmpty()) {
			throw new IllegalArgumentException("ItemVariant may not be empty or null.");
		}
	}

	public static void notEmpty(Item item) {
		if (item == null || item == Items.AIR) {
			throw new IllegalArgumentException("Item may not be empty or null.");
		}
	}

	public static void notEmptyNotNegative(ItemVariant key, long amount) {
		ItemPreconditions.notEmpty(key);
		Preconditions.checkArgument(amount >= 0);
	}

	private ItemPreconditions() {
	}
}
