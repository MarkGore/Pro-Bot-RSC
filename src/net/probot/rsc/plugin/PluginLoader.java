package net.probot.rsc.plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.PermissionCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Mark
 *
 * @param <T>
 */
public class PluginLoader<T> {
	private final Map<String, Class<? extends T>> classes;
	private final Class<T> extendClass;
	private final boolean init;
	private final Map<String, T> objects;
	private final PermissionCollection permissionCollection;

	private final File pluginDir;
	private final boolean singleton;
	private final Logger log;

	/**
	 * Class that you want your classes to extend. The directory that everything
	 * will be loaded from. Whether to init on load or not. Whether instances
	 * are singletons.
	 */
	public PluginLoader(final Class<T> extendClass, final File pluginDir,
			final boolean init, final boolean singleton, final String logName,
			final PermissionCollection pc) {
		log = Logger.getLogger(logName);
		this.extendClass = extendClass;
		this.pluginDir = pluginDir;
		this.init = init;
		this.singleton = singleton;
		permissionCollection = pc;
		classes = new HashMap<String, Class<? extends T>>();
		objects = new HashMap<String, T>();
	}

	/**
	 * Returns all the instances in the cache. (unmodifiable)
	 */
	public Collection<T> getCachedObjects() {
		return Collections.unmodifiableCollection(objects.values());
	}

	/**
	 * Returns all of the classes. (unmodifiable)
	 */
	public Collection<Class<? extends T>> getClasses() {
		return Collections.unmodifiableCollection(classes.values());
	}

	/**
	 * Returns the class with the given name.
	 */
	public Class<? extends T> getClassForName(final String name) {
		return classes.get(name);
	}

	/**
	 * Returns all of the class names. (unmodifiable)
	 */
	public Collection<String> getClassNames() {
		return Collections.unmodifiableCollection(classes.keySet());
	}

	/**
	 * Returns the class with the given name.
	 */
	public T getInstanceForName(final String name) {
		if (singleton && objects.get(name) != null) {
			return objects.get(name);
		}
		try {
			final Class<? extends T> c = getClassForName(name);
			if (c == null) {
				return null;
			}
			final T t = c.newInstance();
			if (singleton) {
				objects.put(name, t);
			}
			return t;
		} catch (final LinkageError noSuchMethodError) {
			log.log(Level.SEVERE, "Failed initialising class [" + name
					+ "] due to : ", noSuchMethodError);
			log.severe("Suggest recompiling.");
		} catch (final Throwable e) {
			log.log(Level.SEVERE, "", e);
		}
		return null;
	}

	/**
	 * Returns a NEW list of NEW instances.
	 */
	public List<T> getNewLoadedObjects() {
		if (singleton) {
			throw new RuntimeException("Cannot get new instance of singleton!");
		}
		final List<T> out = new ArrayList<T>();
		for (final Class<? extends T> c : classes.values()) {
			try {
				out.add(c.newInstance());
			} catch (final InstantiationException e) {
				log.log(Level.SEVERE, "Faild initing class " + c.getName(), e);
				return null;
			} catch (final IllegalAccessException e) {
				log.log(Level.SEVERE, "Faild initing class " + c.getName(), e);
				return null;
			}
		}
		return out;
	}

	/**
	 * Same as getCachedObjects() if singleton. Inits if init not specified.
	 */
	public Collection<T> getObjects() {
		if (singleton) {
			if (!init) {
				for (final String name : getClassNames()) {
					getInstanceForName(name);
				}
			}
			return getCachedObjects();
		} else {
			final Collection<String> names = getClassNames();
			final Collection<T> objects = new ArrayList<T>(names.size());
			for (final String name : names) {
				System.out.println(name);
				final T t = getInstanceForName(name);
				if (t != null) {
					objects.add(t);
				}
			}
			return objects;
		}
	}

	/**
	 * Loads the class using the loader and adds it to the maps if applicable.
	 * */
	private void load(final URLClassLoader loader, final String name) {
		Class<?> c;
		try {
			c = loader.loadClass(name);
		} catch (final LinkageError noSuchMethodError) {
			log.log(Level.SEVERE, "Failed initialising class [" + name
					+ "] due to : ", noSuchMethodError);
			log.severe("Suggest recompiling.");
			return;
		} catch (final ClassNotFoundException e) {
			log.log(Level.SEVERE, "Failed initialising class [" + name
					+ "] due to : ", e);
			return;
		}
		if (NonPluginClass.class.isAssignableFrom(c)) {
			return;
		}
		if (!extendClass.isAssignableFrom(c)) {
			log
					.warning("Class "
							+ name
							+ " does not extend the correct class.\nIf you are a script writer "
							+ "and this class is not a script you need either put the class in "
							+ "the same block of code as the script and make it non-static or "
							+ "implement net.pink.probot.plugin.NonPluginClass.");
			return;
		}
		final Class<? extends T> subClass = c.asSubclass(extendClass);
		classes.put(c.getName(), subClass);
		if (init) {
			try {
				objects.put(c.getName(), subClass.newInstance());
			} catch (final InstantiationException e) {
				log.log(Level.SEVERE, "Faild initing class " + c.getName(), e);
				return;
			} catch (final IllegalAccessException e) {
				log.log(Level.SEVERE, "Faild initing class " + c.getName(), e);
				return;
			}
		}
	}

	/**
	 * Loads all of the classes in the directory. Inits if (init) is true.
	 */
	public void reload() {
		String loadedNames = null;
		List<String> loaded_names = new ArrayList<String>();
		classes.clear();
		objects.clear();
		final List<File> dirs = new ArrayList<File>();
		dirs.add(pluginDir);
		final File[] dirArray = pluginDir.listFiles();
		if (dirArray == null) {
			log.severe("Attempting to load from [" + pluginDir
					+ "] but it does not exist.");
			return;
		}
		for (final File dir : dirArray) {
			if (dir.isDirectory() && dir.getName().charAt(0) != '.') {
				dirs.add(dir);
			}
		}
		URLClassLoader loader;
		try {
			final URL[] urls = new URL[dirs.size()];
			for (int i = 0; i < dirs.size(); i++) {
				urls[i] = dirs.get(i).toURI().toURL();
			}
			loader = new PermissionUCL(urls, permissionCollection);
		} catch (final MalformedURLException e) {
			throw new RuntimeException(e);
		}
		for (final File dir : dirs) {
			for (String name : dir.list()) {
				if (!name.endsWith(".class") || name.contains("$")) {
					continue;
				}
				name = name.substring(0, name.length() - ".class".length());
				// System.out.println("Name: " + name);
				
				if (loadedNames == null)
					loadedNames = name;
				else  {
					if (loadedNames.length()>40)  {
						loaded_names.add(loadedNames);
						loadedNames = null;
					}
					else
						loadedNames = loadedNames + ", " + name;
				}
				
				load(loader, name);
			}
		}
		final int size = classes.size();
		log.info(String.format("Loaded %d class%s.", size, size == 1 ? ""
				: "es"));
		
		for (String name : loaded_names)  {
				log.info("       " + name);
		}
	}
}
