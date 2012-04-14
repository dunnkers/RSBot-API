package org.powerbot.game.api.util.node;

import org.powerbot.game.api.util.internal.Multipliers;
import org.powerbot.game.bot.Context;
import org.powerbot.game.client.HashTable2Buckets;
import org.powerbot.game.client.HashTableBuckets;
import org.powerbot.game.client.Node;

/**
 * @author Timer
 */
public class Nodes {
	public static Node lookup(final Object nc, final long id) {
		try {
			if (nc == null || id < 0L) {
				return null;
			}
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
			final Multipliers multipliers = Context.multipliers();
			final long multiplier = (((long) multipliers.NODE_ID) << 32) + ((multipliers.NODE_ID_p2 & 0xFFFFFFFFL));
			final Node n = buckets[(int) (id & buckets.length - 1)];
			for (Node node = n.getPrevious(); node != n; node = node.getPrevious()) {
				if (node.getID() * multiplier == id) {
					return node;
				}
			}
		} catch (final Exception ignored) {
		}
		return null;
	}
}
