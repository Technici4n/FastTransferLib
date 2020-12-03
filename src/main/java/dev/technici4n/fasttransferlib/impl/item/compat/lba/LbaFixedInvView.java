package dev.technici4n.fasttransferlib.impl.item.compat.lba;

import alexiil.mc.lib.attributes.item.FixedItemInvView;
import dev.technici4n.fasttransferlib.api.item.ItemView;

import net.minecraft.item.ItemStack;

class LbaFixedInvView implements FixedItemInvView {
	private final ItemView view;

	LbaFixedInvView(ItemView view) {
		this.view = view;
	}

	@Override
	public int getSlotCount() {
		return view.getItemSlotCount();
	}

	@Override
	public ItemStack getInvStack(int slot) {
		return view.getItemKey(slot).toStack(view.getItemCount(slot));
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return true; // let's say it is?
	}
}
