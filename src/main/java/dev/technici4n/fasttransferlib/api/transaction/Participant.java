package dev.technici4n.fasttransferlib.api.transaction;

import org.jetbrains.annotations.Nullable;

public interface Participant {
	<T> @Nullable T beginTransaction();
	<T> void endTransaction(@Nullable T state, boolean success);
}
