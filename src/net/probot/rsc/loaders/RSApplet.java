package net.probot.rsc.loaders;
import java.applet.Applet;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JOptionPane;

import net.probot.rsc.bot.Bot;



public class RSApplet extends Applet implements Runnable {

	RSAppletStub stub;
	private Class clientClass;
	public Applet loader;
	private Bot bot;
	private Runnable callBackOnceLoaded;
	private Object clientInitialation;
	public Object loaderInitialation;
	public boolean loaded;
	public Field clientField;

	public RSApplet(Bot bot2) {
		loaded = false;
		bot = bot2;
	}

	public void setClientStub(RSAppletStub stub) {
		this.stub = stub;
	}

	public void run() {
		System.out.println("loading applet");
		try {
			bot.getLoader().load();
			clientClass = bot.getLoader().loadClass("loader");
			loader = (Applet) (loaderInitialation = clientClass.newInstance());
			loader.setStub(stub);
			loader.init();
			loader.start();
			loaded = true;
			(new Thread(callBackOnceLoaded)).run();
			Thread.sleep(20);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    public void destroyLoader() {
    	loader.stop();
    	loader.destroy();
    	loader = null;
    	loader = null;
    }

	public void setCallBack(Runnable thread) {
		callBackOnceLoaded = thread;
	}

	public final synchronized void destroy() {
		if (loader != null) {
			loader.destroy();
		}
	}

	public Applet getClient() {
		return loader;
	}

	public final synchronized void init() {
		if (loader != null) {
			loader.init();
		}
	}

	public final void paint(Graphics g) {
		if (loader != null) {
			loader.paint(g);
		}
	}

	public final synchronized void start() {
		if (loader != null) {
			loader.start();
		}
	}

	public final synchronized void stop() {
		if (loader != null) {
			loader.stop();
		}
	}

	public final void update(Graphics g) {
		if (loader != null) {
			loader.update(g);
		}
	}

}
