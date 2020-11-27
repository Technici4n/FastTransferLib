package dev.technici4n.fasttransferlib.api.item;

import dev.technici4n.fasttransferlib.api.Simulation;
import net.minecraft.item.ItemStack;

/**
 * Utilities related to moving items from an {@link ItemExtractable} to another {@link ItemInsertable}.
 */
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
            // Try to extract once
            ItemStack filter = from.extract(i, from.getStack(i), Simulation.SIMULATE);
            if (filter.isEmpty()) continue;

            // Try to insert
            filter = filter.copy();
            filter.setCount(Math.min(filter.getCount(), maxCount));
            ItemStack leftover = to.insert(filter.copy(), Simulation.SIMULATE);
            int moved = filter.getCount() - leftover.getCount();
            if (moved <= 0) continue;
            filter.setCount(moved);

            // Try to extract again with the new count
            ItemStack extracted = from.extract(i, filter, Simulation.SIMULATE);
            if (extracted.getCount() != moved) continue;

            // Move the items at last
            extracted = from.extract(i, filter, Simulation.ACT);
            // TODO: if extracted.getCount() != moved, throw
            leftover = to.insert(extracted, Simulation.ACT);
            // TODO: if !leftover.isEmpty(), throw

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
