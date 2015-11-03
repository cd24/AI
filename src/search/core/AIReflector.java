package search.core;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

@SuppressWarnings("rawtypes")
public class AIReflector<T> {
	private final static String suffix = ".class";
	
	private Map<String,Class> name2type;
	
	private FilenameFilter filter = new FilenameFilter(){public boolean accept(File dir, String name) {
			return name.endsWith(suffix);
		}};
		
	public AIReflector(Class superType, String packageName, Class... paramTypes) {
		this.name2type = new TreeMap<String,Class>();
		
		String targetDirName = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		File targetDir = new File(targetDirName + packageName.replace('.', File.separatorChar));
		if (!targetDir.isDirectory()) {throw new IllegalArgumentException(targetDir + " is not a directory");}
		for (File f: targetDir.listFiles(filter)) {
			testAndAdd(f.getName(), superType, packageName, paramTypes);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void testAndAdd(String name, Class superType, String packageName, Class... paramTypes) {
		name = name.substring(0, name.length() - suffix.length());
		try {
			Class type = Class.forName(packageName + "." + name);
			type.newInstance();
			if (superType.isAssignableFrom(type)) {
				name2type.put(name, type);
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			// If an exception is thrown, we omit the type.
			// Hence, ignore this exceptions.
		}
	}
	
	public ArrayList<String> getTypeNames() {
		return new ArrayList<String>(name2type.keySet());
	}
	
	public String toString() {
		String result = "Available:";
		for (String s: name2type.keySet()) {
			result += " " + s;
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public T newInstanceOf(String typeName) throws InstantiationException, IllegalAccessException {
		return (T)name2type.get(typeName).newInstance();
	}
}
