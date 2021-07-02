package dev.technici4n.fasttransferlib.impl.mixin;

import dev.technici4n.fasttransferlib.experimental.api.item.ItemVariant;
import dev.technici4n.fasttransferlib.experimental.impl.item.ItemVariantCache;
import dev.technici4n.fasttransferlib.experimental.impl.item.ItemVariantImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.item.Item;

/**
 * Cache the ItemVariant with a null tag inside each Item directly.
 */
@Mixin(Item.class)
public class ItemMixin implements ItemVariantCache {
	@Unique
	private final ItemVariant cachedItemVariant = new ItemVariantImpl((Item) (Object) this, null);

	@Override
	public ItemVariant ftl_getCachedItemVariant() {
		return cachedItemVariant;
	}
}
