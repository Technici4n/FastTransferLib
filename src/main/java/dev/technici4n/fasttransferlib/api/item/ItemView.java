package dev.technici4n.fasttransferlib.api.item;

import dev.technici4n.fasttransferlib.impl.item.ItemImpl;
import net.minecraft.item.ItemStack;

/**
 * A view of an item inventory.
 *
 * @see ItemInsertable
 * @see ItemExtractable
 */
public interface ItemView {
    /**
     * Return the number of slots in the view.
     */
    int getItemSlotCount();

    /**
     * Return the stack in some slot. Note that the stack may have an arbitrary count. It is possible that the returned stack may change
     *
     * @param slot The slot id, must be between 0 and {@link ItemView#getItemSlotCount()}.
     * @throws IndexOutOfBoundsException if the slot is not in the range [0, {@link ItemView#getItemSlotCount()}).
     * @apiNote <b>THIS IS AN ITEM VIEW. DO NOT EVER MODIFY THE RETURNED STACK.</b>
     * You should {@linkplain ItemStack#copy() copy the item stack} if you wish to mutate a copy of the stack.
     */
    ItemStack getStack(int slot);

    /**
     * Return the version of this inventory. If this number is the same for two calls, it is expected
     * that the underlying inventory hasn't changed. There is however no guarantee that the inventory has changed if this number has changed.
     */
    default int getVersion() {
        return ItemImpl.version++;
    }
}
