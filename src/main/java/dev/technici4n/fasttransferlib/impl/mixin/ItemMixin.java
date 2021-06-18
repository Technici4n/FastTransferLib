package dev.technici4n.fasttransferlib.impl.mixin;

import dev.technici4n.fasttransferlib.experimental.api.item.ItemKey;
import dev.technici4n.fasttransferlib.experimental.impl.item.ItemKeyCache;
import dev.technici4n.fasttransferlib.experimental.impl.item.ItemKeyImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.item.Item;

/**
 * Cache the ItemKey with a null tag inside each Item directly.
 */
@Mixin(Item.class)
public class ItemMixin implements ItemKeyCache {
	@Unique
	private final ItemKey cachedItemKey = new ItemKeyImpl((Item) (Object) this, null);

	@Override
	public ItemKey ftl_getCachedItemKey() {
		return cachedItemKey;
	}
}
