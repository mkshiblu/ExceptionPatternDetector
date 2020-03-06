package jeaphunter.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.corext.callhierarchy.CallHierarchy;
import org.eclipse.jdt.internal.corext.callhierarchy.MethodCall;
import org.eclipse.jdt.internal.corext.callhierarchy.MethodWrapper;

public class ASTUtil {

	/**
	 * Holds a map of qualified name and their superclasses
	 */
	private static Map<String, Set<String>> typeSuperClasses = new HashMap<>();

	/** this only works when the method is declared in an Eclipse project **/
	public static MethodDeclaration declarationFromInvocation(MethodInvocation node) {
		MethodDeclaration md = null;
		try {
			IMethodBinding binding = node.resolveMethodBinding();
			ICompilationUnit iunit = (ICompilationUnit) binding.getJavaElement()
					.getAncestor(IJavaElement.COMPILATION_UNIT);

			if (iunit == null) {
				return null;
			}

			CompilationUnit cu = Cache.getCompilationUnit(iunit);
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
		Set<String> superTypes = typeSuperClasses.get(typeBinding.getKey());
		if (superTypes != null && superTypes.contains(potentialSuperType.getKey())) {
			return true;
		}

		ITypeBinding superClass;
		ITypeBinding currClass = typeBinding;

		while ((superClass = currClass.getSuperclass()) != null) {
			if (superClass.getKey().equals(potentialSuperType.getKey())) {
				// Add to the cache
				if (superTypes == null) {
					superTypes = new HashSet<>();
					typeSuperClasses.put(typeBinding.getKey(), superTypes);
				}
				superTypes.add(potentialSuperType.getKey());
				return true;
			}
			currClass = superClass;
		}

		return false;
	}

	public static IMethodBinding getAncestorMethod(MethodInvocation methodInvoke) {

		IMethodBinding methodBinding = methodInvoke.resolveMethodBinding();

		if (methodBinding != null) {
			IMethod method = (IMethod) (methodBinding.getJavaElement().getAncestor(IJavaElement.METHOD));

			if (method != null) {
				method = null;
			}
		}

		return null;

	}

	public static Set<ITypeBinding> getMatchedSubClasses(ITypeBinding typeBindinng,
			Set<ITypeBinding> potentialSubClasses) {
		Set<ITypeBinding> subClasses = new HashSet<>();
		for (ITypeBinding subClass : potentialSubClasses) {
			if (isSubClass(subClass, typeBindinng))
				subClasses.add(subClass);
		}
		return subClasses;
	}

	public void callGraph(IMember[] methods) {
		CallHierarchy hierarchy = null;
		IJavaSearchScope searchScope = SearchEngine.createWorkspaceScope();
		hierarchy.setSearchScope(searchScope);
		ArrayList<MethodCall> methodCalls = new ArrayList<MethodCall>();

		MethodWrapper[] callerWrapper = hierarchy.getCallerRoots(methods);
		ArrayList<MethodWrapper> callsWrapper = new ArrayList<MethodWrapper>();
		for (int i = 0; i < callerWrapper.length; i++) {
			callsWrapper.addAll(Arrays.asList(callerWrapper[i].getCalls(new NullProgressMonitor())));
		}

		for (int i = 0; i < callsWrapper.size(); i++)
			methodCalls.add(callsWrapper.get(i).getMethodCall());
		// Now you will get method calls in methodCalls list.
		IMember member = methodCalls.get(0).getMember();
	}

	/**
	 * Clears the calculated hierarchy cache
	 */
	public static void clearCache() {
		typeSuperClasses.clear();
	}
}
