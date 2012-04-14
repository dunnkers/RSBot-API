package org.powerbot.game.api.methods.tab;

import java.util.LinkedList;
import java.util.List;

import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.api.wrappers.widget.WidgetChild;

public class Inventory {
	public static final int WIDGET = 679;
	public static final int WIDGET_BANK = 763;
	public static final int WIDGET_PRICE_CHECK = 204;
	public static final int WIDGET_EQUIPMENT_BONUSES = 670;
	public static final int WIDGET_EXCHANGE = 644;
	public static final int WIDGET_SHOP = 621;
	public static final int WIDGET_DUNGEONEERING_SHOP = 957;
	public static final int WIDGET_BEAST_OF_BURDEN_STORAGE = 665;

	public static final int[] ALT_WIDGETS = {
			WIDGET_BANK,
			WIDGET_PRICE_CHECK, WIDGET_EQUIPMENT_BONUSES,
			WIDGET_EXCHANGE, WIDGET_SHOP, WIDGET_DUNGEONEERING_SHOP,
			WIDGET_BEAST_OF_BURDEN_STORAGE
	};

	public static Item getItem(final int... ids) {
		final Item[] items = getItems(false);
		for (final Item item : items) {
			final int item_id = item.getId();
			for (final int id : ids) {
				if (item_id == id) {
					return item;
				}
			}
		}
		return null;
	}

	public static Item[] getItems() {
		return getItems(false);
	}

	public static Item[] getItems(final boolean cached) {
		final WidgetChild inventoryWidget = getWidget(cached);
		if (inventoryWidget != null) {
			final WidgetChild[] inventoryChildren = inventoryWidget.getChildren();
			if (inventoryChildren.length > 27) {
				final List<Item> items = new LinkedList<Item>();
				for (int i = 0; i < 28; ++i) {
					if (inventoryChildren[i].getChildId() != -1) {
						items.add(new Item(inventoryChildren[i]));
					}
				}
				return items.toArray(new Item[items.size()]);
			}
		}
		return new Item[0];
	}

	public static int getCount() {
		return getItems().length;
	}

	public static int getCount(final int id) {
		return getCount(false, new Filter<Item>() {
			public boolean accept(final Item item) {
				return item.getId() == id;
			}
		});
	}

	public static int getCount(final Filter<Item> itemFilter) {
		return getCount(false, itemFilter);
	}

	public static int getCount(final boolean countStack, final int id) {
		return getCount(countStack, new Filter<Item>() {
			public boolean accept(final Item item) {
				return item.getId() == id;
			}
		});
	}

	public static int getCount(final boolean countStack, final Filter<Item> itemFilter) {
		final Item[] items = getItems();
		int count = 0;
		for (final Item item : items) {
			if (item != null && itemFilter.accept(item)) {
				if (countStack) {
					count += item.getStackSize();
				} else {
					++count;
				}
			}
		}
		return count;
	}

	public static boolean selectItem(final int itemId) {
		final Item item = getItem(itemId);
		return item != null && selectItem(item);
	}

	/**
	 * Selects the specified item in the inventory
	 *
	 * @param item The item to select.
	 * @return <tt>true</tt> if the item was selected; otherwise <tt>false</tt>.
	 */
	public static boolean selectItem(final Item item) {//TODO fix index 0
		final int itemID = item.getId();
		Item selItem = getSelectedItem();
		if (selItem != null && selItem.getId() == itemID) {
			return true;
		}
		if (selItem != null) {
			selItem.getWidgetChild().interact("Use");
			Time.sleep(Random.nextInt(500, 700));
		}
		if (!item.getWidgetChild().interact("Use")) {
			return false;
		}
		for (int c = 0; c < 5 && (selItem = getSelectedItem()) == null; c++) {
			Time.sleep(Random.nextInt(500, 700));
		}
		return selItem != null && selItem.getId() == itemID;
	}

	public static Item getItemAt(final int index) {
		final WidgetChild child = getWidget(false).getChild(index);
		return index >= 0 && index < 28 && child != null ? new Item(child) : null;
	}


	public static Item getSelectedItem() {
		final int index = getSelectedItemIndex();
		return index == -1 ? null : getItemAt(index);
	}

	/**
	 * Gets the selected item index.
	 *
	 * @return The index of current selected item, or -1 if none is selected.
	 */
	public static int getSelectedItemIndex() {
		final WidgetChild[] children = getWidget(false).getChildren();
		for (int i = 0; i < Math.min(28, children.length); i++) {
			if (children[i].getBorderThickness() == 2) {
				return i;
			}
		}
		return -1;
	}

	public static WidgetChild getWidget(final boolean cached) {
		for (final int widget : ALT_WIDGETS) {
			WidgetChild inventory = Widgets.get(widget, 0);
			if (inventory != null && inventory.getAbsoluteX() > 50) {
				return inventory;
			}
		}
		if (!cached) {
			Tabs.INVENTORY.open(true);
		}
		return Widgets.get(WIDGET, 0);
	}
}
