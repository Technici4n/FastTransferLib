package dev.technici4n.fasttransferlib.api.fluid.base;

import java.util.IdentityHashMap;
import java.util.Map;

import dev.technici4n.fasttransferlib.api.ContainerItemContext;
import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.fluid.FluidApi;
import dev.technici4n.fasttransferlib.api.fluid.FluidIo;
import dev.technici4n.fasttransferlib.api.fluid.FluidTextHelper;
import dev.technici4n.fasttransferlib.api.item.ItemKey;
import dev.technici4n.fasttransferlib.api.item.ItemKeyApiLookup;
import dev.technici4n.fasttransferlib.impl.FtlImpl;
import org.jetbrains.annotations.Nullable;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

/**
 * A provider for item-provided FluidIo's that can be filled with multiple fluids.
 */
// TODO: this is still WIP
public class ItemFillMapProvider implements ItemKeyApiLookup.ItemKeyApiProvider<FluidIo, ContainerItemContext> {
	private final Map<Fluid, Entry> fluids = new IdentityHashMap<>();
	private final Item sourceItem;

	public ItemFillMapProvider(Item sourceItem) {
		if (sourceItem == null || sourceItem == Items.AIR) throw new IllegalArgumentException("Illegal sourceItem: " + sourceItem);

		this.sourceItem = sourceItem;
		FluidApi.ITEM.register(this, sourceItem);
	}

	public void registerFill(Fluid fluid, ItemKey filledKey, long amount) {
		if (fluid == null || fluid == Fluids.EMPTY || filledKey.isEmpty() || amount <= 0) throw new IllegalArgumentException();

		Entry entry = new Entry(filledKey, amount);

		if (fluids.put(fluid, entry) != null) {
			String errorMessage = String.format(
					"In fill map for %s, entry for fluid %s existed already. Could not register amount %d for key %s.",
					sourceItem, FluidTextHelper.toString(fluid), amount, filledKey
			);
			FtlImpl.LOGGER.warn(errorMessage);
		}
	}

	@Override
	public @Nullable FluidIo get(ItemKey itemKey, ContainerItemContext context) {
		return null;
	}

	private class Entry {
		private final ItemKey filledKey;
		private final long amount;

		private Entry(ItemKey filledKey, long amount) {
			this.filledKey = filledKey;
			this.amount = amount;
		}
	}

	private class Io implements FluidIo {
		@Override
		public int getFluidSlotCount() {
			return 0;
		}

		@Override
		public Fluid getFluid(int slot) {
			return null;
		}

		@Override
		public long getFluidAmount(int slot) {
			return 0;
		}

		@Override
		public boolean supportsFluidInsertion() {
			return false;
		}

		@Override
		public long insert(Fluid fluid, long amount, Simulation simulation) {
			return 0;
		}
	}
}
