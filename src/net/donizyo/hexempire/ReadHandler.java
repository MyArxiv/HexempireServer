package net.donizyo.hexempire;

import static net.donizyo.hexempire.Utils.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class ReadHandler implements CompletionHandler<Integer, ByteBuffer> {
	private final Server server;
	private final ClientHandler clientHandler;
	private final AsynchronousSocketChannel socket;
	private volatile boolean chatMode;
	
	private static final String CHAT_PRIVATE = "/msg ";
	private static final String PLAYER_ALTERNAME = "/setname ";
	private static final int LEN_CHAT_PRIVATE = CHAT_PRIVATE.length();
	private static final int LEN_PLAYER_ALTERNAME = PLAYER_ALTERNAME.length();

	ReadHandler(Server parent, ClientHandler handler, AsynchronousSocketChannel socket) {
		server = parent;
		clientHandler = handler;
		this.socket = socket;
		chatMode = false;
	}

	@Override
	public void completed(Integer i, ByteBuffer buf) {
		if (i > 0) {
			buf.flip();
			if (DEBUG) {
				try {
					System.out.println("收到来自["+socket.getRemoteAddress().toString()+"]的消息:\n"+CHARSET.decode(buf));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
//			server.serverEntry.process(Integer.valueOf(CHARSET.decode(buf).toString()));
			String msg = CHARSET.decode(buf).toString();
			int command = Utils.parseHexInt(msg);
			if ((command & 0xFF) == Communication.CHAT_NEW_BEGIN) {
				setBufferSize(command >> Communication.HEXLEN * 2);
				chatMode = true;
				socket.read(buf, buf, this);
			} else if ((command & 0xFF) == Communication.CHAT_NEW_END) {
				setBufferSize(Communication.MAXLEN);
				socket.read(buf, buf, this);
			} else {
				if (chatMode) {
					if (msg.startsWith(CHAT_PRIVATE)) {
						String sub = msg.substring(LEN_CHAT_PRIVATE);
						try {
							int nid = Integer.valueOf(sub.substring(0, 1));
							server.taskManager.send(nid, ByteBuffer.wrap(Integer.toHexString(buf.capacity() << Communication.HEXLEN * 2 | Communication.CHAT_NEW_BEGIN).getBytes(Utils.CHARSET)));
							server.taskManager.send(nid, buf);
							server.taskManager.send(nid, ByteBuffer.wrap(Integer.toHexString(Communication.CHAT_NEW_END).getBytes(Utils.CHARSET)));
						} catch (NumberFormatException e) {
							int split = sub.indexOf(" ");
							String name = sub.substring(0, split);// TODO bad player name tradition -- split by whitespace
							ClientHandler handler = server.playerList.get(name);
							if (handler != null) {
								AsynchronousSocketChannel channel = handler.getChannel(); 
								channel.write(ByteBuffer.wrap(Integer.toHexString(buf.capacity() << Communication.HEXLEN * 2 | Communication.CHAT_NEW_BEGIN).getBytes(Utils.CHARSET)));
								channel.write(buf);
								channel.write(ByteBuffer.wrap(Integer.toHexString(Communication.CHAT_NEW_END).getBytes(Utils.CHARSET)));
							}
						}
					} else if (msg.startsWith(PLAYER_ALTERNAME)) {
						clientHandler.playerName = msg.substring(LEN_PLAYER_ALTERNAME);
					}
				} else {
					command |= (clientHandler.getId() << Communication.HEXLEN * 7);
					server.serverEntry.process(clientHandler, command);
				}
			}
			buf.compact();
			if (server.isShuttingDown.get())
				return;
			socket.read(buf, buf, this);
		} else if (i == -1) {
			try {
				System.out.println("客户端["+socket.getRemoteAddress().toString()+"]断开连接");
				buf=null;
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}

	@Override
	public void failed(Throwable exc, ByteBuffer attachment) {
		exc.printStackTrace();
	}

	private final void setBufferSize(int bufSize) {
		clientHandler.bufSize = bufSize;
	}
}