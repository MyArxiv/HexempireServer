package net.donizyo.hexempire;

import java.nio.ByteBuffer;
import java.util.Iterator;


class ServerTaskManager {
	private final Server server;

	public ServerTaskManager(Server parent) {
		server = parent;
	}

	public static boolean send(ClientHandler handler, ByteBuffer src) {
		return handler.getChannel().write(src).isDone();
	}

	public static boolean send(ClientHandler handler, String txt) {
		return send(handler, ByteBuffer.wrap(txt.getBytes(Utils.CHARSET)));
	}

	public static boolean send(ClientHandler handler, int command) {
		return send(handler, Integer.toHexString(command));
	}

	public boolean send(int id, ByteBuffer src) {
		return send(server.playerList.get(id), src);
	}

	public void broadcast(ByteBuffer src) {
		for (Iterator<ClientHandler> itr = server.playerList.iterator(); itr.hasNext();) {
			send(itr.next(), src);
		}
	}

	public boolean send(char id, String txt) {
		return send(id, ByteBuffer.wrap(txt.getBytes(Utils.CHARSET)));
	}

	public void broadcast(String txt) {
		broadcast(ByteBuffer.wrap(txt.getBytes(Utils.CHARSET)));
	}

	public boolean send(char id, int command) {
		return send(id, Integer.toHexString(command));
	}

	public void broadcast(int command) {
		broadcast(Integer.toHexString(command));
	}
}