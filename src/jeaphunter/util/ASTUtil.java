package jeaphunter.util;

import java.util.ArrayList;
import java.util.List;

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
}
