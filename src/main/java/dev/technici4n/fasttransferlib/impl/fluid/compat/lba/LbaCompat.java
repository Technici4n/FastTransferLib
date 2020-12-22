package dev.technici4n.fasttransferlib.impl.fluid.compat.lba;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.CustomAttributeAdder;
import alexiil.mc.lib.attributes.SearchOptions;
import alexiil.mc.lib.attributes.fluid.FixedFluidInv;
import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import dev.technici4n.fasttransferlib.api.fluid.FluidApi;
import dev.technici4n.fasttransferlib.api.fluid.FluidIo;
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
		FluidApi.SIDED.registerFallback((world, pos, state, blockEntity, direction) -> {
			if (inCompat) return null;
			inCompat = true;
			AttributeList<FixedFluidInv> to = FluidAttributes.FIXED_INV.getAll(world, pos, SearchOptions.inDirection(direction.getOpposite()));
			inCompat = false;

			if (to.hasOfferedAny()) {
				return new LbaWrappedFixedInv(to.combine(FluidAttributes.FIXED_INV));
			} else {
				return null;
			}
		});
	}

	private static @Nullable FluidIo getIo(World world, BlockPos pos, Direction direction) {
		if (inCompat) return null;
		inCompat = true;
		@Nullable FluidIo view = FluidApi.SIDED.get(world, pos, direction);
		inCompat = false;
		return view;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void registerFtlInLba() {
		CustomAttributeAdder lbaBlockAdder = (world, pos, state, to) -> {
			Direction dir = to.getTargetSide();

			if (dir != null) {
				FluidIo io = getIo(world, pos, dir);

				if (io != null) {
					to.offer(new LbaWrappedFluidIo(io));
				}
			}
		};
		FluidAttributes.EXTRACTABLE.appendBlockAdder(lbaBlockAdder);
		FluidAttributes.INSERTABLE.appendBlockAdder(lbaBlockAdder);
		FluidAttributes.FIXED_INV_VIEW.appendBlockAdder(lbaBlockAdder);
		FluidAttributes.GROUPED_INV_VIEW.appendBlockAdder(lbaBlockAdder);
	}
}
