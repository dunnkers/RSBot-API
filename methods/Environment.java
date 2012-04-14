package org.powerbot.game.api.methods;

import java.awt.image.BufferedImage;

import org.powerbot.game.api.util.ScreenCapture;
import org.powerbot.game.bot.Context;

public class Environment {
	public static String getDisplayName() {
		return Context.get().getDisplayName();
	}

	public static int getUserId() {
		return Context.get().getUserId();
	}

	public static BufferedImage captureScreen() {
		return ScreenCapture.capture(Context.get());
	}

	public static void saveScreenCapture() {
		ScreenCapture.save(Context.get());
	}

	public static void saveScreenCapture(final String fileName) {
		ScreenCapture.save(Context.get(), fileName);
	}
}
