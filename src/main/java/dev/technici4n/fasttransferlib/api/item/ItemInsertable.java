package dev.technici4n.fasttransferlib.api.item;

import dev.technici4n.fasttransferlib.api.Simulation;

/**
 * An item inventory that supports inserting items.
 *
 * @see ItemExtractable
 */
public interface ItemInsertable extends ItemView {
    /**
     * Insert items into this inventory, and return the number of leftover items.
     * Distribution is left entirely to the implementor.
     * <p>If simulation is {@link Simulation#SIMULATE}, the result of the operation must be returned, but the underlying state of the item insertable must not change.
     *
     * @param key The ItemKey to insert
     * @param count The number of items to insert
     * @param simulation If {@link Simulation#SIMULATE}, do not mutate the insertable
     * @return the number of items that could not be inserted
     */
    int insert(ItemKey key, int count, Simulation simulation);
}
