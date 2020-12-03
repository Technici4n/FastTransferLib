package dev.technici4n.fasttransferlib.impl.fluid.compat.lba;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.SearchOptions;
import alexiil.mc.lib.attributes.fluid.FixedFluidInv;
import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import dev.technici4n.fasttransferlib.api.fluid.FluidApi;
import dev.technici4n.fasttransferlib.api.fluid.FluidExtractable;
import dev.technici4n.fasttransferlib.api.fluid.FluidInsertable;
import dev.technici4n.fasttransferlib.api.fluid.FluidView;
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
		FluidApi.SIDED_VIEW.registerBlockFallback((world, pos, state, direction) -> {
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

	private static @Nullable FluidView getView(World world, BlockPos pos, Direction direction) {
		if (inCompat) return null;
		inCompat = true;
		@Nullable FluidView view = FluidApi.SIDED_VIEW.get(world, pos, direction);
		inCompat = false;
		return view;
	}

	private static void registerFtlInLba() {
		FluidAttributes.EXTRACTABLE.appendBlockAdder(((world, pos, state, to) -> {
			Direction dir = to.getTargetSide();

			if (dir != null) {
				FluidView view = getView(world, pos, dir);

				if (view instanceof FluidExtractable) {
					to.offer(new LbaExtractable((FluidExtractable) view));
				}
			}
		}));
		FluidAttributes.INSERTABLE.appendBlockAdder((world, pos, state, to) -> {
			Direction dir = to.getTargetSide();

			if (dir != null) {
				FluidView view = getView(world, pos, dir);

				if (view instanceof FluidInsertable) {
					to.offer(new LbaInsertable((FluidInsertable) view));
				}
			}
		});
		FluidAttributes.FIXED_INV_VIEW.appendBlockAdder((world, pos, state, to) -> {
			Direction dir = to.getTargetSide();

			if (dir != null) {
				FluidView view = getView(world, pos, dir);

				if (view != null) {
					to.offer(new LbaFixedInvView(view));
				}
			}
		});
	}
}
