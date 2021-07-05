package dev.technici4n.fasttransferlib.experimental.impl.item;

import java.util.Objects;

import dev.technici4n.fasttransferlib.experimental.api.item.ItemVariant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemVariantImpl implements ItemVariant {
	public static ItemVariant of(Item item, @Nullable NbtCompound tag) {
		Objects.requireNonNull(item, "Item may not be null.");

		// Only tag-less or empty item keys are cached for now.
		if (tag == null || item == Items.AIR) {
			return ((ItemVariantCache) item).ftl_getCachedItemVariant();
		} else {
			return new ItemVariantImpl(item, tag);
		}
	}

	private static final Logger LOGGER = LogManager.getLogger("fast-transfer-lib/item");

	private final Item item;
	private final @Nullable NbtCompound tag;
	private final int hashCode;

	public ItemVariantImpl(Item item, NbtCompound tag) {
		this.item = item;
		this.tag = tag == null ? null : tag.copy(); // defensive copy
		hashCode = Objects.hash(item, tag);
	}

	@Override
	public Item getObject() {
		return item;
	}

	@Nullable
	@Override
	public NbtCompound getNbt() {
		return tag;
	}

	@Override
	public boolean isBlank() {
		return item == Items.AIR;
	}

	@Override
	public NbtCompound toNbt() {
		NbtCompound result = new NbtCompound();
		result.putString("item", Registry.ITEM.getId(item).toString());

		if (tag != null) {
			result.put("tag", tag.copy());
		}

		return result;
	}

	public static ItemVariant fromNbt(NbtCompound tag) {
		try {
			Item item = Registry.ITEM.get(new Identifier(tag.getString("item")));
			NbtCompound aTag = tag.contains("tag") ? tag.getCompound("tag") : null;
			return of(item, aTag);
		} catch (RuntimeException runtimeException) {
			LOGGER.debug("Tried to load an invalid ItemVariant from NBT: {}", tag, runtimeException);
			return ItemVariant.blank();
		}
	}

	@Override
	public void toPacket(PacketByteBuf buf) {
		if (isBlank()) {
			buf.writeBoolean(false);
		} else {
			buf.writeBoolean(true);
			buf.writeVarInt(Item.getRawId(item));
			buf.writeNbt(tag);
		}
	}

	public static ItemVariant fromPacket(PacketByteBuf buf) {
		if (!buf.readBoolean()) {
			return ItemVariant.blank();
		} else {
			Item item = Item.byRawId(buf.readVarInt());
			NbtCompound tag = buf.readNbt();
			return of(item, tag);
		}
	}

	@Override
	public String toString() {
		return "ItemVariantImpl{item=" + item + ", tag=" + tag + '}';
	}

	@Override
	public boolean equals(Object o) {
		// succeed fast with == check
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ItemVariantImpl ItemVariant = (ItemVariantImpl) o;
		// fail fast with hash code
		return hashCode == ItemVariant.hashCode && item == ItemVariant.item && nbtMatches(ItemVariant.tag);
	}

	@Override
	public int hashCode() {
		return hashCode;
	}
}
