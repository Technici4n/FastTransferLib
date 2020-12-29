package dev.technici4n.fasttransferlib.impl.transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

import dev.technici4n.fasttransferlib.api.transaction.Participant;
import dev.technici4n.fasttransferlib.api.transaction.Transaction;

public class TransactionImpl implements Transaction {
	private static Thread serverThread = null;

	private static final Set<Participant> GLOBAL_PARTICIPANTS = Collections.newSetFromMap(new IdentityHashMap<>());
	private static final ArrayList<TransactionImpl> STACK = new ArrayList<>();
	private static int stackPointer = -1;
	private static boolean allowAccess = true;

	private final IdentityHashMap<Participant, Object> stateStorage = new IdentityHashMap<>();
	private boolean isOpen = false;

	private void clear() {
		stateStorage.clear();
		isOpen = false;
	}

	public static void setServerThread(Thread serverThread) {
		TransactionImpl.serverThread = serverThread;
	}

	private static void validateGlobalState() {
		if (!allowAccess) {
			throw new IllegalStateException("Transaction operations are not allowed at the moment.");
		}

		if (Thread.currentThread() != serverThread) {
			throw new IllegalStateException("Transaction operations can only be applied on the server thread.");
		}
	}

	private void validateCurrent() {
		validateGlobalState();

		if (!isOpen) {
			throw new IllegalStateException("Transaction operations cannot be applied to a closed transaction.");
		}

		if (STACK.get(stackPointer) != this) {
			throw new IllegalStateException("Transaction operations must be applied to the most recent open transaction.");
		}
	}

	private void close(boolean success) {
		validateCurrent();
		// block transaction operations
		allowAccess = false;
		// notify participants
		stateStorage.forEach((participant, state) -> participant.onClose(state, success));

		// if root, do the final update
		if (stackPointer == 0 && success) {
			GLOBAL_PARTICIPANTS.forEach(Participant::onFinalSuccess);
			GLOBAL_PARTICIPANTS.clear();
		}

		// clear things up
		clear();
		stackPointer--;
		allowAccess = true;
	}

	@Override
	public void rollback() {
		close(false);
	}

	@Override
	public void commit() {
		close(true);
	}

	@Override
	public void close() {
		if (isOpen) {
			rollback();
		}
	}

	public static Transaction open() {
		validateGlobalState();

		++stackPointer;

		if (stackPointer >= STACK.size()) {
			STACK.add(new TransactionImpl());
		}

		STACK.get(stackPointer).isOpen = true;
		return STACK.get(stackPointer);
	}

	public static void enlistIfOpen(Participant participant) {
		validateGlobalState();

		if (stackPointer != -1) {
			TransactionImpl transaction = STACK.get(stackPointer);

			allowAccess = false;
			transaction.stateStorage.computeIfAbsent(participant, Participant::onEnlist);
			GLOBAL_PARTICIPANTS.add(participant);
			allowAccess = true;
		}
	}

	public static boolean isOpen() {
		validateGlobalState();
		return stackPointer != -1;
	}
}
