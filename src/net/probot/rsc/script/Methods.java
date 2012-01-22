package net.probot.rsc.script;

import java.util.HashMap;
import java.util.Map;

import net.probot.rsc.bot.Bot;

public class Methods {
	public Map<String, String> args = new HashMap<String, String>();
	public boolean randomActivated = false;
	public Bot bot;
	
	public Bot getBot() {
		return bot;
	}
	
	public void sleep(final int toSleep) {
		try {
			final long start = System.currentTimeMillis();
			Thread.sleep(toSleep);

			long t = System.currentTimeMillis();

			while (t - start < toSleep) {
				Thread.sleep(start + toSleep - t);
				t = System.currentTimeMillis();
			}
		} catch (final InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
