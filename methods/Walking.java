package org.powerbot.game.api.methods;

import java.awt.Point;

import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.wrappers.Locatable;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.ViewportEntity;
import org.powerbot.game.api.wrappers.map.LocalPath;
import org.powerbot.game.api.wrappers.map.TilePath;
import org.powerbot.game.bot.Bot;
import org.powerbot.game.bot.Context;
import org.powerbot.game.client.RSGroundDataBlocks;
import org.powerbot.game.client.RSGroundDataInts;
import org.powerbot.game.client.RSGroundDataX;
import org.powerbot.game.client.RSGroundDataY;
import org.powerbot.game.client.RSInfoGroundData;

/**
 * A utility for the manipulation of information required for waking.
 *
 * @author Timer
 */
public class Walking {
	private static final int WIDGET = 750;
	private static final int WIDGET_RUN = 2;
	private static final int WIDGET_RUN_ENERGY = 6;

	public static Tile getDestination() {
		final Bot bot = Context.resolve();
		final int lx = (bot.getClient().getDestX() * bot.multipliers.GLOBAL_DESTX) / 4;
		final int ly = (bot.getClient().getDestY() * bot.multipliers.GLOBAL_DESTY) / 4;
		if (lx == -1 || ly == -1) {
			return new Tile(-1, -1, -1);
		}
		return new Tile(
				Game.getBaseX() + lx,
				Game.getBaseY() + ly,
				Game.getPlane()
		);
	}

	/**
	 * @param plane The plane of which to determine the collision offset for.
	 * @return The <code>Tile</code> of the offset location (different than map base!).
	 */
	public static Tile getCollisionOffset(final int plane) {
		final Bot bot = Context.resolve();
		final Object groundDataInts = ((RSGroundDataInts) ((Object[]) ((RSInfoGroundData) bot.getClient().getRSGroundInfo()).getRSInfoGroundData())[plane]).getRSGroundDataInts();
		return new Tile(((RSGroundDataX) groundDataInts).getRSGroundDataX() * bot.multipliers.GROUNDDATA_X, ((RSGroundDataY) groundDataInts).getRSGroundDataY() * bot.multipliers.GROUNDDATA_Y, plane);
	}

	/**
	 * @param plane The plane of which to provide the collision flags for.
	 * @return The collision flags of the current map block.
	 */
	public static int[][] getCollisionFlags(final int plane) {
		return (int[][]) ((RSGroundDataBlocks) ((Object[]) ((RSInfoGroundData) Context.resolve().getClient().getRSGroundInfo()).getRSInfoGroundData())[plane]).getRSGroundDataBlocks();
	}

	public static void setRun(final boolean enabled) {
		if (isRunEnabled() != enabled) {
			Widgets.get(WIDGET, WIDGET_RUN).click(true);
		}
	}

	public static boolean isRunEnabled() {
		return Settings.get(Settings.BOOLEAN_RUN_ENABLED) == 1;
	}

	public static int getEnergy() {
		try {
			return Integer.parseInt(Widgets.get(WIDGET, WIDGET_RUN_ENERGY).getText());
		} catch (final NumberFormatException ignored) {
			return -1;
		}
	}

	public static TilePath newTilePath(final Tile[] path) {
		return new TilePath(path);
	}

	public static LocalPath findPath(final Tile end) {
		return new LocalPath(end);
	}

	public static boolean walk(final Locatable mobile) {
		return walk(mobile.getLocation());
	}

	/**
	 * Clicks a tile on the minimap.
	 *
	 * @param stepDirection The tile to click (global).
	 * @return <tt>true</tt> if the tile was clicked; otherwise <tt>false</tt>.
	 */
	public static boolean walk(Tile stepDirection) {
		if (!stepDirection.isOnMap()) {
			stepDirection = getClosestOnMap(stepDirection);
		}
		final Tile tile = stepDirection;
		return Mouse.apply(
				new ViewportEntity() {
					public Point getCentralPoint() {
						return Calculations.worldToMap(tile.getX(), tile.getY());
					}

					public Point getNextViewportPoint() {
						return getCentralPoint();
					}

					public boolean contains(final Point point) {
						return getCentralPoint().distance(point) <= 2;
					}

					public boolean validate() {
						return Calculations.distance(tile, Players.getLocal().getLocation()) <= 17;
					}
				},
				new Filter<Point>() {
					public boolean accept(final Point point) {
						Mouse.click(true);
						return true;
					}
				}
		);
	}

	public static Tile getClosestOnMap(final Tile tile) {
		if (tile.isOnMap()) {
			return tile;
		}

		final Tile location = Players.getLocal().getLocation();
		final double angle = Math.atan2(tile.getY(), tile.getX());
		return new Tile(
				location.getX() + (int) (16d * Math.cos(angle)),
				location.getY() + (int) (16d * Math.sin(angle)),
				tile.getPlane()
		);
	}
}
