package net.probot.rsc.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class BaseWrapper {

	protected Field getterfield;
	protected Object getterobject;
	protected Object baseobject;
	private HashMap<String, Field> fieldmap = new HashMap<String, Field>();
	private HashMap<Member, Field> multilevelfields = new HashMap<Member, Field>();

	public BaseWrapper(Field getterfield, Object getterobj, Object baseobject) {
		this.getterfield = getterfield;
		this.getterobject = getterobj;
		this.baseobject = baseobject;
	}

	public void setBaseobj(Object obj) {
		baseobject = obj;
	}

	protected Object invokeMethod(Method m, String name, Object... params) {
		if (m == null) {
			System.out.println("Null method " + name + "!!");
			return null;
		}
		try {
			return m.invoke(getTarget(m), params);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private Object getTarget(Member field_method) throws Exception {
		Object target = getterfield != null ? getterfield.get(getterobject) : baseobject;
		//is not mapped as multilevel supported field
		Field supportfield = multilevelfields.get(field_method);
		if (supportfield == null) {
			return target;
		}
		target = supportfield.get(target);
		return target;
	}

	protected Object getFieldValue(Field rh, String name) {
		if (rh == null) {
			System.out.println("hook " + name + " is not loaded correctly!!! World is about to end!!!");
			return null;
		}
		try {
			return rh.get(getTarget(rh));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	protected void setFieldValue(Field rh, String name, Object value) {
		if (rh == null) {
			System.out.println("hook " + name + " is not loaded correctly!!! World is about to end!!!");
			return;
		}
		try {
			rh.set(getTarget(rh), value);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void bindHooks(ArrayList<ReflectionHook> hooks) {
		int size = hooks.size();
		int binded = 0;
		for (ReflectionHook rh : hooks) {
			if (rh.bind(this)) {
				binded++;
			}
		}
		System.out.println(getClass().getSimpleName() + ".bindhooks: binded " + binded + "/" + size + " hooks");
		Field[] fs = getClass().getDeclaredFields();
		for (Field f : fs) {
			try {
				f.setAccessible(true);
				Member val = (Member) f.get(this);
				if ((f.getName().startsWith("f_") || f.getName().startsWith("m_")) && val != null) {
					MultiLevelField ann = f.getAnnotation(MultiLevelField.class);
					if (ann != null) {
						Field supportfield = getClass().getDeclaredField("f_" + ann.supportField().toLowerCase());
						supportfield.setAccessible(true);
						multilevelfields.put(val, (Field) supportfield.get(this));
						System.out.println("field " + f.getName() + " is dependent on field value of f_" + ann.supportField().toLowerCase());
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}