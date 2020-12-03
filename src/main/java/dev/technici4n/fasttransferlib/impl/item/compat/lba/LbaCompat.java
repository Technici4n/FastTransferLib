package dev.technici4n.fasttransferlib.impl.item.compat.lba;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.SearchOptions;
import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.ItemAttributes;
import dev.technici4n.fasttransferlib.api.item.ItemApi;
import dev.technici4n.fasttransferlib.api.item.ItemExtractable;
import dev.technici4n.fasttransferlib.api.item.ItemInsertable;
import dev.technici4n.fasttransferlib.api.item.ItemView;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class LbaCompat {
	private static boolean inCompat = false;

	public static void init() {
		// initialize static
	}

	static {
		registerLbaInFtl();
		registerFtlInLba();
	}

	private static void registerLbaInFtl() {
		ItemApi.SIDED_VIEW.registerBlockFallback((world, pos, state, direction) -> {
			if (inCompat) return null;
			inCompat = true;
			AttributeList<FixedItemInv> to = ItemAttributes.FIXED_INV.getAll(world, pos, SearchOptions.inDirection(direction.getOpposite()));
			inCompat = false;

			if (to.hasOfferedAny()) {
				return new LbaWrappedFixedInv(to.combine(ItemAttributes.FIXED_INV));
			} else {
				return null;
			}
		});
	}

	private static @Nullable ItemView getView(World world, BlockPos pos, Direction direction) {
		if (inCompat) return null;
		inCompat = true;
		@Nullable ItemView view = ItemApi.SIDED_VIEW.get(world, pos, direction);
		inCompat = false;
		return view;
	}

	private static void registerFtlInLba() {
		ItemAttributes.EXTRACTABLE.appendBlockAdder(((world, pos, state, to) -> {
			Direction dir = to.getTargetSide();

			if (dir != null) {
				ItemView view = getView(world, pos, dir);

				if (view instanceof ItemExtractable) {
					to.offer(new LbaExtractable((ItemExtractable) view));
				}
			}
		}));
		ItemAttributes.INSERTABLE.appendBlockAdder((world, pos, state, to) -> {
			Direction dir = to.getTargetSide();

			if (dir != null) {
				ItemView view = getView(world, pos, dir);

				if (view instanceof ItemInsertable) {
					to.offer(new LbaInsertable((ItemInsertable) view));
				}
			}
		});
		ItemAttributes.FIXED_INV_VIEW.appendBlockAdder((world, pos, state, to) -> {
			Direction dir = to.getTargetSide();

			if (dir != null) {
				ItemView view = getView(world, pos, dir);

				if (view != null) {
					to.offer(new LbaFixedInvView(view));
				}
			}
		});
	}
}
