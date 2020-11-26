package dev.technici4n.fasttransferlib.api.item;

import dev.technici4n.fasttransferlib.api.Simulation;
import net.minecraft.item.ItemStack;

/**
 * An item inventory that supports extracting items.
 */
public interface ItemExtractable extends ItemView {
    /**
     * Extract some items from this extractable, with the same semantics as {@link ItemExtractable#extract(ItemStack, Simulation) the slotless variant}.
     * The slot parameter, as long as it is in range, can be anything.
     * It is however expected that calling this in a loop will be faster for callers that need to move a lot of items, with the following snippet for example:
     * <pre>
     * for(int i = 0; i < extractable.getSlotCount(); i++) {
     *     ItemStack extractedStack = extractable.extract(i, extractable.getStack(i), Simulation.ACT);
     *     // use the extracted slot
     * }
     * </pre>
     * @param slot The slot id, must be between 0 and {@link ItemView#getSlotCount()}.
     * @param stack The filter for the stack to extract, and the number of items to extract at most.
     * @param simulation If {@link Simulation#SIMULATE}, do not mutate the insertable
     * @return The extracted stack
     */
    ItemStack extract(int slot, ItemStack stack, Simulation simulation);

    /**
     * Extract some items from this extractable, matching the passed stack ignoring the count, and at most the count of the stack.
     * <p>If simulation is {@link Simulation#SIMULATE}, the result of the operation must be returned, but the underlying state of the item extractable must not change.
     * <p>The passed stack must never be stored or mutated by this function.
     * The returned stack is given to the caller in the sense that it will not be used by the item extractable.
     * <p><b>It is possible that the passed stack is one of the stacks of the inventory. This should be taken into account by the implementation.
     * @param stack The filter for the stack to extract, and the number of items to extract at most.
     * @param simulation If {@link Simulation#SIMULATE}, do not mutate the insertable
     * @return The extracted stack
     */
    default ItemStack extract(ItemStack stack, Simulation simulation) {
        for(int i = 0; i < getSlotCount(); ++i) {
            ItemStack extracted = extract(i, stack, simulation);
            if (!extracted.isEmpty()) {
                return extracted;
            }
        }
        return ItemStack.EMPTY;
    }
}
