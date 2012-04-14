package org.powerbot.game.api.wrappers.interactive;

import java.lang.ref.SoftReference;

import org.powerbot.game.api.util.internal.Multipliers;
import org.powerbot.game.bot.Context;
import org.powerbot.game.client.RSInteractableInts;
import org.powerbot.game.client.RSPlayerComposite;
import org.powerbot.game.client.RSPlayerCompositeEquipment;
import org.powerbot.game.client.RSPlayerCompositeInts;
import org.powerbot.game.client.RSPlayerCompositeNPCID;
import org.powerbot.game.client.RSPlayerLevel;
import org.powerbot.game.client.RSPlayerName;
import org.powerbot.game.client.RSPlayerPrayerIcon;
import org.powerbot.game.client.RSPlayerSkullIcon;
import org.powerbot.game.client.RSPlayerTeam;

/**
 * @author Timer
 */
public class Player extends Character {
	private final SoftReference<Object> p;
	private final Multipliers multipliers;

	public Player(final Object p) {
		this.p = new SoftReference<Object>(p);
		this.multipliers = Context.multipliers();
	}

	public int getLevel() {
		return ((RSPlayerLevel) ((RSInteractableInts) get()).getRSInteractableInts()).getRSPlayerLevel() * multipliers.PLAYER_LEVEL;
	}

	public String getName() {
		return (String) ((RSPlayerName) get()).getRSPlayerName();
	}

	public int getTeam() {
		return ((RSPlayerTeam) ((RSInteractableInts) get()).getRSInteractableInts()).getRSPlayerTeam() * multipliers.PLAYER_TEAM;
	}

	public int getPrayerIcon() {
		return ((RSPlayerPrayerIcon) ((RSInteractableInts) get()).getRSInteractableInts()).getRSPlayerPrayerIcon() * multipliers.PLAYER_PRAYERICON;
	}

	public int getSkullIcon() {
		return ((RSPlayerSkullIcon) ((RSInteractableInts) get()).getRSInteractableInts()).getRSPlayerSkullIcon() * multipliers.PLAYER_SKULLICON;
	}

	public int getNpcId() {
		return ((RSPlayerCompositeNPCID) ((RSPlayerCompositeInts) ((RSPlayerComposite) get()).getRSPlayerComposite()).getRSPlayerCompositeInts()).getRSPlayerCompositeNPCID() * multipliers.PLAYERCOMPOSITE_NPCID;
	}

	public int getId() {
		return getName().hashCode();
	}

	public int[] getAppearance() {
		final Object composite = ((RSPlayerComposite) get()).getRSPlayerComposite();
		if (composite != null) {
			final int[] appearance = (int[]) ((RSPlayerCompositeEquipment) composite).getRSPlayerCompositeEquipment();
			for (int i = 0; i < appearance.length; i++) {
				if ((appearance[i] & 0x40000000) != 0) {
					appearance[i] &= 0x3fffffff;
				} else {
					appearance[i] = -1;
				}
			}
			return appearance;
		}
		return null;
	}

	public Object get() {
		return p.get();
	}
}
