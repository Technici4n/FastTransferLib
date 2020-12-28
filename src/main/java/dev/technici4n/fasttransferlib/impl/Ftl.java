package dev.technici4n.fasttransferlib.impl;

import dev.technici4n.fasttransferlib.impl.transaction.TransactionImpl;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class Ftl implements ModInitializer {
	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTED.register((server) -> TransactionImpl.setServerThread(Thread.currentThread()));
		ServerLifecycleEvents.SERVER_STOPPED.register((server) -> TransactionImpl.setServerThread(null));
	}
}
