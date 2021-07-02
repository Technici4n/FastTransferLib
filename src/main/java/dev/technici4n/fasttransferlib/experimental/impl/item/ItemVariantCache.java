package dev.technici4n.fasttransferlib.experimental.impl.item;

import dev.technici4n.fasttransferlib.experimental.api.item.ItemVariant;

/**
 * Implemented by items to cache the ItemVariant with a null tag inside the Item object directly.
 */
public interface ItemVariantCache {
	ItemVariant ftl_getCachedItemVariant();
}
