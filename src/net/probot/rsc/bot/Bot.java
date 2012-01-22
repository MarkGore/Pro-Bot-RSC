package net.probot.rsc.bot;
import java.applet.Applet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

import net.probot.rsc.UI.BotUI;
import net.probot.rsc.loaders.JarLoader;
import net.probot.rsc.loaders.RSApplet;
import net.probot.rsc.loaders.RSAppletStub;
import net.probot.rsc.reflection.ClientWrapper;
import net.probot.rsc.reflection.ReflectionHook;
import net.probot.rsc.script.ScriptHandler;

/**
 * @author Mark
 *
 */
public class Bot implements Runnable {
	public static ArrayList<ReflectionHook> hooks = new ArrayList<ReflectionHook>();
	private JarLoader loader;
	public RSApplet rsApplet;
	private RSAppletStub rsAppletStub;
	private ThreadGroup threadGroup;
	private Thread thread;
	private ScriptHandler sh;
	public Object client;
	public Class<? extends Object> clientClass;
	private Field clientField;
	
	public JarLoader getLoader() {
		return loader;
	}
	
	public Applet getApplet() {
		return rsApplet.getClient();
	}
	
	public void init() {
		loader = new JarLoader(this);
		rsApplet = new RSApplet(this);
		rsAppletStub = new RSAppletStub(rsApplet);
		rsApplet.setClientStub(rsAppletStub);
		rsApplet.setStub(rsAppletStub);
		startBots();
		waitFor();
	}
	
	public void startBots() {
		rsAppletStub.setActive(true);
		rsApplet.setCallBack(this);
		threadGroup = new ThreadGroup("RSC");
		thread = new Thread(threadGroup, rsApplet, "RscApplet");
		
		thread.start();
	}

	public void waitFor() {
		while (!rsApplet.loaded) {
			try {
				Thread.sleep(300L);
			} catch (InterruptedException ex) {
			}
		}
	}

	@Override
	public void run() {
		try {
			loadHooks();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		sh = new ScriptHandler(new File("./"), this);
		new Thread(new Runnable() {
			public void run() {
				try {
					sh.reload();
				} finally { }
			}
		},"SRA-Load").start();
	}
	
	public ReflectionHook getHookObject(String name) {
		for (ReflectionHook rh : hooks) {
			if (rh.getName().equals(name)) {
				return rh;
			}
		}
		return null;
	}
	public ClientWrapper wrapper;
	public void loadHooks() throws Exception {
		BufferedReader in = new BufferedReader(new FileReader("hooks.txt"));
		String blah = null;
		HashMap<String, ArrayList<ReflectionHook>> map = new HashMap<String, ArrayList<ReflectionHook>>();
		int loadedHookCount = 0;
		while ((blah = in.readLine()) != null) {
			String data[] = blah.split(" ");
			if (data.length == 2) {
				String[] localinfo = data[0].toLowerCase().split("\\.", 2);
				if (localinfo.length != 2) {
					continue;
				}
				String[] rsinfo = data[1].split("\\.");
				if (rsinfo.length != 2) {
					continue;
				}
				if (map.get(localinfo[0]) == null) {
					map.put(localinfo[0], new ArrayList<ReflectionHook>());
				}
				ArrayList<ReflectionHook> hooklist = map.get(localinfo[0]);
				hooklist.add(new ReflectionHook(this, localinfo[1], rsinfo[0],
						rsinfo[1]));
				System.out.println(localinfo[1] + " " + rsinfo[0] + " "
						+ rsinfo[1]);
				loadedHookCount++;
				if (data.length == 3) {
					hooks.add(new ReflectionHook(this, data[0], data[1], data[2]));
					loadedHookCount++;
				}
			}
		}
		System.out.println("Loaded " + loadedHookCount + " Rhooks");
		wrapper = new ClientWrapper(getClientInitialation());
		wrapper.setBaseobj(getClientInitialation());
		wrapper.bindHooks(map);
	}
	
	public Object getClientInitialation() throws Exception {
		Class<? extends Applet> loaderClass = rsApplet.loader.getClass();
		Object ob = null;
		try {
			clientField = loaderClass.getDeclaredField("h");
		} catch (NoSuchFieldException fnfe) {
			JOptionPane.showMessageDialog(null,
					"Fatal error: could not find client, errorcode:(#0x0ff)");
		}
		while (ob == null) {
			if (clientField != null) {
				clientField.setAccessible(true);
				ob = clientField.get(rsApplet.loaderInitialation);
			} else {
				return null;
			}
			Thread.sleep(300L);
		}
		return ob;
	}

	public void setSh(ScriptHandler sh) {
		this.sh = sh;
	}

	public ScriptHandler getSh() {
		return sh;
	}

}