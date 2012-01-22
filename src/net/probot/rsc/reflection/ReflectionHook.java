package net.probot.rsc.reflection;

import java.lang.reflect.Field;

import java.lang.reflect.Method;
import java.util.ArrayList;

import net.probot.rsc.bot.Bot;


/**
 * @author Mark Gore
 * 
 */
public class ReflectionHook {

	private String name = null;
	private String clazz = null;
	private String field = null;
	private Bot loaderInstance = null;

	public ReflectionHook(Bot l, String name, String clazz, String field) {
		this.name = name;
		this.clazz = clazz;
		this.field = field;
		this.loaderInstance = l;
	}

	public String getFieldName() {
		return field;
	}

	public String getClassName() {
		return clazz;
	}

	@Override
	public String toString() {
		return (name + " gets " + clazz + "." + field);
	}

	private Class<?> getClass(String clazz) {
		try {
			Class<?> classy = (Class<?>) loaderInstance.getClientInitialation()
					.getClass();
			return classy;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public int getIntReflection(String clazzz, String fieldz) {
		try {
			Class clazz = getClass(clazzz);
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				if (field.getName().equals(fieldz)) {
					field.setAccessible(true);
					return field.getInt(null);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	private Field getField(String clazz, String field) throws Exception {
		Class<?> clazzObj = getClass(clazz);
		Field fieldObj = clazzObj.getDeclaredField(field);
		fieldObj.setAccessible(true);
		return fieldObj;
	}

	public boolean bind(Object target) {
		try {
			System.out.print("trying to bind " + name + " to "
					+ target.getClass().getName());
			String realname = name.toLowerCase();
			String basefieldname = null;
			if (name.contains(".")) {
				String[] splits = name.split("\\.");
				if (splits.length > 2) {
					throw new Exception(
							"More than 1 sublevel binding is not supported!!");
				}
				basefieldname = splits[0];
				realname = splits[1].toLowerCase();
				// get real method field
			}
			boolean isMethod = isMethod();
			Field f = null;
			if (isMethod) {
				f = target.getClass().getDeclaredField(
						"m_"
								+ realname.substring(0, realname.indexOf("("))
										.toLowerCase());
			} else {
				f = target.getClass().getDeclaredField("f_" + realname);
			}
			if (f != null) {
				f.setAccessible(true);
				// multilevel stuff
				if (basefieldname != null) {
					MultiLevelField ann = f
							.getAnnotation(MultiLevelField.class);
					if (ann == null) {
						System.err
								.println("multilevel binding must have MultiLevelField annotation present on target field!!");
						return false;
					}
					String supportfield = ann.supportField().toLowerCase();
					if (!basefieldname.equals(supportfield)) {
						System.err
								.println("multilevel binding must have MultiLevelField.supportField name matching the subfield name!!\r\n"
										+ supportfield + " != " + basefieldname);
						return false;
					}
				}
				f.set(target,
						isMethod ? getMethod(clazz, field) : getField(clazz,
								field));
				System.out.println(" binded");
				return true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public boolean isMethod() {
		return name.contains("(") && name.contains(")");
	}

	private Method getMethod(String clazz, String method) throws Exception {
		Class[] types = extractTypes(method);
		if (types == null) {
			return null;// no method
		}
		Class<?> clazzObj = getClass(clazz);
		Method fieldObj = clazzObj.getDeclaredMethod(
				method.substring(0, method.indexOf("(")), types);
		fieldObj.setAccessible(true);
		return fieldObj;
	}

	private static Class[] extractTypes(String methodname) {
		int start = methodname.indexOf("(");
		int end = methodname.indexOf(")");
		if (start == -1 || end == -1) {
			return null;
		}
		if (start > end || start + 1 == end) {
			return new Class[] {};
		}
		String types = methodname.substring(start + 1, end).trim()
				.toLowerCase();
		if (types.length() == 0) {
			return new Class[] {};
		}
		String[] typearr = types.split(",");
		ArrayList<Class> classes = new ArrayList<Class>();
		for (String type : typearr) {
			if (type.trim().length() == 0) {
				continue;
			}
			if ("bool".equals(type) || "boolean".equals(type)) {
				classes.add(boolean.class);
				continue;
			}
			if ("int".equals(type) || "integer".equals(type)) {
				classes.add(int.class);
				continue;
			}
			if ("long".equals(type)) {
				classes.add(long.class);
				continue;
			}
			if ("string".equals(type)) {
				classes.add(String.class);
				continue;
			}
			if ("byte".equals(type)) {
				classes.add(byte.class);
				continue;
			}
			if ("char".equals(type)) {
				classes.add(char.class);
				continue;
			}
			if ("int[]".equals(type)) {
				classes.add(new int[] {}.getClass());
				continue;
			}
			if ("byte[]".equals(type)) {
				classes.add(new byte[] {}.getClass());
				continue;
			}
			if ("char[]".equals(type)) {
				classes.add(new char[] {}.getClass());
				continue;
			}
			if ("string[]".equals(type)) {
				classes.add(new String[] {}.getClass());
				continue;
			}
			classes.add(Object.class);
		}
		return classes.toArray(new Class[] {});
	}

	public String getName() {
		return name;
	}
}
