package net.probot.rsc.script;

import java.util.EventListener;
import java.util.Map;
import java.util.logging.Level;

import net.probot.rsc.bot.Bot;

public abstract class Script extends Scriptable implements EventListener {
	public int ID = -1;
	public volatile boolean isActive = false;
	public volatile boolean isPaused = false;

	private boolean checkForRandoms(Bot bot) {
		for (final Random r : getBot().getSh().getRandoms()) {
			if (r.runRandom(bot)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the category of the script. By default it returns "Other".
	 * 
	 * eg. Mining
	 * 
	 * Do not abuse by returning the author's name.
	 * */
	public String getScriptCategory() {
		final Object o = parameters.get("category");
		return o instanceof String ? (String) o : "Other";
	}

	/**
	 * Returns a description of this script. HTML is permitted.
	 * 
	 * By default returns a basic description with space to enter arguments.
	 * */
	public String getScriptDescription() {
		final Object o = description();
		return o instanceof String ? (String) o
				: "<html>\n"
						+ "<head></head>\n"
						+ "<body>\n"
						+ "<center>"
						+ "<h2>"
						+ getName()
						+ "</h2>"
						+ "</center>\n"
						+ "<p\n"
						+ "<b>Author:</b> "
						+ getAuthor()
						+ "<p>\n"
						+ "<b>Version:</b> "
						+ getVersion()
						+ "<p>\n"
						+ "This script is has no description."
						+ "Please see the <a href=\"http://www.probot-ltd.org/\">forums</a> "
						+ "for information on how to use it.\n" + "<p>\n"
						 + "</body>\n" + "</html>";
	}

	/**
	 * The main loop. Called if you return true from main. Called until you
	 * return a negative number. Returns an integer. The manager will wait that
	 * many milliseconds before calling the method again.
	 * */
	public abstract int loop();
	public abstract boolean onStart();
	public abstract String description();

	public final void run(final Map<String, String> map, Bot bot) {
		this.bot = bot;
		this.args = map;
		log.info("Script started.");
		boolean start = false;
		try {
			start = onStart();
		} catch (final ThreadDeath td) {
		} catch (final Throwable e) {
			log.log(Level.SEVERE, "Error starting script: ", e);
		}
		if (start) {
			isActive = true;
			try {
				while (isActive) {
					if (!isPaused) {
						if (checkForRandoms(bot)) {
							continue;
						}
						int timeOut = -1;
						try {
							timeOut = loop();
						} catch (final ThreadDeath td) {
							break;
						} catch (final Throwable e) {
							log.log(Level.WARNING,
									"Uncaught exception from script: ", e);
						}
						if (timeOut == -1) {
							break;
						}
						try {
							checkForRandoms(bot);
						} catch (final ThreadDeath td) {
							break;
						}
						try {
							sleep(timeOut);
						} catch (final ThreadDeath td) {
							break;
						} catch (final RuntimeException e) {
							e.printStackTrace();
							break;
						}
					} else {
						try {
							sleep(1000);
						} catch (final ThreadDeath td) {
							break;
						} catch (final RuntimeException e) {
							e.printStackTrace();
							break;
						}
					}
				}
				try {
					onFinish();
				} catch (final ThreadDeath td) {
				} catch (final RuntimeException e) {
					e.printStackTrace();
				}
			} catch (final ThreadDeath td) {
				onFinish();
			}
			isActive = false;

			log.info("Script stopped.");
		} else {
			log.severe("Failed to start up.");
		}
		getBot().getSh().removeScript(ID);
	}

	private void onFinish() {
		// TODO Auto-generated method stub
		
	}
}