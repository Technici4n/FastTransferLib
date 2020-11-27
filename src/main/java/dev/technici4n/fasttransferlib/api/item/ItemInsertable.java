package dev.technici4n.fasttransferlib.api.item;

import dev.technici4n.fasttransferlib.api.Simulation;
import net.minecraft.item.ItemStack;

/**
 * An item inventory that supports inserting items.
 *
 * @see ItemExtractable
 */
public interface ItemInsertable extends ItemView {
    /**
     * Insert a stack into this inventory, and return the leftover stack.
     * Distribution is left entirely to the implementor.
     * <p>If simulation is {@link Simulation#SIMULATE}, the result of the operation must be returned, but the underlying state of the item insertable must not change.
     * <p>In all cases, the passed stack is given to the item insertable, in the sense that it must not be used afterwards in any circumstances.
     * The leftover stack may or may not be the inserted stack, and it is given to the caller in the sense that it will not be used by the item insertable.
     *
     * @param insertedStack The inserted stack, given to the insertable forever
     * @param simulation If {@link Simulation#SIMULATE}, do not mutate the insertable
     * @return the leftover stack that could not be inserted
     */
    ItemStack insert(ItemStack insertedStack, Simulation simulation);
}
