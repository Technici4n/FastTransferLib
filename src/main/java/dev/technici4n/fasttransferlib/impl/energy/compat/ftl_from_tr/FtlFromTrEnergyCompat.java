package dev.technici4n.fasttransferlib.impl.energy.compat.ftl_from_tr;

import dev.technici4n.fasttransferlib.api.energy.EnergyApi;
import dev.technici4n.fasttransferlib.api.energy.EnergyIo;
import dev.technici4n.fasttransferlib.impl.energy.compat.TrEnergyCompat;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.Energy;
import team.reborn.energy.EnergySide;
import team.reborn.energy.EnergyStorage;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

public class FtlFromTrEnergyCompat {
	public static void init() {
		Energy.registerHolder(object -> compatProvider(object) != null, FtlFromTrEnergyCompat::compatProvider);
	}

	@Nullable
	private static EnergyStorage compatProvider(Object object) {
		if (TrEnergyCompat.IN_COMPAT.get() == TrEnergyCompat.IN_COMPAT_TRUE) return null;

		try {
			TrEnergyCompat.IN_COMPAT.set(TrEnergyCompat.IN_COMPAT_TRUE);

			if (object instanceof BlockEntity) {
				BlockEntity be = (BlockEntity) object;
				@Nullable
				EnergyIo[] ioArray = new EnergyIo[6];
				boolean foundAny = false;

				for (int i = 0; i < 6; ++i) {
					ioArray[i] = EnergyApi.SIDED.find(be.getWorld(), be.getPos(), null, be, Direction.byId(i));

					if (ioArray[i] != null) {
						foundAny = true;
					}
				}

				if (foundAny) {
					return new EnergyIoWrapper() {
						@Override
						public EnergyIo getIo(EnergySide side) {
							int ordinal = side.ordinal();
							if (ordinal < 6 && ioArray[ordinal] != null) {
								return ioArray[ordinal];
							} else {
								return EnergyApi.EMPTY;
							}
						}
					};
				}
			} else if (object instanceof ItemStack) {
				ItemStack stack = (ItemStack) object;
				@Nullable
				EnergyIo io = EnergyApi.ITEM.find(stack, null);

				if (io != null) {
					return new EnergyIoWrapper() {
						@Override
						public EnergyIo getIo(EnergySide side) {
							return side == EnergySide.UNKNOWN ? io : EnergyApi.EMPTY;
						}
					};
				} else {
					return null;
				}
			}
		} finally {
			TrEnergyCompat.IN_COMPAT.remove();
		}

		return null;
	}
}
