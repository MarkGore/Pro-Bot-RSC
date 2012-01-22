package net.probot.rsc.script;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.probot.rsc.bot.Bot;
import net.probot.rsc.plugin.PluginLoader;
/**
 * @author Mark Gore
 */
public class ScriptHandler {
	private HashMap<Integer, Script> scripts = new HashMap<Integer, Script>();
	private HashMap<Integer, Thread> scriptThreads = new HashMap<Integer, Thread>();

	private final PluginLoader<Random> randomLoader;
	private final PluginLoader<Script> scriptLoader;

	private volatile boolean screenshotOnFinish = false;
	private Bot bot;

	public ScriptHandler(final File workDir, Bot bot) {
		this.bot = bot;
		randomLoader = new PluginLoader<Random>(Random.class, new File(workDir,
				"Randoms"), true, true, "RandomLoader", null);
		scriptLoader = new PluginLoader<Script>(Script.class, new File(workDir,
				"Scripts"), false, false, "ScriptLoader", null);
	}

	private void addScriptToPool(final Script ss, final Thread t) {
		for (int off = 0; off < bot.getSh().scripts.size(); off++) {
			if (!bot.getSh().scripts.containsKey(off)) {
				bot.getSh().scripts.put(off, ss);
				ss.ID = off;
				bot.getSh().scriptThreads.put(off, t);
				return;
			}
		}
		ss.ID = bot.getSh().scripts.size();
		bot.getSh().scripts.put(bot.getSh().scripts.size(), ss);
		bot.getSh().scriptThreads.put(bot.getSh().scriptThreads.size(), t);
	}

	public Collection<Random> getRandoms() {
		return randomLoader.getObjects();
	}

	public Map<Integer, Script> getRunningScripts() {
		return Collections.unmodifiableMap(bot.getSh().scripts);
	}

	public Script getScriptForName(final String name) {
		return scriptLoader.getInstanceForName(name);
	}

	public Collection<String> getScriptNames() {
		return scriptLoader.getClassNames();
	}

	/**
	 * May cause unwanted loading
	 */
	public Collection<Script> getScripts() {
		return scriptLoader.getObjects();
	}

	public boolean isScreenshotOnFinish() {
		return screenshotOnFinish;
	}

	public void pauseScript(final int id) {
		final Script s = bot.getSh().scripts.get(id);
		s.isPaused = !s.isPaused;
	}

	public void reload() {
		reloadRandoms();
		reloadScripts();
	}

	public void reloadRandoms() {
		randomLoader.reload();
	}

	public void reloadScripts() {
		scriptLoader.reload();
	}

	public void removeScript(final int id) {
		if (bot.getSh().scripts.get(id) == null) {
			return;
		}
		bot.getSh().scripts.get(id).isActive = false;
		bot.getSh().scripts.remove(id);
		bot.getSh().scriptThreads.remove(id);
	}

	public void runScript(final Script ss, final Map<String, String> map, final Bot bot) {
		final Thread t = new Thread(new Runnable() {
			public void run() {
				ss.run(map, bot);
			}
		}, "Script-" + ss.getName());
		addScriptToPool(ss, t);
		t.start();
	}

	public void setScreenshotOnFinish(final boolean screenshotOnFinish) {
		this.screenshotOnFinish = screenshotOnFinish;
	}

	public void stopAllScripts() {
		Thread curThread = Thread.currentThread();
		for (int i = 0; i < bot.getSh().scripts.size(); i++) {
			if (bot.getSh().scripts.get(i) != null
					&& bot.getSh().scripts.get(i).isActive) {
				if (bot.getSh().scriptThreads.get(i) == curThread) {
					removeScript(i);
					curThread = null;
				} else {
					stopScript(i);
				}
			}
		}
		if (curThread == null) {
			throw new ThreadDeath();
		}
	}

	@SuppressWarnings("deprecation")
	public void stopScript(final int id) {
		bot.getSh().scripts.get(id).isActive = false;
		bot.getSh().scripts.remove(id);
		bot.getSh().scriptThreads.get(id).stop();
		// TODO - better way to stop (currently depreciated)
		bot.getSh().scriptThreads.remove(id);
	}
}