package org.powerbot.game.api.methods;

import java.awt.Canvas;
import java.awt.Dimension;

import org.powerbot.game.api.util.internal.Constants;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.bot.Bot;
import org.powerbot.game.bot.Context;
import org.powerbot.game.client.BaseInfoInts;
import org.powerbot.game.client.BaseInfoX;
import org.powerbot.game.client.BaseInfoY;
import org.powerbot.game.client.RSInfoBaseInfo;

/**
 * A utility for the manipulation of the game.
 *
 * @author Timer
 */
public class Game {
	public static final int INDEX_LOGIN_SCREEN = 3;
	public static final int INDEX_LOBBY_SCREEN = 7;
	public static final int INDEX_LOGGING_IN = 9;
	public static final int INDEX_MAP_LOADED = 11;
	public static final int INDEX_MAP_LOADING = 12;
	public static final int[] INDEX_LOGGED_IN = {INDEX_MAP_LOADED, INDEX_MAP_LOADING};

	/**
	 * @return The current state of the game client.
	 */
	public static int getClientState() {
		final Bot bot = Context.resolve();
		final Constants constants = bot.constants;
		final int clientState = bot.getClient().getLoginIndex() * bot.multipliers.GLOBAL_LOGININDEX;
		if (clientState == constants.CLIENTSTATE_3) {
			return 3;
		} else if (clientState == constants.CLIENTSTATE_6) {
			return 6;
		} else if (clientState == constants.CLIENTSTATE_7) {
			return 7;
		} else if (clientState == constants.CLIENTSTATE_9) {
			return 9;
		} else if (clientState == constants.CLIENTSTATE_10) {
			return 10;
		} else if (clientState == constants.CLIENTSTATE_11) {
			return 11;
		} else if (clientState == constants.CLIENTSTATE_12) {
			return 12;
		}
		return -1;
	}

	/**
	 * @return <tt>true</tt> if this client instance is logged in; otherwise <tt>false</tt>.
	 */
	public static boolean isLoggedIn() {
		final int state = getClientState();
		for (final int p_state : INDEX_LOGGED_IN) {
			if (state == p_state) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return The floor level, or plane, you are currently located on.
	 */
	public static int getPlane() {
		final Bot bot = Context.resolve();
		return bot.getClient().getPlane() * bot.multipliers.GLOBAL_PLANE;
	}

	/**
	 * @return The x location of the currently loaded map base.
	 */
	public static int getBaseX() {
		final Bot bot = Context.resolve();
		return (((BaseInfoX) ((BaseInfoInts) ((RSInfoBaseInfo) bot.getClient().getRSGroundInfo()).getRSInfoBaseInfo()).getBaseInfoInts()).getBaseInfoX() * bot.multipliers.BASEDATA_X) >> 8;
	}

	/**
	 * @return The y location of the currently loaded map base.
	 */
	public static int getBaseY() {
		final Bot bot = Context.resolve();
		return (((BaseInfoY) ((BaseInfoInts) ((RSInfoBaseInfo) bot.getClient().getRSGroundInfo()).getRSInfoBaseInfo()).getBaseInfoInts()).getBaseInfoY() * bot.multipliers.BASEDATA_Y) >> 8;
	}

	public static Tile getMapBase() {
		final Bot bot = Context.resolve();
		final Object infoInts = ((BaseInfoInts) ((RSInfoBaseInfo) bot.getClient().getRSGroundInfo()).getRSInfoBaseInfo()).getBaseInfoInts();
		return new Tile(
				(((BaseInfoX) infoInts).getBaseInfoX() * bot.multipliers.BASEDATA_X) >> 8,
				(((BaseInfoY) infoInts).getBaseInfoY() * bot.multipliers.BASEDATA_Y) >> 8,
				Game.getPlane()
		);
	}

	public static int getLoopCycle() {
		final Bot bot = Context.resolve();
		return bot.getClient().getLoopCycle() * bot.multipliers.GLOBAL_LOOPCYCLE;
	}

	public static Dimension getDimensions() {
		final Canvas canvas = Context.resolve().getCanvas();
		return new Dimension(canvas.getWidth(), canvas.getHeight());
	}
}
