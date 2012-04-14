package org.powerbot.game.api.wrappers.widget;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;

import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.node.Menu;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.internal.Multipliers;
import org.powerbot.game.api.util.node.HashTable;
import org.powerbot.game.api.wrappers.Entity;
import org.powerbot.game.api.wrappers.Identifiable;
import org.powerbot.game.bot.Context;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.RSInterfaceActions;
import org.powerbot.game.client.RSInterfaceBooleans;
import org.powerbot.game.client.RSInterfaceBorderThinkness;
import org.powerbot.game.client.RSInterfaceBoundsArrayIndex;
import org.powerbot.game.client.RSInterfaceComponentID;
import org.powerbot.game.client.RSInterfaceComponentIndex;
import org.powerbot.game.client.RSInterfaceComponentName;
import org.powerbot.game.client.RSInterfaceComponentStackSize;
import org.powerbot.game.client.RSInterfaceComponents;
import org.powerbot.game.client.RSInterfaceHeight;
import org.powerbot.game.client.RSInterfaceHorizontalScrollbarPosition;
import org.powerbot.game.client.RSInterfaceHorizontalScrollbarSize;
import org.powerbot.game.client.RSInterfaceHorizontalScrollbarThumbSize;
import org.powerbot.game.client.RSInterfaceID;
import org.powerbot.game.client.RSInterfaceInts;
import org.powerbot.game.client.RSInterfaceIsHidden;
import org.powerbot.game.client.RSInterfaceIsHorizontallyFlipped;
import org.powerbot.game.client.RSInterfaceIsInventoryInterface;
import org.powerbot.game.client.RSInterfaceIsVerticallyFlipped;
import org.powerbot.game.client.RSInterfaceModelID;
import org.powerbot.game.client.RSInterfaceModelType;
import org.powerbot.game.client.RSInterfaceModelZoom;
import org.powerbot.game.client.RSInterfaceNode;
import org.powerbot.game.client.RSInterfaceNodeInts;
import org.powerbot.game.client.RSInterfaceNodeMainID;
import org.powerbot.game.client.RSInterfaceParentID;
import org.powerbot.game.client.RSInterfaceSelectedActionName;
import org.powerbot.game.client.RSInterfaceShadowColor;
import org.powerbot.game.client.RSInterfaceSpecialType;
import org.powerbot.game.client.RSInterfaceText;
import org.powerbot.game.client.RSInterfaceTextColor;
import org.powerbot.game.client.RSInterfaceTextureID;
import org.powerbot.game.client.RSInterfaceTooltip;
import org.powerbot.game.client.RSInterfaceType;
import org.powerbot.game.client.RSInterfaceVerticalScrollbarPosition;
import org.powerbot.game.client.RSInterfaceVerticalScrollbarSize;
import org.powerbot.game.client.RSInterfaceVerticalScrollbarThumbSize;
import org.powerbot.game.client.RSInterfaceWidth;
import org.powerbot.game.client.RSInterfaceX;
import org.powerbot.game.client.RSInterfaceXRotation;
import org.powerbot.game.client.RSInterfaceY;
import org.powerbot.game.client.RSInterfaceYRotation;
import org.powerbot.game.client.RSInterfaceZRotation;

/**
 * @author Timer
 */
public class WidgetChild implements Entity, Identifiable {
	/**
	 * The index of this interface in the parent. If this
	 * component does not have a parent component, this
	 * represents the index in the parent interface;
	 * otherwise this represents the component index in
	 * the parent component.
	 */
	private final int index;

	/**
	 * The parent interface containing this component.
	 */
	private final Widget parentWidget;

	/**
	 * The parent component
	 */
	private final WidgetChild parent;

	/**
	 * Initializes the component.
	 *
	 * @param parent The parent interface.
	 * @param index  The child index of this child.
	 */
	public WidgetChild(final Widget parent, final int index) {
		parentWidget = parent;
		this.index = index;
		this.parent = null;
	}

	/**
	 * Initializes the component.
	 *
	 * @param parentWidget    The parent interface.
	 * @param parentComponent The parent component.
	 * @param index           The child index of this child.
	 */
	public WidgetChild(final Widget parentWidget, final WidgetChild parentComponent, final int index) {
		this.parentWidget = parentWidget;
		this.parent = parentComponent;
		this.index = index;
	}

	/**
	 * Gets the parent widget of this component.  This component may be nested from its parent widget in parent components.
	 *
	 * @return The parent widget.
	 */
	public Widget getWidget() {
		return parentWidget;
	}

	/**
	 * Gets the parent component of this component, or null if this is a top-level component.
	 *
	 * @return The parent component, or null.
	 */
	public WidgetChild getParent() {
		return parent;
	}

	public int getIndex() {
		return index;
	}

	public int getAbsoluteX() {
		return getAbsoluteLocation().x;
	}

	public int getAbsoluteY() {
		return getAbsoluteLocation().y;
	}

	public Point getAbsoluteLocation() {
		if (getInternal() == null) {
			return new Point(-1, -1);
		}
		final Client client = Context.client();
		final int parentId = getParentId();
		int x = 0, y = 0;
		if (parentId != -1) {
			final Point point = Widgets.get(parentId >> 0x10, parentId & 0xffff).getAbsoluteLocation();
			x = point.x;
			y = point.y;
		} else {
			final Rectangle[] bounds = client.getRSInterfaceBoundsArray();
			final int index = getBoundsArrayIndex();
			if (bounds != null && index > 0 && index < bounds.length && bounds[index] != null) {
				return new Point(bounds[index].x, bounds[index].y);
			}
			//x = getMasterX();
			//y = getMasterY();
		}
		if (parentId != -1) {
			final WidgetChild child = Widgets.get(parentId >> 0x10, parentId & 0xffff);
			final int horizontalScrollSize = child.getScrollableContentWidth(), verticalScrollSize = child.getScrollableContentHeight();
			if (horizontalScrollSize > 0 || verticalScrollSize > 0) {
				x -= horizontalScrollSize;
				y -= verticalScrollSize;
			}
		}
		x += getRelativeX();
		y += getRelativeY();
		return new Point(x, y);
	}

	public int getRelativeX() {
		final Object widget = getInternal();
		return widget != null ? ((RSInterfaceX) ((RSInterfaceInts) widget).getRSInterfaceInts()).getRSInterfaceX() * Context.multipliers().INTERFACE_X : -1;
	}

	public int getRelativeY() {
		final Object widget = getInternal();
		return widget != null ? ((RSInterfaceY) ((RSInterfaceInts) widget).getRSInterfaceInts()).getRSInterfaceY() * Context.multipliers().INTERFACE_Y : -1;
	}

	public Point getRelativeLocation() {
		final Object widget = getInternal();
		if (widget == null) {
			return new Point(-1, -1);
		}
		final Object rsInterfaceInts = ((RSInterfaceInts) widget).getRSInterfaceInts();
		final Multipliers multipliers = Context.multipliers();
		return new Point(
				((RSInterfaceX) rsInterfaceInts).getRSInterfaceX() * multipliers.INTERFACE_X,
				((RSInterfaceY) rsInterfaceInts).getRSInterfaceY() * multipliers.INTERFACE_Y
		);
	}

	public int getWidth() {
		if (!isInScrollableArea()) {
			return getHorizontalScrollThumbSize();
		}
		final Object widget = getInternal();
		return widget != null ? (((RSInterfaceWidth) ((RSInterfaceInts) widget).getRSInterfaceInts()).getRSInterfaceWidth() * Context.multipliers().INTERFACE_WIDTH) - 4 : -1;
	}

	public int getHeight() {
		if (!isInScrollableArea()) {
			return getVerticalScrollThumbSize();
		}
		final Object widget = getInternal();
		return widget != null ? (((RSInterfaceHeight) ((RSInterfaceInts) widget).getRSInterfaceInts()).getRSInterfaceHeight() * Context.multipliers().INTERFACE_HEIGHT) - 4 : -1;
	}

	public int getId() {
		final Object widget = getInternal();
		return widget != null ? ((RSInterfaceID) ((RSInterfaceInts) widget).getRSInterfaceInts()).getRSInterfaceID() * Context.multipliers().INTERFACE_ID : -1;
	}

	public int getType() {
		final Object widget = getInternal();
		return widget != null ? ((RSInterfaceType) ((RSInterfaceInts) widget).getRSInterfaceInts()).getRSInterfaceType() * Context.multipliers().INTERFACE_TYPE : -1;
	}

	public int getSpecialType() {
		final Object widget = getInternal();
		return widget != null ? ((RSInterfaceSpecialType) ((RSInterfaceInts) widget).getRSInterfaceInts()).getRSInterfaceSpecialType() * Context.multipliers().INTERFACE_SPECIALTYPE : -1;
	}

	public int getChildId() {
		final Object widget = getInternal();
		return widget != null ? ((RSInterfaceComponentID) ((RSInterfaceInts) widget).getRSInterfaceInts()).getRSInterfaceComponentID() * Context.multipliers().INTERFACE_COMPONENTID : -1;
	}

	public int getChildIndex() {
		final Object widget = getInternal();
		return widget != null ? ((RSInterfaceComponentIndex) ((RSInterfaceInts) widget).getRSInterfaceInts()).getRSInterfaceComponentIndex() * Context.multipliers().INTERFACE_COMPONENTINDEX : -1;
	}

	public String getChildName() {
		final Object widget = getInternal();
		return widget != null ? (String) ((RSInterfaceComponentName) widget).getRSInterfaceComponentName() : null;
	}

	public int getTextureId() {
		final Object widget = getInternal();
		return widget != null ? ((RSInterfaceTextureID) ((RSInterfaceInts) widget).getRSInterfaceInts()).getRSInterfaceTextureID() * Context.multipliers().INTERFACE_TEXTUREID : -1;
	}

	public String getText() {
		final Object widget = getInternal();
		return widget != null ? (String) ((RSInterfaceText) widget).getRSInterfaceText() : null;
	}

	public int getTextColor() {
		final Object widget = getInternal();
		return widget != null ? ((RSInterfaceTextColor) ((RSInterfaceInts) widget).getRSInterfaceInts()).getRSInterfaceTextColor() * Context.multipliers().INTERFACE_TEXTCOLOR : -1;
	}

	public int getShadowColor() {
		final Object widget = getInternal();
		return widget != null ? ((RSInterfaceShadowColor) ((RSInterfaceInts) widget).getRSInterfaceInts()).getRSInterfaceShadowColor() * Context.multipliers().INTERFACE_SHADOWCOLOR : -1;
	}

	public String getTooltip() {
		final Object widget = getInternal();
		return widget != null ? (String) ((RSInterfaceTooltip) widget).getRSInterfaceTooltip() : null;
	}

	public int getBorderThickness() {
		final Object widget = getInternal();
		return widget != null ? ((RSInterfaceBorderThinkness) ((RSInterfaceInts) widget).getRSInterfaceInts()).getRSInterfaceBorderThinkness() * Context.multipliers().INTERFACE_BORDERTHICKNESS : -1;
	}

	public String getSelectedAction() {
		final Object widget = getInternal();
		return widget != null ? (String) ((RSInterfaceSelectedActionName) widget).getRSInterfaceSelectedActionName() : null;
	}

	public int getModelId() {
		final Object widget = getInternal();
		return widget != null ? ((RSInterfaceModelID) ((RSInterfaceInts) widget).getRSInterfaceInts()).getRSInterfaceModelID() * Context.multipliers().INTERFACE_MODELID : -1;
	}

	public int getModelType() {
		final Object widget = getInternal();
		return widget != null ? ((RSInterfaceModelType) ((RSInterfaceInts) widget).getRSInterfaceInts()).getRSInterfaceModelType() * Context.multipliers().INTERFACE_MODELTYPE : -1;
	}

	public int getModelZoom() {
		final Object widget = getInternal();
		return widget != null ? ((RSInterfaceModelZoom) ((RSInterfaceInts) widget).getRSInterfaceInts()).getRSInterfaceModelZoom() * Context.multipliers().INTERFACE_MODELZOOM : -1;
	}

	public boolean isInventory() {
		final Object widget = getInternal();
		return widget != null && ((RSInterfaceIsInventoryInterface) ((RSInterfaceBooleans) widget).getRSInterfaceBooleans()).getRSInterfaceIsInventoryInterface();
	}

	public int getChildStackSize() {
		final Object widget = getInternal();
		return widget != null ? ((RSInterfaceComponentStackSize) ((RSInterfaceInts) widget).getRSInterfaceInts()).getRSInterfaceComponentStackSize() * Context.multipliers().INTERFACE_COMPONENTSTACKSIZE : -1;
	}

	public int getXRotation() {
		final Object widget = getInternal();
		return widget != null ? ((RSInterfaceXRotation) ((RSInterfaceInts) widget).getRSInterfaceInts()).getRSInterfaceXRotation() * Context.multipliers().INTERFACE_XROTATION : -1;
	}

	public int getYRotation() {
		final Object widget = getInternal();
		return widget != null ? ((RSInterfaceYRotation) ((RSInterfaceInts) widget).getRSInterfaceInts()).getRSInterfaceYRotation() * Context.multipliers().INTERFACE_YROTATION : -1;
	}

	public int getZRotation() {
		final Object widget = getInternal();
		return widget != null ? ((RSInterfaceZRotation) ((RSInterfaceInts) widget).getRSInterfaceInts()).getRSInterfaceZRotation() * Context.multipliers().INTERFACE_ZROTATION : -1;
	}

	public String[] getActions() {
		final Object widget = getInternal();
		return widget != null ? (String[]) ((RSInterfaceActions) widget).getRSInterfaceActions() : null;
	}

	public boolean isHorizontallyFlipped() {
		final Object widget = getInternal();
		return widget != null && ((RSInterfaceIsHorizontallyFlipped) ((RSInterfaceBooleans) widget).getRSInterfaceBooleans()).getRSInterfaceIsHorizontallyFlipped();
	}

	public boolean isVerticallyFlipped() {
		final Object widget = getInternal();
		return widget != null && ((RSInterfaceIsVerticallyFlipped) ((RSInterfaceBooleans) widget).getRSInterfaceBooleans()).getRSInterfaceIsVerticallyFlipped();
	}

	public int getHorizontalScrollPosition() {
		final Object widget = getInternal();
		return widget != null ? ((RSInterfaceHorizontalScrollbarPosition) ((RSInterfaceInts) widget).getRSInterfaceInts()).getRSInterfaceHorizontalScrollbarPosition() * Context.multipliers().INTERFACE_HORIZONTALSCROLLBARSIZE : -1;
	}

	public int getScrollableContentWidth() {
		final Object widget = getInternal();
		return widget != null ? ((RSInterfaceHorizontalScrollbarSize) ((RSInterfaceInts) widget).getRSInterfaceInts()).getRSInterfaceHorizontalScrollbarSize() * Context.multipliers().INTERFACE_HORIZONTALSCROLLBARSIZE : -1;
	}

	public int getHorizontalScrollThumbSize() {
		final Object widget = getInternal();
		return widget != null ? ((RSInterfaceHorizontalScrollbarThumbSize) ((RSInterfaceInts) widget).getRSInterfaceInts()).getRSInterfaceHorizontalScrollbarThumbSize() * Context.multipliers().INTERFACE_HORIZONTALSCROLLBARTHUMBSIZE : -1;
	}

	public int getVerticalScrollPosition() {
		final Object widget = getInternal();
		return widget != null ? ((RSInterfaceVerticalScrollbarPosition) ((RSInterfaceInts) widget).getRSInterfaceInts()).getRSInterfaceVerticalScrollbarPosition() * Context.multipliers().INTERFACE_VERTICALSCROLLBARSIZE : -1;
	}

	public int getScrollableContentHeight() {
		final Object widget = getInternal();
		return widget != null ? ((RSInterfaceVerticalScrollbarSize) ((RSInterfaceInts) widget).getRSInterfaceInts()).getRSInterfaceVerticalScrollbarSize() * Context.multipliers().INTERFACE_VERTICALSCROLLBARSIZE : -1;
	}

	public int getVerticalScrollThumbSize() {
		final Object widget = getInternal();
		return widget != null ? ((RSInterfaceVerticalScrollbarThumbSize) ((RSInterfaceInts) widget).getRSInterfaceInts()).getRSInterfaceVerticalScrollbarThumbSize() * Context.multipliers().INTERFACE_VERTICALSCROLLBARTHUMBSIZE : -1;
	}

	public int getBoundsArrayIndex() {
		final Object widget = getInternal();
		return widget != null ? ((RSInterfaceBoundsArrayIndex) ((RSInterfaceInts) widget).getRSInterfaceInts()).getRSInterfaceBoundsArrayIndex() * Context.multipliers().INTERFACE_BOUNDSARRAYINDEX : -1;
	}

	public WidgetChild[] getChildren() {
		final Object inter = getInternal();
		if (inter != null) {
			final Object[] interfaceComponents = (Object[]) ((RSInterfaceComponents) inter).getRSInterfaceComponents();
			if (interfaceComponents != null) {
				final WidgetChild[] components = new WidgetChild[interfaceComponents.length];
				for (int i = 0; i < components.length; i++) {
					components[i] = new WidgetChild(parentWidget, this, i);
				}
				return components;
			}
		}
		return new WidgetChild[0];
	}

	public WidgetChild getChild(final int index) {
		final WidgetChild[] children = getChildren();
		if (index >= 0 && index < children.length) {
			return children[index];
		}
		return null;
	}

	public boolean isVisible() {
		final Object inter = getInternal();
		return inter != null && !((RSInterfaceIsHidden) ((RSInterfaceBooleans) inter).getRSInterfaceBooleans()).getRSInterfaceIsHidden();
	}

	@Override
	public int hashCode() {
		return parentWidget.getIndex() * 0x1f + index;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof WidgetChild) {
			final WidgetChild child = (WidgetChild) obj;
			return index == child.index && child.parentWidget.equals(parentWidget);
		}
		return false;
	}


	public int getParentId() {
		final Object inter = getInternal();
		if (inter == null) {
			return -1;
		}
		final Client client = Context.client();
		final Multipliers multipliers = Context.multipliers();

		final int parentId = ((RSInterfaceParentID) ((RSInterfaceInts) inter).getRSInterfaceInts()).getRSInterfaceParentID() * multipliers.INTERFACE_PARENTID;

		if (parentId != -1) {
			return parentId;
		}

		final int mainID = getId() >>> 0x10;
		final HashTable ncI = new HashTable(client.getRSInterfaceNC());
		for (RSInterfaceNode node = (RSInterfaceNode) ncI.getFirst(); node != null; node = (RSInterfaceNode) ncI.getNext()) {
			if (mainID == ((RSInterfaceNodeMainID) ((RSInterfaceNodeInts) node.getData()).getRSInterfaceNodeInts()).getRSInterfaceNodeMainID() * multipliers.INTERFACENODE_MAINID) {
				final long multiplier = (((long) multipliers.NODE_ID) << 32) + ((multipliers.NODE_ID_p2 & 0xffffffffL));
				return (int) (node.getID() * multiplier);
			}
		}

		return -1;
	}

	public boolean isInScrollableArea() {
		if (getParentId() == -1) {
			return false;
		}

		WidgetChild scrollableArea = Widgets.getChild(getParentId());
		while (scrollableArea.getScrollableContentHeight() == 0 && scrollableArea.getParentId() != -1) {
			scrollableArea = Widgets.getChild(scrollableArea.getParentId());
		}

		return scrollableArea.getScrollableContentHeight() != 0;
	}


	private Object getInternal() {
		if (parent != null) {
			final Object p = parent.getInternal();
			if (p != null) {
				final Object[] components = (Object[]) ((RSInterfaceComponents) p).getRSInterfaceComponents();
				if (components != null && index >= 0 && index < components.length) {
					return components[index];
				}
			}
		} else {
			final Object[] children = parentWidget.getChildrenInternal();
			if (children != null && index < children.length) {
				return children[index];
			}
		}
		return null;
	}

	public boolean isOnScreen() {
		return validate() && isVisible();
	}

	public Polygon[] getBounds() {
		if (validate()) {
			final Point p = getAbsoluteLocation();
			final int w = getWidth();
			final int h = getHeight();
			final Polygon poly = new Polygon();
			poly.addPoint(p.x, p.y);
			poly.addPoint(p.x + w, p.y);
			poly.addPoint(p.x + w, p.y + h);
			poly.addPoint(p.x, p.y + h);
			return new Polygon[]{poly};
		}
		return new Polygon[0];
	}

	public Rectangle getBoundingRectangle() {
		if (validate()) {
			final Point p = getAbsoluteLocation();
			final int w = getWidth();
			final int h = getHeight();
			return new Rectangle(p.x, p.y, w, h);
		}
		return new Rectangle(-1, -1);
	}

	public boolean hover() {
		return Mouse.apply(this, new Filter<Point>() {
			public boolean accept(final Point point) {
				return true;
			}
		});
	}

	public boolean click(final boolean left) {
		return Mouse.apply(this, new Filter<Point>() {
			public boolean accept(final Point point) {
				Mouse.click(left);
				return true;
			}
		});
	}

	public boolean interact(final String action) {
		return Mouse.apply(this, new Filter<Point>() {
			public boolean accept(final Point point) {
				return Menu.select(action);
			}
		});
	}

	public boolean interact(final String action, final String option) {
		return Mouse.apply(this, new Filter<Point>() {
			public boolean accept(final Point point) {
				return Menu.select(action, option);
			}
		});
	}

	public void draw(final Graphics render) {
		//TODO
	}

	public Point getCentralPoint() {
		final Point p = getAbsoluteLocation();
		final int w = getWidth();
		final int h = getHeight();
		return validate() ? new Point((p.x * 2 + w) / 2, (p.y * 2 + h) / 2) : new Point(-1, -1);
	}

	public Point getNextViewportPoint() {
		final Rectangle rect = getBoundingRectangle();
		if (rect.x == -1 || rect.y == -1 || rect.width == -1 || rect.height == -1) {
			return new Point(-1, -1);
		}
		final int min_x = rect.x + 1, min_y = rect.y + 1;
		final int max_x = min_x + rect.width - 2, max_y = min_y + rect.height - 2;
		return new Point(Random.nextGaussian(min_x, max_x, rect.width / 3), Random.nextGaussian(min_y, max_y, rect.height / 3));
	}

	public boolean contains(final Point point) {
		final Rectangle rect = getBoundingRectangle();
		if (rect.x == -1 || rect.y == -1 || rect.width == -1 || rect.height == -1) {
			return false;
		}
		final int min_x = rect.x + 1, min_y = rect.y + 1;
		final int max_x = min_x + rect.width - 2, max_y = min_y + rect.height - 2;
		return (point.x >= min_x) && (point.x <= max_x) && (point.y >= min_y) && (point.y <= max_y);
	}

	public boolean validate() {
		return parentWidget.validate() && getBoundsArrayIndex() != -1;
	}
}
