package org.powerbot.game.api.methods.interactive;

import java.util.HashSet;
import java.util.Set;

import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.util.node.Nodes;
import org.powerbot.game.api.wrappers.RegionOffset;
import org.powerbot.game.api.wrappers.interactive.NPC;
import org.powerbot.game.bot.Context;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.Node;
import org.powerbot.game.client.RSNPCHolder;
import org.powerbot.game.client.RSNPCNode;
import org.powerbot.game.client.RSNPCNodeHolder;

/**
 * A utility for the access of Npcs.
 *
 * @author Timer
 */
public class NPCs {
	public static final Filter<NPC> ALL_FILTER = new Filter<NPC>() {
		public boolean accept(final NPC player) {
			return true;
		}
	};

	/**
	 * @return An array of the currently loaded Npcs in the game.
	 */
	public static NPC[] getLoaded() {
		return getLoaded(ALL_FILTER);
	}

	public static NPC[] getLoaded(final int... ids) {
		return getLoaded(new Filter<NPC>() {
			public boolean accept(final NPC npc) {
				for (final int id : ids) {
					if (npc.getId() == id) {
						return true;
					}
				}
				return false;
			}
		});
	}

	/**
	 * @param filter The filtering <code>Filter</code> to accept all the Npcs through.
	 * @return An array of the currently loaded Npcs in the game that are accepted by the provided filter.
	 */
	public static NPC[] getLoaded(final Filter<NPC> filter) {
		final Client client = Context.client();
		final int[] indices = client.getRSNPCIndexArray();
		final Set<NPC> npcs = new HashSet<NPC>();
		for (final int index : indices) {
			final Node node = Nodes.lookup(client.getRSNPCNC(), index);
			if (node != null && node instanceof RSNPCNode) {
				final NPC npc = new NPC(((RSNPCHolder) ((RSNPCNodeHolder) ((RSNPCNode) node).getData()).getRSNPCNodeHolder()).getRSNPC());
				if (filter.accept(npc)) {
					npcs.add(npc);
				}
			}
		}
		return npcs.toArray(new NPC[npcs.size()]);
	}

	public static NPC getNearest(final int... ids) {
		return getNearest(new Filter<NPC>() {
			public boolean accept(final NPC npc) {
				for (final int id : ids) {
					if (id == npc.getId()) {
						return true;
					}
				}
				return false;
			}
		});
	}

	public static NPC getNearest(final Filter<NPC> filter) {
		final Client client = Context.client();
		final int[] indices = client.getRSNPCIndexArray();
		NPC npc = null;
		double distance = Double.MAX_VALUE;
		final RegionOffset position = Players.getLocal().getRegionOffset();
		for (final int index : indices) {
			final Node node = Nodes.lookup(client.getRSNPCNC(), index);
			if (node != null && node instanceof RSNPCNode) {
				final NPC t_npc = new NPC(((RSNPCHolder) ((RSNPCNodeHolder) ((RSNPCNode) node).getData()).getRSNPCNodeHolder()).getRSNPC());
				if (filter.accept(t_npc)) {
					final double dist = Calculations.distance(position, t_npc.getRegionOffset());
					if (dist < distance) {
						distance = dist;
						npc = t_npc;
					}
				}
			}
		}
		return npc;
	}
}
