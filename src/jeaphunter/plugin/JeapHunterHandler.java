package jeaphunter.plugin;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.dom.*;

import jeaphunter.JeapHunterProject;
import jeaphunter.JeapHunter;

public class JeapHunterHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		// Init plugin workspace console
		PluginConsole.init();

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();

		IProject[] projects = root.getProjects();

		detectAntiPatterns(projects);

		return null;
	}

	public static void detectAntiPatterns(IProject[] projects) {
		//PluginConsole.writeLine("Hello");

		JeapHunter hunter;
		for (IProject project : projects) {
			hunter = new JeapHunter(new JeapHunterProject(project));
			hunter.detectAllExceptionAntiPatterns();
		}
	}
}
