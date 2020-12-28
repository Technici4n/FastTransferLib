package dev.technici4n.fasttransferlib.api.transaction;

import dev.technici4n.fasttransferlib.impl.transaction.TransactionImpl;

/**
 * An operation where participants guarantee atomicity.
 */
public interface Transaction extends AutoCloseable {
	/**
	 * Rollback all changes that happened during this transaction.
	 */
	void rollback();

	/**
	 * Validate all changes that happened during this transaction.
	 */
	void commit();

	/**
	 * Open a new transaction.
	 * It must always be used in a try-with-resources block.
	 * If the transaction is not rolled back or committed when it is closed, it will be rolled back.
	 */
	static Transaction open() {
		return TransactionImpl.open();
	}

	/**
	 * Enlist the participant in the current transaction if there is an open transaction,
	 * and if the participants isn't yet enlisted.
	 */
	static void enlistIfOpen(Participant participant) {
		TransactionImpl.enlistIfOpen(participant);
	}
}
