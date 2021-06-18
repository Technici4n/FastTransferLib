package dev.technici4n.fasttransferlib.experimental.impl.item;

import java.util.Map;

import com.google.common.collect.MapMaker;
import dev.technici4n.fasttransferlib.experimental.api.item.ItemKey;
import dev.technici4n.fasttransferlib.experimental.api.item.ItemPreconditions;
import dev.technici4n.fasttransferlib.experimental.api.storage.SingleSlotStorage;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;

public class CursorSlotWrapper extends SnapshotParticipant<ItemStack> implements SingleSlotStorage<ItemKey> {
	private static final Map<ScreenHandler, CursorSlotWrapper> WRAPPERS = new MapMaker().weakKeys().makeMap();

	public static CursorSlotWrapper get(ScreenHandler screenHandler) {
		return WRAPPERS.computeIfAbsent(screenHandler, CursorSlotWrapper::new);
	}

	private final ScreenHandler screenHandler;

	private CursorSlotWrapper(ScreenHandler screenHandler) {
		this.screenHandler = screenHandler;
	}

	@Override
	public boolean supportsInsertion() {
		return true;
	}

	@Override
	public long insert(ItemKey itemKey, long maxAmount, Transaction transaction) {
		ItemPreconditions.notEmptyNotNegative(itemKey, maxAmount);
		ItemStack stack = screenHandler.getCursorStack();
		int inserted = (int) Math.min(maxAmount, Math.min(64, itemKey.getItem().getMaxCount()) - stack.getCount());

		if (stack.isEmpty()) {
			ItemStack keyStack = itemKey.toStack(inserted);
			this.updateSnapshots(transaction);
			screenHandler.setCursorStack(keyStack);
			return inserted;
		} else if (itemKey.matches(stack)) {
			this.updateSnapshots(transaction);
			stack.increment(inserted);
			return inserted;
		}

		return 0;
	}

	@Override
	public boolean supportsExtraction() {
		return true;
	}

	@Override
	public long extract(ItemKey itemKey, long maxAmount, Transaction transaction) {
		ItemPreconditions.notEmptyNotNegative(itemKey, maxAmount);
		ItemStack stack = screenHandler.getCursorStack();

		if (itemKey.matches(stack)) {
			int extracted = (int) Math.min(stack.getCount(), maxAmount);
			this.updateSnapshots(transaction);
			stack.decrement(extracted);
			return extracted;
		}

		return 0;
	}

	@Override
	public ItemKey resource() {
		return ItemKey.of(screenHandler.getCursorStack());
	}

	@Override
	public long capacity() {
		return screenHandler.getCursorStack().getMaxCount();
	}

	@Override
	public boolean isEmpty() {
		return screenHandler.getCursorStack().isEmpty();
	}

	@Override
	public long amount() {
		return screenHandler.getCursorStack().getCount();
	}

	@Override
	protected ItemStack createSnapshot() {
		return screenHandler.getCursorStack().copy();
	}

	@Override
	protected void readSnapshot(ItemStack snapshot) {
		screenHandler.setCursorStack(snapshot);
	}
}
