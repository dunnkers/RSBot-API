package org.powerbot.game.api;

import java.io.File;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import org.powerbot.concurrent.LoopTask;
import org.powerbot.concurrent.Processor;
import org.powerbot.concurrent.Task;
import org.powerbot.concurrent.TaskContainer;
import org.powerbot.concurrent.TaskProcessor;
import org.powerbot.concurrent.strategy.Condition;
import org.powerbot.concurrent.strategy.DaemonState;
import org.powerbot.concurrent.strategy.Strategy;
import org.powerbot.concurrent.strategy.StrategyDaemon;
import org.powerbot.concurrent.strategy.StrategyGroup;
import org.powerbot.event.EventManager;
import org.powerbot.game.bot.Context;

/**
 * @author Timer
 */
public abstract class ActiveScript implements EventListener, Processor {
	public final Logger log = Logger.getLogger(getClass().getName());

	private EventManager eventManager;
	private TaskContainer container;
	private StrategyDaemon executor;
	private final List<LoopTask> loopTasks;
	private final List<EventListener> listeners;

	private Context context;
	private boolean silent;

	public ActiveScript() {
		eventManager = null;
		container = null;
		executor = null;
		loopTasks = new ArrayList<LoopTask>();
		listeners = new ArrayList<EventListener>();
		silent = false;
	}

	public final void init(final Context context) {
		this.context = context;
		eventManager = context.getEventManager();
		container = new TaskProcessor(context.getThreadGroup());
		executor = new StrategyDaemon(container, context.getContainer());
	}

	protected final void provide(final Strategy strategy) {
		executor.append(strategy);

		if (!listeners.contains(strategy)) {
			listeners.add(strategy);
			if (!isLocked()) {
				eventManager.accept(strategy);
			}
		}
	}

	protected final void provide(final StrategyGroup group) {
		for (final Strategy strategy : group) {
			provide(strategy);
		}
	}

	protected final void revoke(final Strategy strategy) {
		executor.omit(strategy);

		listeners.remove(strategy);
		eventManager.remove(strategy);
	}

	protected final void revoke(final StrategyGroup group) {
		for (final Strategy strategy : group) {
			revoke(strategy);
		}
	}

	public final Future<?> submit(final Task task) {
		return container.submit(task);
	}

	public final boolean submit(final LoopTask loopTask) {
		if (loopTasks.contains(loopTask)) {
			return false;
		}

		loopTask.init(this);
		loopTask.start();
		loopTasks.add(loopTask);
		listeners.add(loopTask);
		if (!isLocked()) {
			eventManager.accept(loopTask);
		}

		return true;
	}

	public final void terminated(final Task task) {
		if (task instanceof LoopTask) {
			final LoopTask loopTask = (LoopTask) task;
			listeners.remove(loopTask);
			eventManager.remove(loopTask);
		}
	}

	protected final void setInterruptionPolicy(final Condition policy) {
	}

	protected final void setIterationDelay(final int milliseconds) {
		executor.setIterationSleep(milliseconds);
	}

	protected abstract void setup();

	public final Task start() {
		return new Task() {
			public void run() {
				setup();
				resume();
				if (context != null) {
					context.ensureAntiRandoms();
				}
			}
		};
	}

	public final void resume() {
		silent = false;
		eventManager.accept(ActiveScript.this);
		final Iterator<LoopTask> taskIterator = loopTasks.iterator();
		while (taskIterator.hasNext()) {
			final LoopTask task = taskIterator.next();
			if (task.isKilled()) {
				loopTasks.remove(task);
				continue;
			}

			task.start();
			container.submit(task);
		}
		for (final EventListener eventListener : listeners) {
			eventManager.accept(eventListener);
		}
		executor.listen();
	}

	public final void pause() {
		pause(false);
	}

	public final void pause(final boolean removeListener) {
		executor.lock();
		for (final LoopTask task : loopTasks) {
			task.stop();
		}
		if (removeListener) {
			eventManager.remove(ActiveScript.this);
			for (final EventListener eventListener : listeners) {
				eventManager.remove(eventListener);
			}
		}
	}

	public final void silentLock(final boolean removeListener) {
		silent = true;
		pause(removeListener);
	}

	public final void stop() {
		container.submit(new Task() {
			public void run() {
				onStop();
			}
		});
		eventManager.remove(ActiveScript.this);
		for (final LoopTask task : loopTasks) {
			task.stop();
			task.kill();
		}
		for (final EventListener eventListener : listeners) {
			eventManager.remove(eventListener);
		}
		executor.destroy();
		container.shutdown();

		final String name = Thread.currentThread().getThreadGroup().getName();
		if (name.startsWith("GameDefinition-") ||
				name.startsWith("ThreadPool-")) {
			context.updateControls();
		}
	}

	public void onStop() {
	}

	public final void kill() {
		container.stop();
	}

	protected final DaemonState getState() {
		return executor.state;
	}

	public final boolean isRunning() {
		return getState() != DaemonState.DESTROYED;
	}

	public final boolean isPaused() {
		return isLocked() && !silent;
	}

	public final boolean isLocked() {
		return getState() == DaemonState.LOCKED;
	}

	public final boolean isSilentlyLocked() {
		return silent;
	}

	public final TaskContainer getContainer() {
		return container;
	}

	/**
	 * Get an accessible and isolated directory for reading and writing files.
	 *
	 * @return A unique per-script directory path with file IO permissions.
	 */
	public File getStorageDirectory() {
		final File dir = new File(System.getProperty("java.io.tmpdir"), getClass().getName());
		if (!dir.isDirectory()) {
			dir.mkdirs();
		}
		return dir;
	}
}
