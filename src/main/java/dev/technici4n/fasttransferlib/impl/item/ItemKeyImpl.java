package dev.technici4n.fasttransferlib.impl.item;

import java.util.Objects;

import dev.technici4n.fasttransferlib.api.item.ItemKey;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemKeyImpl implements ItemKey {
	private final Item item;
	private final @Nullable CompoundTag tag;
	private final int hashCode;

	ItemKeyImpl(Item item, CompoundTag tag) {
		this.item = item;
		this.tag = tag == null ? null : tag.copy(); // defensive copy
		hashCode = Objects.hash(item, tag);
	}

	@Override
	public Item getItem() {
		return item;
	}

	@Override
	public boolean hasTag() {
		return tag != null;
	}

	@Override
	public boolean tagMatches(@Nullable CompoundTag other) {
		return Objects.equals(tag, other);
	}

	@Override
	public @Nullable CompoundTag copyTag() {
		return tag == null ? null : tag.copy();
	}

	@Override
	public CompoundTag toTag() {
		CompoundTag result = new CompoundTag();
		result.putString("item", Registry.ITEM.getId(item).toString());

		if (tag != null) {
			result.put("tag", tag.copy());
		}

		return result;
	}

	@Override
	public void toPacket(PacketByteBuf buf) {
		buf.writeVarInt(Registry.ITEM.getRawId(item));
		buf.writeCompoundTag(tag);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ItemKeyImpl itemKey = (ItemKeyImpl) o;
		return item == itemKey.item && tagMatches(itemKey.tag);
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	public static ItemKey of(Item item, @Nullable CompoundTag tag) {
		return ItemKeyCache.get(item, tag);
	}

	public static ItemKey fromTag(CompoundTag tag) {
		if (tag == null) {
			return ItemKey.EMPTY;
		}

		Item item = Registry.ITEM.get(new Identifier(tag.getString("item")));
		CompoundTag aTag = tag.contains("tag") ? tag.getCompound("tag") : null;
		return of(item, aTag);
	}

	public static ItemKey fromPacket(PacketByteBuf buf) {
		Item item = Registry.ITEM.get(buf.readVarInt());
		CompoundTag tag = buf.readCompoundTag();
		return of(item, tag);
	}
}
