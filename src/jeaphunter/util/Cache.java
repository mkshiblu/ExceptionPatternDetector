package jeaphunter.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class Cache {

	public static final int CAPACITY = 10;
	private static Map<ICompilationUnit, CompilationUnit> iCompMap = new HashMap<>();
	private static Map<IClassFile, CompilationUnit> iClassFileMap = new HashMap<>();

	/**
	 * Keeps track of the binary files that cannot be resolved
	 */
	private static Set<IClassFile> unResolvedClassFiles = new HashSet<>();

	/**
	 * Returns the corresponding compilation unit from the cache or create one with
	 * resolved binding
	 */
	public static CompilationUnit getCompilationUnit(final ICompilationUnit iCompilationUnit) {

		if (iCompMap.containsKey(iCompilationUnit)) {
			return iCompMap.get(iCompilationUnit);
		}

		CompilationUnit unit = parse(iCompilationUnit, true);
		if (unit != null) {
			if (hasICompReachedMaxCapacity()) {
				iCompMap.clear();
			}
			iCompMap.put(iCompilationUnit, unit);
		}

		return unit;
	}

	public static CompilationUnit getCompilationUnit(IClassFile classFile) {
		// Returns from cache if exists
		if (iClassFileMap.containsKey(classFile)) {
			return iClassFileMap.get(classFile);
		}

		CompilationUnit unit = parse(classFile, true);
		if (unit != null) {
			if (hasIClassReachedMaxCapacity()) {
				iClassFileMap.clear();
			}
			iClassFileMap.put(classFile, unit);
		}
		return unit;
	}

	/**
	 * Parse and return the underlying compilation unit
	 */
	public static CompilationUnit parse(IClassFile classFile, boolean resolveBinding) {
		CompilationUnit compilationUnit = null;

		if (!unResolvedClassFiles.contains(classFile)) {
			try {
				ASTParser parser = ASTParser.newParser(AST.JLS13);
				parser.setSource(classFile);
				parser.setResolveBindings(true);
				compilationUnit = (CompilationUnit) parser.createAST(null);
			} catch (IllegalStateException e) {
				System.out.println("Cannot get compilation unit for classfile : " + classFile);
				unResolvedClassFiles.add(classFile);
			}
		}
		return compilationUnit;
	}

	/**
	 * Parse and retuns compilation unit for the the binary class file
	 */
	public static CompilationUnit parse(ICompilationUnit iUnit, boolean resolveBinding) {
		ASTParser parser = ASTParser.newParser(AST.JLS13);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(iUnit);
		parser.setResolveBindings(resolveBinding);
		CompilationUnit unit = (CompilationUnit) parser.createAST(null);
		return unit;
	}

	public static void clearICompilationCache() {
		iCompMap.clear();
	}

	public static void clearLibraryCache() {
		iClassFileMap.clear();
	}

	public static void clear() {
		clearICompilationCache();
		clearLibraryCache();
	}

	public static boolean hasICompReachedMaxCapacity() {
		return iCompMap.size() >= CAPACITY;
	}

	public static boolean hasIClassReachedMaxCapacity() {
		return iClassFileMap.size() >= CAPACITY;
	}
}
