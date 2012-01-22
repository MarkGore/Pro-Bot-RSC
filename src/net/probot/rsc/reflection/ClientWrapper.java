package net.probot.rsc.reflection;

import java.applet.Applet;
import java.awt.Graphics;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 * @author Mark Gore
 */
public class ClientWrapper extends BaseWrapper {
	
	public ClientWrapper(Object client) {
		super(null, null, client);
	}

	public void bindHooks(HashMap<String, ArrayList<ReflectionHook>> hookmap) {
		int binded = 0;
		ArrayList<ReflectionHook> clienthooks = hookmap.get("client");
		if (clienthooks == null) {
			throw new RuntimeException("Who fucked up client hooks ??? Not me...");
		}
		int size = clienthooks.size();
		for (ReflectionHook rh : clienthooks) {
			try {
				if (rh.bind(this)) {
					binded++;
				}
			} catch (Exception ex) {
			}
		}
		System.out.println("binded to clientwrapper " + binded + "/" + size);
		hookmap.remove("client");
		for (Map.Entry<String, ArrayList<ReflectionHook>> entry : hookmap.entrySet()) {
			String key = entry.getKey();
			Field myfield = null;
			Field wrapperfield = null;
			Class<?> wrapper = null;
			String newclassname = null;
			try {
				myfield = (Field)getClass().getDeclaredField("f_" + key).get(this);
				myfield.setAccessible(true);
			} catch (Exception ex) {
				System.err.println("Cannot find clientwrapper field f_" + key + ": " + ex.getMessage());
				continue;
			}
			try {
				wrapperfield = getClass().getDeclaredField(key);
				wrapperfield.setAccessible(true);
			} catch (Exception ex) {
				System.err.println("Cannot find clientwrapper field " + key + ": " + ex.getMessage());
				continue;
			}
			try {
				newclassname = getClass().getName().replace("ClientWrapper", key.substring(0, 1).toUpperCase() + key.substring(1).toLowerCase() + "Wrapper");
				wrapper = Class.forName(newclassname);
			} catch (Exception ex) {
				System.err.println("Cannot find " + newclassname + ": " + ex.getMessage());
				continue;
			}
			try {
				//create wrapper class
				Object wrapperobj = wrapper.getConstructor(new Class[]{Field.class, Object.class, Object.class}).newInstance(new Object[]{myfield, baseobject, null});
				//assign to this
				wrapperfield.set(this, wrapperobj);
				//bind wrapper hooks
				((BaseWrapper)wrapperobj).bindHooks(entry.getValue());
			} catch (Exception ex) {
			}
		}
		hookmap.clear();
	}
	
	public String getUsername() {
		return (String) getFieldValue(f_username, "Username");
	}
	private Field f_username;
	
	public boolean isSleeping() {
		return (Boolean) getFieldValue(f_issleeping, "issleeping");
	}
	private Field f_issleeping;
}
