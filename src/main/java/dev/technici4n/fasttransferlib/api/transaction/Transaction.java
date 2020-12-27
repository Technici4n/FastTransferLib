package dev.technici4n.fasttransferlib.api.transaction;

public interface Transaction extends AutoCloseable {
	void rollback();
	void commit();
	static Transaction open() {
		throw new UnsupportedOperationException("NYI");
	}
	static void enlistIfOpen(Participant participant) {
		throw new UnsupportedOperationException("NYI");
	}
}
