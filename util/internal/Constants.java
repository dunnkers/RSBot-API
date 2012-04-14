package org.powerbot.game.api.util.internal;

import java.util.Map;

/**
 * Represents constants of states or possibilities.
 *
 * @author Timer
 */
public class Constants {
	public final int
			CLIENTSTATE_3, CLIENTSTATE_6, CLIENTSTATE_7,
			CLIENTSTATE_9, CLIENTSTATE_10, CLIENTSTATE_11,
			CLIENTSTATE_12,
			VIEWPORT_XOFF, VIEWPORT_XX, VIEWPORT_XY, VIEWPORT_XZ,
			VIEWPORT_YOFF, VIEWPORT_YX, VIEWPORT_YY, VIEWPORT_YZ,
			VIEWPORT_ZOFF, VIEWPORT_ZX, VIEWPORT_ZY, VIEWPORT_ZZ;

	public Constants(final Map<Integer, Integer> constants) {
		CLIENTSTATE_3 = constants.get(3);
		CLIENTSTATE_6 = constants.get(6);
		CLIENTSTATE_7 = constants.get(7);
		CLIENTSTATE_9 = constants.get(9);
		CLIENTSTATE_10 = constants.get(10);
		CLIENTSTATE_11 = constants.get(11);
		CLIENTSTATE_12 = constants.get(12);

		VIEWPORT_XOFF = constants.get(20);
		VIEWPORT_XX = constants.get(21);
		VIEWPORT_XY = constants.get(22);
		VIEWPORT_XZ = constants.get(23);
		VIEWPORT_YOFF = constants.get(24);
		VIEWPORT_YX = constants.get(25);
		VIEWPORT_YY = constants.get(26);
		VIEWPORT_YZ = constants.get(27);
		VIEWPORT_ZOFF = constants.get(28);
		VIEWPORT_ZX = constants.get(29);
		VIEWPORT_ZY = constants.get(30);
		VIEWPORT_ZZ = constants.get(31);
	}
}
