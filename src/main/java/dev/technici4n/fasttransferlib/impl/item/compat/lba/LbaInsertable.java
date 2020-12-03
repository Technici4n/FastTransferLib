package dev.technici4n.fasttransferlib.impl.item.compat.lba;

import alexiil.mc.lib.attributes.Simulation;
import dev.technici4n.fasttransferlib.api.item.ItemInsertable;
import dev.technici4n.fasttransferlib.api.item.ItemKey;
import dev.technici4n.fasttransferlib.impl.compat.LbaUtil;
import net.minecraft.item.ItemStack;

class LbaInsertable implements alexiil.mc.lib.attributes.item.ItemInsertable {
    private final ItemInsertable insertable;

    LbaInsertable(ItemInsertable insertable) {
        this.insertable = insertable;
    }

    @Override
    public ItemStack attemptInsertion(ItemStack stack, Simulation simulation) {
        int leftover = insertable.insert(ItemKey.of(stack), stack.getCount(), LbaUtil.getSimulation(simulation));
        stack.setCount(leftover);
        return stack;
    }
}
