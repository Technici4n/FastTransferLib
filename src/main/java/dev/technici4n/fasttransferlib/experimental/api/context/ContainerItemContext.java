package dev.technici4n.fasttransferlib.experimental.api.context;

import dev.technici4n.fasttransferlib.experimental.api.item.ItemKey;
import dev.technici4n.fasttransferlib.experimental.impl.context.PlayerEntityContainerItemContext;
import dev.technici4n.fasttransferlib.experimental.impl.context.StorageContainerItemContext;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Hand;

import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

/**
 * A context for interaction with item-provided apis, bound to a specific
 * ItemKey that must match that provided to {@link ItemApiLookup#get}.
 */
// TODO: rework this stuff, it's not great.
public interface ContainerItemContext {
	/**
	 * Return the item key this context is bound to.
	 */
	ItemKey getBoundKey();

	/**
	 * Get the current count. If the ItemKey is not present anymore, return 0
	 * instead.
	 */
	long getCount(Transaction transaction);

	/**
	 * Transform some of the bound items into another item key.
	 *
	 * @param count How much to transform, must be positive.
	 * @param into  The target item key. If empty, delete the items instead.
	 * @return How many items were successfully transformed.
	 * @throws RuntimeException If there aren't enough items to replace, that is if
	 *                          {@link ContainerItemContext#getCount
	 *                          this.getCount()} < count.
	 */
	// TODO: consider using an enum instead of a boolean? what about
	// TransactionResult?
	boolean transform(long count, ItemKey into, Transaction transaction);

	/**
	 * Try to find an API instance for the passed lookup and return it, or {@code null} if there is none.
	 *
	 * @see ItemApiLookup#find
	 */
	@Nullable
	default <A> A find(ItemApiLookup<A, ContainerItemContext> lookup) {
		return lookup.find(getBoundKey().toStack(), this);
	}

	static ContainerItemContext ofPlayerHand(PlayerEntity player, Hand hand) {
		return PlayerEntityContainerItemContext.ofHand(player, hand);
	}

	static ContainerItemContext ofPlayerCursor(PlayerEntity player, ScreenHandler screenHandler) {
		return PlayerEntityContainerItemContext.ofCursor(player, screenHandler);
	}

	static ContainerItemContext ofStorage(ItemKey boundKey, Storage<ItemKey> storage) {
		return new StorageContainerItemContext(boundKey, storage);
	}
}
