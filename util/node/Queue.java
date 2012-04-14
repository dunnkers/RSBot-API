package org.powerbot.game.api.util.node;

import org.powerbot.game.client.NodeSub;
import org.powerbot.game.client.NodeSubQueueTail;

/**
 * @param <N> Node type.
 * @author Timer
 */
public class Queue<N extends NodeSub> {
	private final Object nl;
	private NodeSub current;

	public Queue(final Object nl) {
		this.nl = nl;
	}

	public int size() {
		int size = 0;
		NodeSub node = ((NodeSub) ((NodeSubQueueTail) nl).getNodeSubQueueTail()).getPrevSub();

		while (node != ((NodeSubQueueTail) nl).getNodeSubQueueTail()) {
			node = node.getPrevSub();
			size++;
		}

		return size;
	}

	@SuppressWarnings("unchecked")
	public N getHead() {
		NodeSub node = ((NodeSub) ((NodeSubQueueTail) nl).getNodeSubQueueTail()).getNextSub();

		if (node == ((NodeSubQueueTail) nl).getNodeSubQueueTail()) {
			current = null;
			return null;
		}
		current = node.getNextSub();

		return (N) node;
	}

	@SuppressWarnings("unchecked")
	public N getNext() {
		NodeSub node = current;

		if (node == ((NodeSubQueueTail) nl).getNodeSubQueueTail()) {
			current = null;
			return null;
		}
		current = node.getNextSub();

		return (N) node;
	}
}
