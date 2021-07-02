package dev.technici4n.fasttransferlib.experimental.api.item;

import dev.technici4n.fasttransferlib.experimental.impl.item.ItemVariantImpl;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;

/**
 * An immutable count-less ItemStack, i.e. an immutable association of an item
 * and an optional NBT compound tag.
 *
 * <p>Do not implement, use the static {@code of(...)} functions instead.
 */
@ApiStatus.NonExtendable
public interface ItemVariant extends TransferVariant<Item> {
	/**
	 * Retrieve an empty ItemVariant.
	 */
	static ItemVariant empty() {
		return of(Items.AIR);
	}

	/**
	 * Retrieve an ItemVariant with the item and tag of a stack.
	 */
	static ItemVariant of(ItemStack stack) {
		return of(stack.getItem(), stack.getTag());
	}

	/**
	 * Retrieve an ItemVariant with an item and without a tag.
	 */
	static ItemVariant of(ItemConvertible item) {
		return of(item, null);
	}

	/**
	 * Retrieve an ItemVariant with an item and an optional tag.
	 */
	static ItemVariant of(ItemConvertible item, @Nullable NbtCompound tag) {
		return ItemVariantImpl.of(item.asItem(), tag);
	}

	/**
	 * Return true if the item and tag of this key match those of the passed stack,
	 * and false otherwise.
	 */
	default boolean matches(ItemStack stack) {
		return isOf(stack.getItem()) && nbtMatches(stack.getTag());
	}

	/**
	 * Return the item of this key.
	 */
	default Item getItem() {
		return getObject();
	}

	/**
	 * Create a new item stack with count 1 from this key.
	 */
	default ItemStack toStack() {
		return toStack(1);
	}

	/**
	 * Create a new item stack from this key.
	 *
	 * @param count The count of the returned stack. It may lead to counts higher
	 *              than maximum stack size.
	 */
	default ItemStack toStack(int count) {
		if (isEmpty()) return ItemStack.EMPTY;
		ItemStack stack = new ItemStack(getItem(), count);
		stack.setTag(copyNbt());
		return stack;
	}

	/**
	 * Deserialize a key from an NBT compound tag, assuming it was serialized using
	 * {@link #toNbt}. If an error occurs during deserialization, it will be logged
	 * with the DEBUG level, and an empty key will be returned.
	 */
	static ItemVariant fromNbt(NbtCompound nbt) {
		return ItemVariantImpl.fromNbt(nbt);
	}

	/**
	 * Write a key from a packet byte buffer, assuming it was serialized using
	 * {@link #toPacket}.
	 */
	static ItemVariant fromPacket(PacketByteBuf buf) {
		return ItemVariantImpl.fromPacket(buf);
	}
}
