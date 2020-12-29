package dev.technici4n.fasttransferlib.api.transaction;

public interface Participant {
	/**
	 * Return the state to be put in the Transaction for this participant.
	 * This will be called every time a participant is enlisted for the first time in a transaction.
	 * <p>Note: Returning {@code null} is allowed.</p>
	 */
	Object onEnlist();

	/**
	 * This will be called when a transaction is closed if the participant was enlisted.
	 */
	void onClose(Object state, boolean success);

	/**
	 * This will be called at the end of the outermost transaction if it is successful, exactly once per participant
	 * involved in the transaction. Block updates should be deferred until then.
	 */
	default void onFinalSuccess() {
	}
}
