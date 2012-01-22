package net.probot.rsc.script;

import java.util.logging.Level;

import net.probot.rsc.bot.Bot;
/**
 * @author Mark Gore
 */
public abstract class Random extends Scriptable {
	public boolean isActive = false;
	public boolean isUsed = true;

	public abstract boolean activateCondition();

	public abstract int loop();

	public final boolean runRandom(Bot bot) {
		this.bot = bot;
		if (!isUsed) {
			return false;
		}
		try {
			if (!activateCondition()) {
				randomActivated = false;
				return false;
			}
		} catch (final ThreadDeath td) {
			throw td;
		} catch (final Throwable e) {
			log.log(Level.WARNING, "", e);
			return false;
		}
		isActive = true;
		randomActivated = true;
		log.info("Random: Anti-Random " + getName() + " by " + getAuthor()
				+ " activated!");
		while (isActive) {
			try {
				final int timeOut = loop();
				if (timeOut == -1) {
					break;
				}
				sleep(timeOut);
			} catch (final ThreadDeath td) {
				isActive = false;
				throw td;
			} catch (final Throwable e) {
				log.log(Level.WARNING, "", e);
				break;
			}
		}
		log.info("Random: Anti-Random " + getName() + " by " + getAuthor()
				+ " finished, continuing script.");
		return true;
	}
}