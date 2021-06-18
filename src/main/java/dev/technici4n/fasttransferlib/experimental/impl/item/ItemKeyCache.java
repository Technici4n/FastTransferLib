package dev.technici4n.fasttransferlib.experimental.impl.item;

import dev.technici4n.fasttransferlib.experimental.api.item.ItemKey;

/**
 * Implemented by items to cache the ItemKey with a null tag inside the Item object directly.
 */
public interface ItemKeyCache {
	ItemKey ftl_getCachedItemKey();
}
