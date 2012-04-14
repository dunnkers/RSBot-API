package org.powerbot.game.api.util.node;

import org.powerbot.game.client.HashTable2Buckets;
import org.powerbot.game.client.HashTableBuckets;
import org.powerbot.game.client.Node;

/**
 * @author Timer
 */
public class HashTable {
	private final Object nc;
	private Node current;
	private int c_index = 0;

	public HashTable(final Object hashTable) {
		nc = hashTable;
	}

	public Node getFirst() {
		c_index = 0;
		return getNext();
	}

	public Node getNext() {
		final Node[] buckets;
		Object o;
		if (nc instanceof HashTableBuckets && ((o = ((HashTableBuckets) nc).getHashTableBuckets()) instanceof Node[])) {
			buckets = (Node[]) o;
		} else if (nc instanceof HashTable2Buckets && ((o = ((HashTable2Buckets) nc).getHashTable2Buckets()) instanceof Node[])) {
			buckets = (Node[]) o;
		} else {
			buckets = null;
		}
		if (buckets == null) {
			return null;
		}
		if (c_index > 0 && buckets[c_index - 1] != current) {
			final Node node = current;
			current = node.getPrevious();
			return node;
		}
		while (c_index < buckets.length) {
			final Node node = buckets[c_index++].getPrevious();
			if (buckets[c_index - 1] != node) {
				current = node.getPrevious();
				return node;
			}
		}
		return null;
	}
}