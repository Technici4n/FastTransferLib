package dev.technici4n.fasttransferlib.experimental.api.fluid;

import java.util.Objects;
import java.util.function.Function;

import com.google.common.base.Preconditions;
import dev.technici4n.fasttransferlib.experimental.api.context.ContainerItemContext;
import dev.technici4n.fasttransferlib.experimental.api.item.ItemKey;
import dev.technici4n.fasttransferlib.experimental.api.item.ItemPreconditions;
import dev.technici4n.fasttransferlib.experimental.impl.fluid.EmptyItemsRegistry;
import dev.technici4n.fasttransferlib.experimental.impl.fluid.FluidApiImpl;
import dev.technici4n.fasttransferlib.experimental.impl.fluid.SimpleFluidContainingItem;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidKey;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;

// TODO: delegate to static FluidApiImpl functions so that other impl classes can be package private
public class ItemFluidStorage {
	public static final ItemApiLookup<Storage<FluidKey>, ContainerItemContext> ITEM = ItemApiLookup.get(new Identifier("ftl:fluid_storage"),
			Storage.asClass(), ContainerItemContext.class);

	/**
	 * Register an item that contains a fluid and can be emptied of it entirely.
	 *
	 * <p>Note: If a provider was already registered for the full item, this function
	 * will do nothing.
	 *
	 * @param fullItem  The item that contains the fluid.
	 * @param fluid     The contained fluid. May not be empty.
	 * @param amount    The amount of fluid in the full item. Must be positive.
	 * @param emptyItem The emptied item.
	 */
	public static void registerFullItem(Item fullItem, FluidKey fluid, long amount, Item emptyItem) {
		registerFullItem(fullItem, fluid, amount, key -> ItemKey.of(emptyItem, key.getNbt()));
	}

	/**
	 * Register an item that contains a fluid and can be emptied of it entirely.
	 *
	 * <p>Note: If a provider was already registered for the full item, this function
	 * will do nothing.
	 *
	 * @param fullItem   The item that contains the fluid.
	 * @param fluid      The contained fluid. May not be empty.
	 * @param amount     The amount of fluid in the full item. Must be positive.
	 * @param keyMapping A function mapping the key of the source item to that of
	 *                   the target item.
	 */
	public static void registerFullItem(Item fullItem, FluidKey fluid, long amount, Function<ItemKey, ItemKey> keyMapping) {
		ItemPreconditions.notEmpty(fullItem);
		StoragePreconditions.notEmptyNotNegative(fluid, amount);
		Preconditions.checkArgument(amount > 0);

		ITEM.registerForItems((stack, ctx) -> new SimpleFluidContainingItem(ctx, ItemKey.of(stack), fluid, amount, keyMapping), fullItem);
	}

	/**
	 * Register an item that is empty, and may be filled with some fluid entirely.
	 */
	// TODO: document params and conflicts
	// TODO: pick parameter order, probably the same for both methods?
	public static void registerEmptyItem(Item emptyItem, FluidKey fluid, long amount, Item fullItem) {
		registerEmptyItem(emptyItem, fluid, amount, key -> ItemKey.of(fullItem, key.getNbt()));
	}

	/**
	 * Register an item that is empty, and may be filled with some fluid entirely.
	 */
	// TODO: document params and conflicts
	// TODO: pick parameter order, probably the same for both methods?
	public static void registerEmptyItem(Item emptyItem, FluidKey fluid, long amount, Function<ItemKey, ItemKey> keyMapping) {
		ItemPreconditions.notEmpty(emptyItem);
		StoragePreconditions.notEmptyNotNegative(fluid, amount);
		Objects.requireNonNull(keyMapping);

		EmptyItemsRegistry.registerEmptyItem(emptyItem, fluid, amount, keyMapping);
	}

	/**
	 * Register full and empty variants of a fluid container item. Calls both
	 * {@link #registerEmptyItem(Item, FluidKey, long, Item) registerEmptyItem} and
	 * {@link #registerFullItem}.
	 *
	 * @param emptyItem The empty variant of the container.
	 * @param fluid     The fluid.
	 * @param amount    The amount.
	 * @param fullItem  The full variant of the container.
	 */
	public static void registerEmptyAndFullItems(Item emptyItem, FluidKey fluid, long amount, Item fullItem) {
		registerEmptyItem(emptyItem, fluid, amount, fullItem);
		registerFullItem(fullItem, fluid, amount, emptyItem);
	}

	// TODO: potion handling

	private ItemFluidStorage() {
	}

	static {
		FluidApiImpl.init();
	}
}
