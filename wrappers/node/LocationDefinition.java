package org.powerbot.game.api.wrappers.node;

import org.powerbot.game.api.wrappers.Identifiable;
import org.powerbot.game.bot.Context;
import org.powerbot.game.client.RSObjectDefActions;
import org.powerbot.game.client.RSObjectDefID;
import org.powerbot.game.client.RSObjectDefInts;
import org.powerbot.game.client.RSObjectDefName;

/**
 * @author Timer
 */
public class LocationDefinition implements Identifiable {
	private final Object def;
	private final int id_multiplier;

	public LocationDefinition(final Object def) {
		this.def = def;
		this.id_multiplier = Context.multipliers().OBJECTDEF_ID;
	}

	public String getName() {
		return (String) ((RSObjectDefName) def).getRSObjectDefName();
	}

	public String[] getActions() {
		return (String[]) ((RSObjectDefActions) def).getRSObjectDefActions();
	}

	public int getId() {
		return ((RSObjectDefID) ((RSObjectDefInts) def).getRSObjectDefInts()).getRSObjectDefID() * id_multiplier;
	}
}
