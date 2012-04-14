package org.powerbot.game.api.wrappers.node;

import org.powerbot.game.api.wrappers.Identifiable;
import org.powerbot.game.bot.Context;
import org.powerbot.game.client.RSItemDefActions;
import org.powerbot.game.client.RSItemDefBooleans;
import org.powerbot.game.client.RSItemDefGroundActions;
import org.powerbot.game.client.RSItemDefID;
import org.powerbot.game.client.RSItemDefInts;
import org.powerbot.game.client.RSItemDefIsMembersObject;
import org.powerbot.game.client.RSItemDefName;

/**
 * @author Timer
 */
public class ItemDefinition implements Identifiable {
	private final Object definition;

	public ItemDefinition(final Object definition) {
		this.definition = definition;
	}

	public String getName() {
		try {
			return (String) ((RSItemDefName) definition).getRSItemDefName();
		} catch (final ClassCastException ignored) {
		}
		return null;
	}

	public int getId() {
		try {
			return ((RSItemDefID) ((RSItemDefInts) definition).getRSItemDefInts()).getRSItemDefID() * Context.resolve().multipliers.ITEMDEF_ID;
		} catch (final ClassCastException ignored) {
		}
		return -1;
	}

	public boolean isMembers() {
		try {
			return ((RSItemDefIsMembersObject) ((RSItemDefBooleans) definition).getRSItemDefBooleans()).getRSItemDefIsMembersObject();
		} catch (final ClassCastException ignored) {
		}
		return false;
	}

	public String[] getActions() {
		try {
			return (String[]) ((RSItemDefActions) definition).getRSItemDefActions();
		} catch (final ClassCastException ignored) {
		}
		return null;
	}

	public String[] getGroundActions() {
		try {
			return (String[]) ((RSItemDefGroundActions) definition).getRSItemDefGroundActions();
		} catch (final ClassCastException ignored) {
		}
		return null;
	}
}
