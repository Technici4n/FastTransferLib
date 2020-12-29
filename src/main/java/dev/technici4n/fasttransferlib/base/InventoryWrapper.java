package dev.technici4n.fasttransferlib.base;

import java.util.List;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.primitives.Ints;
import dev.technici4n.fasttransferlib.api.item.ItemKey;
import dev.technici4n.fasttransferlib.api.item.ItemPreconditions;
import dev.technici4n.fasttransferlib.api.transaction.Participant;
import dev.technici4n.fasttransferlib.api.transaction.Transaction;
import dev.technici4n.fasttransferlib.api.transfer.StorageFunction;
import dev.technici4n.fasttransferlib.api.transfer.Storage;
import org.jetbrains.annotations.Nullable;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class InventoryWrapper {
	private static final WeakHashMap<Inventory, Storage<ItemKey>> WRAPPERS = new WeakHashMap<>();

	public static Storage<ItemKey> of(Inventory inventory) {
		Objects.requireNonNull(inventory, "Null inventory is not supported.");
		return WRAPPERS.computeIfAbsent(inventory, InventoryWrapper::innerOf);
	}

	private static Storage<ItemKey> innerOf(Inventory inventory) {
		List<InventoryStorageView> slots = IntStream.range(0, inventory.size()).mapToObj(i -> new InventoryStorageView(inventory, i)).collect(Collectors.toList());
		return new AggregateStorage<>(slots);
	}

	private static final class InventoryStorageView implements Storage<ItemKey>, DiscreteStorageView<ItemKey>, Participant {
		private final Inventory inventory;
		private final int slot;
		private final DiscreteStorageFunction<ItemKey> insertionFunction;
		private final DiscreteStorageFunction<ItemKey> extractionFunction;

		private InventoryStorageView(Inventory inventory, int slot) {
			this.inventory = inventory;
			this.slot = slot;
			this.insertionFunction = (itemKey, longCount) -> {
				ItemPreconditions.notEmpty(itemKey);
				int count = Math.min(Ints.saturatedCast(longCount), inventory.getMaxCountPerStack());
				ItemStack stack = inventory.getStack(slot);

				if (stack.isEmpty()) {
					ItemStack keyStack = itemKey.toStack(count);

					if (inventory.isValid(slot, keyStack)) {
						int inserted = Math.min(keyStack.getMaxCount(), count);

						Transaction.wrapModification(this, () -> {
							keyStack.setCount(inserted);
							inventory.setStack(slot, keyStack);
						});

						return inserted;
					}
				} else if (itemKey.matches(stack)) {
					int inserted = Math.min(stack.getMaxCount() - stack.getCount(), count);

					Transaction.wrapModification(this, () -> stack.increment(inserted));

					return inserted;
				}

				return 0;
			};
			this.extractionFunction = (itemKey, longCount) -> {
				ItemPreconditions.notEmpty(itemKey);
				int count = Ints.saturatedCast(longCount);
				ItemStack stack = inventory.getStack(slot);

				if (itemKey.matches(stack)) {
					int extracted = Math.min(stack.getCount(), count);

					Transaction.wrapModification(this, () -> stack.decrement(extracted));

					return extracted;
				}

				return 0;
			};
		}

		public DiscreteStorageFunction<ItemKey> insertionFunction() {
			return insertionFunction;
		}

		@Override
		public StorageFunction<ItemKey> extractionFunction() {
			return extractionFunction;
		}

		@Override
		public boolean forEach(Visitor<ItemKey> visitor) {
			if (!inventory.getStack(slot).isEmpty()) {
				return visitor.visit(this);
			}

			return false;
		}

		@Override
		public ItemKey resource() {
			return ItemKey.of(inventory.getStack(slot));
		}

		@Override
		public long amount() {
			return inventory.getStack(slot).getCount();
		}

		@Override
		public @Nullable Object onEnlist() {
			return inventory.getStack(slot).copy();
		}

		@Override
		public void onClose(@Nullable Object state, boolean success) {
			if (!success) {
				inventory.setStack(slot, (ItemStack) state);
			}
		}

		@Override
		public void onFinalSuccess() {
			inventory.markDirty();
		}
	}
}
