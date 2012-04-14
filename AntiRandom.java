package org.powerbot.game.api;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.logging.Logger;

import org.powerbot.concurrent.Task;
import org.powerbot.concurrent.strategy.Condition;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.bot.Bot;
import org.powerbot.game.bot.Context;
import org.powerbot.game.bot.event.listener.PaintListener;

/**
 * @author Timer
 */
public abstract class AntiRandom implements Condition, Task, PaintListener {
	public final Logger log = Logger.getLogger(getClass().getName());
	public Bot bot = null;

	private static final boolean VERBOSE = true;

	public void onRepaint(final Graphics render) {
		final Point p = Mouse.getLocation();
		final Canvas canvas = Context.client().getCanvas();
		final int w = canvas.getWidth(), h = canvas.getHeight();
		render.setColor(new Color(51, 153, 255, 30));
		render.fillRect(0, 0, p.x - 1, p.y - 1);
		render.fillRect(p.x + 1, 0, w - (p.x + 1), p.y - 1);
		render.fillRect(0, p.y + 1, p.x - 1, h - (p.y - 1));
		render.fillRect(p.x + 1, p.y + 1, w - (p.x + 1), h - (p.y - 1));
	}

	protected void verbose(final String info) {
		if (VERBOSE) {
			log.info(info);
		}
	}
}
