package me.vrekt.oasis.server;

import gdx.lunar.protocol.LunarProtocol;

/** Launches the server application. */
public class ServerLauncher {
	public static void main(String[] args) {
		new OasisLocalServer(new LunarProtocol(true)).startAsync();
	}
}