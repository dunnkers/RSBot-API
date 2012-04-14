package org.powerbot.game.api.methods;

import org.powerbot.game.bot.Context;
import org.powerbot.game.client.SettingsData;

/**
 * A utility for the manipulation of game settings.
 *
 * @author Timer
 */
public class Settings {
	public static final int BOOLEAN_RUN_ENABLED = 173;
	public static final int VALUE_RANDOMEVENT_DRILLDEMON_MAT = 531;

	/**
	 * @return The <code>int[]</code> of all the game's settings in their respective positioning.
	 */
	public static int[] get() {
		return ((int[]) ((SettingsData) Context.resolve().getClient().getSettingArray()).getSettingsData()).clone();
	}

	/**
	 * @param index The position of this setting in the game's database.
	 * @return The setting value of the desired index.
	 */
	public static int get(final int index) {
		final int[] settings = get();
		if (index < settings.length) {
			return settings[index];
		}
		return -1;
	}
}
