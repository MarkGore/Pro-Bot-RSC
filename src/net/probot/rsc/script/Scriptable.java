package net.probot.rsc.script;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class Scriptable extends Methods {

	protected Map<String, Object> parameters = new HashMap<String, Object>();

	protected final Logger log = getLogger();

	/**
	 * Returns the author of this script. In the case of multiple authors,
	 * separate with commas.
	 * */
	public String getAuthor() {
		final Object o = parameters.get("author");
		return o instanceof String ? (String) o : "Not Set";
	}

	private Logger getLogger() {
		final String[] className = getClass().getName().split("\\.");
		return Logger.getLogger(className[className.length - 1]);
	}

	/**
	 * Returns a human readable name for this script. The class of the script
	 * should be similar to this.
	 * */
	public String getName() {
		final Object o = parameters.get("name");
		return o instanceof String ? (String) o : getClass().getName();
	}

	/**
	 * Returns the value for a given parameter.
	 * */
	public Object getParameter(final String key) {
		return parameters.get(key);
	}

	/**
	 * Returns the underlying parameter map.
	 * */
	public Map<String, Object> getParameters() {
		return parameters;
	}

	/**
	 * Returns the version of this script.
	 * */
	public double getVersion() {
		final Object o = parameters.get("version");
		return o instanceof Number ? ((Number) o).doubleValue() : -1;
	}

	public Object setParameter(final String key, final Object val) {
		return parameters.put(key, val);
	}
}