package net.probot.rsc.loaders;


import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.applet.AudioClip;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

public class RSAppletStub implements AppletStub, AppletContext{
	public static int worldID;
    public boolean isActive() {
        return isActive;
    }

    public final URL getDocumentBase() {
        try {
			return new URL("http://classic"+worldID+".runescape.com");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
        return null;
    }

    public void setActive(boolean flag) {
        this.isActive = flag;
    }

    public final URL getCodeBase() {
        try {
			return new URL("http://classic"+worldID+".runescape.com");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
        return null;
    }

    public String getParameter(String name) {
        String s = (String)parameters.get(name);
        if(s == null) {
            return null;
        } else {
            return s;
        }
    }

    public AppletContext getAppletContext() {
        return this;
    }

    public void appletResize(int width, int height) {
        Dimension dimension = new Dimension(width, height);
        applet.setSize(dimension);
        applet.setPreferredSize(dimension);
    }

    public AudioClip getAudioClip(URL url) {
        return null;
    }

    public Image getImage(URL url) {
    	System.out.println("Image please");
        return null;
    }

    public Applet getApplet(String name) {
        return null;
    }

    public Enumeration<Applet> getApplets() {
        return null;
    }

    public void showDocument(URL url) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(url.toURI());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            if (url.toString().contains("runescape.com/l=")) {

            }
        }
    }

    public void showDocument(URL url, String target) {
    }

    public void showStatus(String status) {
    }

    public void setStream(String key, InputStream stream) throws IOException {
    }

    public InputStream getStream(String key) {
        return null;
    }

    public Iterator<String> getStreamKeys() {
        return null;
    }
    
    public void close() {
        applet = null;
    }
   

    public RSAppletStub(Applet applet) {
        worldID = 2;
        this.applet = applet;
    }

    private Applet applet;
    private boolean isActive = false;
    private Map<?, ?> parameters = WebCrawler.getParameters(2);
}
