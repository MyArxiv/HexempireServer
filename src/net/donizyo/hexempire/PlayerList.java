package net.donizyo.hexempire;

import static net.donizyo.hexempire.Configuration.LIM_NATION;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PlayerList extends ArrayList<ClientHandler> implements List<ClientHandler> {
	private static final long serialVersionUID = 1L;
	
//	private final Server server;
	
	PlayerList(Server parent) {
//		server = parent;
	}

	public boolean add(ClientHandler e) {
		if (size() + 1 > LIM_NATION) {
			return false;
		}
		super.add(e);
		e.setId(size() + 1);
		return true;
	}

	public void add(int index, ClientHandler e) {
		if (size() + 1 > LIM_NATION) {
			return;
		}
		if (index < 1 || index > LIM_NATION) {
			return;
		}
		super.add(index, e);
	}

	public boolean addAll(Collection<? extends ClientHandler> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(int index, Collection<? extends ClientHandler> c) {
		throw new UnsupportedOperationException();
	}

	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(Object o) {
		if (o instanceof ClientHandler)
			return super.contains(o);
		else
			return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ClientHandler get(int index) {
		if (index < 1 || index > LIM_NATION)
			throw new RuntimeException();
		return super.get(index);
	}

	@Override
	public int lastIndexOf(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
		if (o instanceof ClientHandler)
			return super.remove(o);
		return false;
	}

	@Override
	public ClientHandler remove(int index) {
		if (index < 1 || index > LIM_NATION)
			throw new RuntimeException();
		return super.remove(index);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ClientHandler set(int index, ClientHandler element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<ClientHandler> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}

	public ClientHandler get(String name) {
		ClientHandler handler = null;
		for (int i = 0; i < Configuration.LIM_NATION; i++) {
			handler = get(i);
			if (handler.playerName == name) {
				break;
			}
		}
		return handler;
	}
}
