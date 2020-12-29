package dev.technici4n.fasttransferlib.base.fluid;

import dev.technici4n.fasttransferlib.api.fluid.FluidPreconditions;
import dev.technici4n.fasttransferlib.api.transaction.Participant;
import dev.technici4n.fasttransferlib.api.transaction.Transaction;
import dev.technici4n.fasttransferlib.api.transfer.Storage;
import dev.technici4n.fasttransferlib.api.transfer.StorageFunction;
import dev.technici4n.fasttransferlib.base.FixedDenominatorStorageFunction;
import dev.technici4n.fasttransferlib.base.FixedDenominatorStorageView;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;

public class SimpleFluidStorage implements Storage<Fluid>, FixedDenominatorStorageView<Fluid>, Participant {
	public final long denominator;
	public final long capacity;
	public Fluid fluid = Fluids.EMPTY;
	public long amount = 0;
	public int version = 0;
	protected final FixedDenominatorStorageFunction<Fluid> insertionFunction;
	protected final FixedDenominatorStorageFunction<Fluid> extractionFunction;

	public SimpleFluidStorage(long denominator, long capacity) {
		this.denominator = denominator;
		this.capacity = capacity;
		this.insertionFunction = new FixedDenominatorStorageFunction<Fluid>() {
			@Override
			public long denominator() {
				return denominator;
			}

			@Override
			public long applyFixedDenominator(Fluid resource, long numerator) {
				FluidPreconditions.notEmpty(resource);

				if (fluid == Fluids.EMPTY) {
					long inserted = Math.min(capacity, numerator);

					Transaction.wrapModification(SimpleFluidStorage.this, () -> {
						fluid = resource;
						amount = inserted;
					});

					return inserted;
				} else if (fluid == resource) {
					long inserted = Math.min(capacity - amount, numerator);

					Transaction.wrapModification(SimpleFluidStorage.this, () -> amount += inserted);

					return inserted;
				}

				return 0;
			}
		};
		this.extractionFunction = new FixedDenominatorStorageFunction<Fluid>() {
			@Override
			public long denominator() {
				return denominator;
			}

			@Override
			public long applyFixedDenominator(Fluid resource, long numerator) {
				FluidPreconditions.notEmpty(resource);

				if (fluid != Fluids.EMPTY) {
					long extracted = Math.min(amount, numerator);

					Transaction.wrapModification(SimpleFluidStorage.this, () -> {
						amount -= extracted;
						if (amount == 0) fluid = Fluids.EMPTY;
					});

					return extracted;
				}

				return 0;
			}
		};
	}

	@Override
	public Object onEnlist() {
		// TODO: pool these things?
		return new Object[] { fluid, amount };
	}

	@Override
	public void onClose(Object state, boolean success) {
		if (!success) {
			Object[] oldState = (Object[]) state;
			this.fluid = (Fluid) oldState[0];
			this.amount = (long) oldState[1];
		}
	}

	@Override
	public void onFinalSuccess() {
		++version;
	}

	@Override
	public StorageFunction<Fluid> insertionFunction() {
		return insertionFunction;
	}

	@Override
	public StorageFunction<Fluid> extractionFunction() {
		return extractionFunction;
	}

	@Override
	public Fluid resource() {
		return fluid;
	}

	@Override
	public boolean forEach(Visitor<Fluid> visitor) {
		if (fluid != Fluids.EMPTY) {
			return visitor.visit(this);
		}

		return false;
	}

	@Override
	public int getVersion() {
		return version;
	}

	@Override
	public long denominator() {
		return denominator;
	}

	@Override
	public long amountFixedDenominator() {
		return amount;
	}
}
