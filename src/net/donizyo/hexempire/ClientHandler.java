package net.donizyo.hexempire;

import static net.donizyo.hexempire.Utils.DEBUG;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public final class ClientHandler implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {
	private final Server server;
	volatile int bufSize = 1024;
	String playerName;
	private int nid;
	private AsynchronousSocketChannel channel;
	private ReadHandler readHandler;

	ClientHandler(Server parent, int bufferSize) {
		server = parent;
		bufSize = bufferSize;
	}
	
	ClientHandler(Server parent) {
		server = parent;
		bufSize = Communication.MAXLEN;
	}

	public AsynchronousSocketChannel getChannel() {
		return channel;
	}

	public ReadHandler getReadHandler() {
		return readHandler;
	}

	final void setId(int id) {
		nid = id;
	}

	public final int getId() {
		return nid;
	}

	public void setName(String name) {
		playerName = name;
	}

	public String getName() {
		return playerName;
	}

	@Override
	public void completed(AsynchronousSocketChannel socket,
			AsynchronousServerSocketChannel attachment)
	{
		channel = socket;
		server.playerList.add(this);
		attachment.accept(attachment, this);
		if (DEBUG) {
			try {
				System.out.println("New connection established. Client IP ["+socket.getRemoteAddress().toString()+"]");
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
		ByteBuffer clientBuffer;
		readHandler = new ReadHandler(server, this, socket);
		while (!server.isShuttingDown.get()) {
			clientBuffer = ByteBuffer.allocate(bufSize);
			socket.read(clientBuffer, clientBuffer, readHandler);
		}
	}

	@Override
	public void failed(Throwable exc,
			AsynchronousServerSocketChannel attachment) {
		exc.printStackTrace();
	}
}
