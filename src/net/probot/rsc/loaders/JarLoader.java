package net.probot.rsc.loaders;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.probot.rsc.bot.Bot;

/**
 * @author Mark
 *
 */
public class JarLoader extends ClassLoader {
	public JarLoader(Bot bot) {
		classes = new HashMap<String, byte[]>();
	}

	public void load() {
		try {
			JarFile jarFile = new JarFile("loadertest.jar");
			Enumeration<?> e = jarFile.entries();

			while (e.hasMoreElements()) {

				JarEntry jarEntry = (JarEntry) e.nextElement();
				String s = jarEntry.getName();
				if (!s.toLowerCase().endsWith(".class")) {
					continue;
				}
				int i = (int) jarEntry.getSize();
				byte[] abyte0 = new byte[i];
				DataInputStream datainputstream = new DataInputStream(jarFile
						.getInputStream(jarEntry));
				datainputstream.readFully(abyte0);
				datainputstream.close();
				String s1 = s.substring(0, s.lastIndexOf('.'));
				classes.put(s1, abyte0);
			}
		} catch (IOException ex) {
			Logger.getLogger(JarLoader.class.getName()).log(Level.SEVERE, null,
					ex);
		}
	}

	public Class<?> findClass(String name) {
		return classess.get(name);
	}

	@Override
	public final Class<?> loadClass(String s) throws ClassNotFoundException {
		if (classes.containsKey(s)) {
			byte abyte0[] = (byte[]) classes.remove(s);
			Class<?> cd = defineClass(s, abyte0, 0, abyte0.length);
			classess.put(s, cd);
			return cd;
		} else {
			return super.loadClass(s);
		}
	}

	private HashMap<String, byte[]> classes;
	public HashMap<String, Class<?>> classess = new HashMap<String, Class<?>>();
}
