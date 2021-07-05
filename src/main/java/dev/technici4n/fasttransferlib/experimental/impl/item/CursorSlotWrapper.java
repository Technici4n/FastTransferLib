package dev.technici4n.fasttransferlib.experimental.impl.item;

import java.util.Map;

import com.google.common.collect.MapMaker;
import dev.technici4n.fasttransferlib.experimental.api.item.ItemVariant;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;

public class CursorSlotWrapper extends SnapshotParticipant<ItemStack> implements SingleSlotStorage<ItemVariant> {
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
	public long insert(ItemVariant ItemVariant, long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notBlankNotNegative(ItemVariant, maxAmount);
		ItemStack stack = screenHandler.getCursorStack();
		int inserted = (int) Math.min(maxAmount, Math.min(64, ItemVariant.getItem().getMaxCount()) - stack.getCount());

		if (stack.isEmpty()) {
			ItemStack keyStack = ItemVariant.toStack(inserted);
			this.updateSnapshots(transaction);
			screenHandler.setCursorStack(keyStack);
			return inserted;
		} else if (ItemVariant.matches(stack)) {
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
	public long extract(ItemVariant ItemVariant, long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notBlankNotNegative(ItemVariant, maxAmount);
		ItemStack stack = screenHandler.getCursorStack();

		if (ItemVariant.matches(stack)) {
			int extracted = (int) Math.min(stack.getCount(), maxAmount);
			this.updateSnapshots(transaction);
			stack.decrement(extracted);
			return extracted;
		}

		return 0;
	}

	@Override
	public ItemVariant getResource() {
		return ItemVariant.of(screenHandler.getCursorStack());
	}

	@Override
	public long getCapacity() {
		return screenHandler.getCursorStack().getMaxCount();
	}

	@Override
	public boolean isResourceBlank() {
		return screenHandler.getCursorStack().isEmpty();
	}

	@Override
	public long getAmount() {
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
