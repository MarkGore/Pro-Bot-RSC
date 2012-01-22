package net.probot.rsc.plugin;

import java.net.URL;
import java.net.URLClassLoader;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;

public class PermissionUCL extends URLClassLoader {
	public static PermissionCollection makeAllPermission() {
		final PermissionCollection pc = new Permissions();
		pc.add(new AllPermission());
		return pc;
	}

	private final PermissionCollection collection;

	public PermissionUCL(final URL[] urls) {
		super(urls);
		collection = PermissionUCL.makeAllPermission();
	}

	public PermissionUCL(final URL[] urls, final ClassLoader parent) {
		super(urls, parent);
		collection = PermissionUCL.makeAllPermission();
	}

	public PermissionUCL(final URL[] urls, final ClassLoader parent,
			final PermissionCollection collection) {
		super(urls, parent);
		this.collection = collection;
	}

	public PermissionUCL(final URL[] urls, final PermissionCollection collection) {
		super(urls);
		this.collection = collection;
	}

	@Override
	protected PermissionCollection getPermissions(final CodeSource codesource) {
		return collection;
	}
}
