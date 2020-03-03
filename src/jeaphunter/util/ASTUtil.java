package jeaphunter.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TagElement;

public class ASTUtil {

	private static Map<ITypeBinding, Set<ITypeBinding>> typeSuperClasses = new HashMap<>();

	/** this only works when the method is declared in an Eclipse project **/
	public static MethodDeclaration declarationFromInvocation(MethodInvocation node) {
		MethodDeclaration md = null;
		try {
			IMethodBinding binding = (IMethodBinding) node.getName().resolveBinding();
			ICompilationUnit unit = (ICompilationUnit) binding.getJavaElement()
					.getAncestor(IJavaElement.COMPILATION_UNIT);

			if (unit == null) {
				// not available, external declaration
				return null;
			}

			ASTParser parser = ASTParser.newParser(AST.JLS13);
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			parser.setSource(unit);
			parser.setResolveBindings(true);
			CompilationUnit cu = (CompilationUnit) parser.createAST(null);
			md = (MethodDeclaration) cu.findDeclaringNode(binding.getKey());
		} catch (Exception ex) {
			System.out.println("Cannot resolve declaration for: " + node);
		}
		return md;
	}

	public static List<ITypeBinding> getThrowableExceptionsFromJavadoc(Javadoc javadoc) {
		List<TagElement> tags = javadoc.tags();
		List<ITypeBinding> thownTypes = new ArrayList<>();

		for (TagElement tag : tags) {
			String name = tag.getTagName();

			if ("@throws".equals(name) || "@exception".equals(name)) {
				List fragments = tag.fragments();

				if (fragments != null && fragments.size() > 0) {
					Object fragment = fragments.get(0);
					if (fragment instanceof SimpleName) {
						SimpleName node = ((SimpleName) fragment);
						ITypeBinding typeBinding = node.resolveTypeBinding();

						if (typeBinding == null) {
							System.out.println("Cannot resolve type binding: " + node);
						} else {
							thownTypes.add(typeBinding);
						}
					}
				}
			}
		}
		return thownTypes;
	}

	// TODO Optimize by caching
	/**
	 * Returns the topmost super class that watch matched from the potential super
	 * types otherwise null
	 */
	public static ITypeBinding getTopMostSuperClass(final ITypeBinding typeBinding,
			final Set<ITypeBinding> potentialSuperTypes) {
		ITypeBinding currClass = typeBinding;
		ITypeBinding superClass;
		ITypeBinding topMostMatchedSuperClass = null;

		while ((superClass = currClass.getSuperclass()) != null) {
			for (ITypeBinding superType : potentialSuperTypes) {
				if (superClass.getQualifiedName().equals(superType.getQualifiedName()))
					topMostMatchedSuperClass = superClass;
			}
			currClass = superClass;
		}

		return topMostMatchedSuperClass;
	}

	public static boolean isSubClass(final ITypeBinding typeBinding, final Set<ITypeBinding> potentialSuperTypes) {
		ITypeBinding currClass = typeBinding;
		ITypeBinding superClass;

		while ((superClass = currClass.getSuperclass()) != null) {
			for (ITypeBinding superType : potentialSuperTypes) {
				if (superClass.getQualifiedName().equals(superType.getQualifiedName()))
					return true;
			}
			currClass = superClass;
		}

		return false;
	}

	/**
	 * Returns true if the type binding is the subclass of the potential supertype
	 */
	public static boolean isSubClass(final ITypeBinding typeBinding, final ITypeBinding potentialSuperType) {
		// Get the already calculated super types
		Set<ITypeBinding> superTypes = typeSuperClasses.get(typeBinding);
		if (superTypes != null && superTypes.contains(potentialSuperType)) {
			return true;
		}

		ITypeBinding superClass;
		ITypeBinding currClass = typeBinding;

		while ((superClass = currClass.getSuperclass()) != null) {
			if (superClass.getQualifiedName().equals(potentialSuperType.getQualifiedName())) {

				// Add to the cache
				if (superTypes == null) {
					superTypes = new HashSet<>();
					typeSuperClasses.put(typeBinding, superTypes);
				}
				superTypes.add(potentialSuperType);

				return true;
			}
			currClass = superClass;
		}

		return false;
	}

	/**
	 * Clears the calculated hierarchy cache
	 */
	public static void clearCache() {
		typeSuperClasses.clear();
	}
}
