package jeaphunter.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class Cache {

	private static Map<ICompilationUnit, CompilationUnit> iCompMap = new HashMap<>();

	public static CompilationUnit getCompilationUnit(ICompilationUnit iCompilationUnit) {

		if (iCompMap.containsKey(iCompilationUnit)) {
			return iCompMap.get(iCompilationUnit);
		}

		CompilationUnit unit = parse(iCompilationUnit, true);
		iCompMap.put(iCompilationUnit, unit);
		return unit;
	}

	public static CompilationUnit parse(ICompilationUnit iUnit, boolean resolveBinding) {
		ASTParser parser = ASTParser.newParser(AST.JLS13);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(iUnit);
		parser.setResolveBindings(resolveBinding);
		CompilationUnit unit = (CompilationUnit) parser.createAST(null);
		return unit;
	}

	public static void clear() {
		iCompMap.clear();
	}
}
