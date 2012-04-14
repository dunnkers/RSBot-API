package org.powerbot.game.api.wrappers.node;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.lang.ref.SoftReference;

import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.util.node.Nodes;
import org.powerbot.game.api.wrappers.Area;
import org.powerbot.game.api.wrappers.Entity;
import org.powerbot.game.api.wrappers.Identifiable;
import org.powerbot.game.api.wrappers.Locatable;
import org.powerbot.game.api.wrappers.RegionOffset;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.graphics.CapturedModel;
import org.powerbot.game.api.wrappers.graphics.model.LocationModel;
import org.powerbot.game.bot.Context;
import org.powerbot.game.client.CacheTable;
import org.powerbot.game.client.HardReferenceGet;
import org.powerbot.game.client.Model;
import org.powerbot.game.client.ModelCapture;
import org.powerbot.game.client.Node;
import org.powerbot.game.client.RSAnimableShorts;
import org.powerbot.game.client.RSAnimableX1;
import org.powerbot.game.client.RSAnimableX2;
import org.powerbot.game.client.RSAnimableY1;
import org.powerbot.game.client.RSAnimableY2;
import org.powerbot.game.client.RSInfoRSObjectDefLoaders;
import org.powerbot.game.client.RSInteractableLocation;
import org.powerbot.game.client.RSInteractableManager;
import org.powerbot.game.client.RSInteractableRSInteractableManager;
import org.powerbot.game.client.RSObjectDefLoaderCache;
import org.powerbot.game.client.Reference;
import org.powerbot.game.client.SoftReferenceGet;

/**
 * @author Timer
 */
public class SceneObject implements Entity, Locatable, Identifiable {
	private final Object object;
	private final Type type;
	private final int plane;

	public static enum Type {
		INTERACTIVE, FLOOR_DECORATION, BOUNDARY, WALL_DECORATION
	}

	public SceneObject(final Object obj, final Type type, final int plane) {
		this.object = obj;
		this.type = type;
		this.plane = plane;
	}

	public Area getArea() {
		if (object instanceof RSAnimableShorts) {
			final Object shorts = ((RSAnimableShorts) object).getRSAnimableShorts();
			if (shorts instanceof RSAnimableX1 &&
					shorts instanceof RSAnimableY1 &&
					shorts instanceof RSAnimableX2 &&
					shorts instanceof RSAnimableY2) {
				final int bX = Game.getBaseX(), bY = Game.getBaseY();
				final Tile tile1 = new Tile(
						bX + (int) ((RSAnimableX1) shorts).getRSAnimableX1(),
						bY + (int) ((RSAnimableY1) shorts).getRSAnimableY1(),
						plane
				);
				final Tile tile2 = new Tile(
						bX + (int) ((RSAnimableX2) shorts).getRSAnimableX2(),
						bY + (int) ((RSAnimableY2) shorts).getRSAnimableY2(),
						plane
				);
				return new Area(tile1, tile2);
			}
		}
		return null;
	}

	public int getId() {
		return Context.client().getRSObjectID(object);
	}

	public Type getType() {
		return type;
	}

	public int getPlane() {
		return plane;
	}

	public Object getInstance() {
		return object;
	}

	public RegionOffset getRegionOffset() {
		final RSInteractableLocation location = ((RSInteractableManager) ((RSInteractableRSInteractableManager) object).getRSInteractableRSInteractableManager()).getData().getLocation();
		return new RegionOffset((int) location.getX() / 512, (int) location.getY() / 512, plane);
	}

	public Tile getLocation() {
		final RegionOffset localTile = getRegionOffset();
		return new Tile(Game.getBaseX() + localTile.getX(), Game.getBaseY() + localTile.getY(), localTile.getPlane());
	}

	public LocationDefinition getDefinition() {
		final Object object = ((RSInfoRSObjectDefLoaders) Context.client().getRSGroundInfo()).getRSInfoRSObjectDefLoaders();
		final Object objectDefLoader = ((CacheTable) ((RSObjectDefLoaderCache) object).getRSObjectDefLoaderCache()).getCacheTable();
		final Node ref = Nodes.lookup(objectDefLoader, getId());
		if (ref != null && ref instanceof Reference) {
			final Object reference = ((Reference) ref).getData();
			if (reference instanceof SoftReferenceGet) {
				final Object soft = ((SoftReferenceGet) reference).getSoftReferenceGet();
				if (soft != null) {
					return new LocationDefinition(soft instanceof SoftReference ? ((SoftReference<?>) soft).get() : soft);
				}
			} else if (reference instanceof HardReferenceGet) {
				return new LocationDefinition(((HardReferenceGet) reference).getHardReferenceGet());
			}
		}
		return null;
	}

	public CapturedModel getModel() {
		if (object != null) {
			Model model = Context.client().getRSObjectModel(object);
			if (model == null) {
				model = ModelCapture.modelCache.get(object);
			}
			if (model != null) {
				return new LocationModel(model, this);
			}
		}
		return null;
	}

	public boolean validate() {
		return getId() != -1;
	}

	public Point getCentralPoint() {
		final CapturedModel model = getModel();
		return model != null ? model.getCentralPoint() : getLocation().getCentralPoint();
	}

	public Point getNextViewportPoint() {
		final CapturedModel model = getModel();
		return model != null ? model.getNextViewportPoint() : getLocation().getNextViewportPoint();
	}

	public boolean contains(final Point point) {
		final CapturedModel model = getModel();
		return model != null ? model.contains(point) : getLocation().contains(point);
	}

	public boolean isOnScreen() {
		final CapturedModel model = getModel();
		return model != null ? model.isOnScreen() : getLocation().isOnScreen();
	}

	public Polygon[] getBounds() {
		final CapturedModel model = getModel();
		return model != null ? model.getBounds() : getLocation().getBounds();
	}

	public boolean hover() {
		final CapturedModel model = getModel();
		return model != null ? model.hover() : getLocation().hover();
	}

	public boolean click(final boolean left) {
		final CapturedModel model = getModel();
		return model != null ? model.click(left) : getLocation().click(left);
	}

	public boolean interact(final String action) {
		final CapturedModel model = getModel();
		return model != null ? model.interact(action) : getLocation().interact(action);
	}

	public boolean interact(final String action, final String option) {
		final CapturedModel model = getModel();
		return model != null ? model.interact(action, option) : getLocation().interact(action, option);
	}

	public void draw(final Graphics render) {
		//TODO color
		final CapturedModel model = getModel();
		if (model != null) {
			model.draw(render);
		} else {
			getLocation().draw(render);
		}
	}

	@Override
	public String toString() {
		return "location[ref=" + System.identityHashCode(object) + ",id=" + getId() + ",plane=" + plane + ",type=" + type.toString() + "]";
	}
}
