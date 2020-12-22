package dev.technici4n.fasttransferlib.impl.item.compat.lba;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.CustomAttributeAdder;
import alexiil.mc.lib.attributes.SearchOptions;
import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.ItemAttributes;
import dev.technici4n.fasttransferlib.api.item.ItemApi;
import dev.technici4n.fasttransferlib.api.item.ItemIo;
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
		ItemApi.SIDED.registerFallback((world, pos, state, blockEntity, direction) -> {
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

	private static @Nullable ItemIo getIo(World world, BlockPos pos, Direction direction) {
		if (inCompat) return null;
		inCompat = true;
		@Nullable ItemIo view = ItemApi.SIDED.get(world, pos, direction);
		inCompat = false;
		return view;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void registerFtlInLba() {
		CustomAttributeAdder lbaBlockAdder = (world, pos, state, to) -> {
			Direction dir = to.getTargetSide();

			if (dir != null) {
				ItemIo io = getIo(world, pos, dir);

				if (io != null) {
					to.offer(new LbaWrappedItemIo(io));
				}
			}
		};
		ItemAttributes.EXTRACTABLE.appendBlockAdder(lbaBlockAdder);
		ItemAttributes.INSERTABLE.appendBlockAdder(lbaBlockAdder);
		ItemAttributes.FIXED_INV_VIEW.appendBlockAdder(lbaBlockAdder);
		ItemAttributes.GROUPED_INV_VIEW.appendBlockAdder(lbaBlockAdder);
	}
}
