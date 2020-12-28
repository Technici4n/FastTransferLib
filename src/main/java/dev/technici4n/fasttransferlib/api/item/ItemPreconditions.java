package dev.technici4n.fasttransferlib.api.item;

public class ItemPreconditions {
	public static void notEmpty(ItemKey key) {
		if (key.isEmpty()) {
			throw new IllegalArgumentException("ItemKey may not be empty.");
		}
	}
}
