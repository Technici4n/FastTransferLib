package dev.technici4n.fasttransferlib.impl.item.compat;

import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.item.ItemExtractable;
import dev.technici4n.fasttransferlib.api.item.ItemInsertable;
import dev.technici4n.fasttransferlib.api.item.ItemKey;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class InventoryWrapper implements ItemInsertable, ItemExtractable {
    private final Inventory wrapped;

    public InventoryWrapper(Inventory wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public int extract(int slot, ItemKey key, int maxCount, Simulation simulation) {
        checkBounds(slot);
        ItemStack stack = wrapped.getStack(slot);

        if (!key.matches(stack)) return 0;

        int extracted = Math.min(maxCount, stack.getCount());
        stack.decrement(extracted);
        wrapped.markDirty();
        return extracted;
    }

    @Override
    public int insert(ItemKey key, int count, Simulation simulation) {
        // TODO
        return 0;
    }

    @Override
    public int getItemSlotCount() {
        return wrapped.size();
    }

    @Override
    public ItemKey getItemKey(int slot) {
        checkBounds(slot);
        return ItemKey.of(wrapped.getStack(slot));
    }

    @Override
    public int getItemCount(int slot) {
        checkBounds(slot);
        return wrapped.getStack(slot).getCount();
    }

    private void checkBounds(int slot) {
        if (slot < 0 || slot >= wrapped.size()) {
            throw new IndexOutOfBoundsException();
        }
    }
}
