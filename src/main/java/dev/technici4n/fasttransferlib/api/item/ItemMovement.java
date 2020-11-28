package dev.technici4n.fasttransferlib.api.item;

import dev.technici4n.fasttransferlib.api.Simulation;
import net.minecraft.item.ItemStack;

/**
 * Utilities related to moving items from an {@link ItemExtractable} to another {@link ItemInsertable}.
 */
// TODO: add a Predicate<ItemKey> parameter
public final class ItemMovement {
    /**
     * Move some items from some slots of an {@link ItemExtractable} to an {@link ItemInsertable}.
     *
     * @param from the extractable to move items from
     * @param to the insertable to move the items to
     * @param maxCount The maximum number of items to move.
     * @param startSlot The first slot of the range to move, inclusive.
     * @param endSlot The last slot of the range to move, exclusive.
     * @return The number of items that were moved.
     * @throws IndexOutOfBoundsException if the start or end slot is out of bounds of the {@link ItemExtractable}'s slot count.
     */
    public static int moveRange(ItemExtractable from, ItemInsertable to, int maxCount, int startSlot, int endSlot) {
        int totalMoved = 0;
        for (int i = startSlot; i < endSlot && maxCount > 0; ++i) {
            // Try to extract the maximum count
            ItemKey key = from.getItemKey(i);
            int moved = from.extract(i, key, maxCount, Simulation.SIMULATE);
            if (moved <= 0) continue;

            // Try to insert the maximum count that can be extracted
            moved -= to.insert(key, moved, Simulation.SIMULATE);
            if (moved <= 0) continue;

            // Try to extract again with the maximum count for the insertable, and return if that doesn't match
            if (from.extract(i, key, moved, Simulation.SIMULATE) != moved) continue;

            // Move the items at last
            if (from.extract(i, key, moved, Simulation.ACT) != moved) {
                // TODO: throw
            }
            if (to.insert(key, moved, Simulation.ACT) != 0) {
                // TODO: throw
            }

            totalMoved += moved;
            maxCount -= moved;
        }
        return totalMoved;
    }

    /**
     * Move some items from an {@link ItemExtractable} to an {@link ItemInsertable}.
     *
     * @param from the extractable to move items from
     * @param to the insertable to move the items to
     * @param maxCount The maximum number of items to move.
     * @return The number of items that were moved.
     */
    public static int moveMultiple(ItemExtractable from, ItemInsertable to, int maxCount) {
        return moveRange(from, to, maxCount, 0, from.getItemSlotCount());
    }

    private ItemMovement() {
    }
}
