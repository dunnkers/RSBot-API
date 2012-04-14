package org.powerbot.game.api.methods;

import java.awt.image.BufferedImage;

import org.powerbot.game.api.util.ScreenCapture;
import org.powerbot.game.bot.Context;
import org.powerbot.service.NetworkAccount;

public class Environment {
	public static String getDisplayName() {
		if (NetworkAccount.getInstance().isLoggedIn()) {
			return NetworkAccount.getInstance().getAccount().getDisplayName();
		}
		return null;
	}

	public static int getUserId() {
		if (NetworkAccount.getInstance().isLoggedIn()) {
			return NetworkAccount.getInstance().getAccount().getID();
		}
		return -1;
	}

	public static BufferedImage captureScreen() {
		return ScreenCapture.capture(Context.resolve());
	}

	public static void saveScreenCapture() {
		ScreenCapture.save(Context.resolve());
	}

	public static void saveScreenCapture(final String fileName) {
		ScreenCapture.save(Context.resolve(), fileName);
	}
}
