package jeaphunter.plugin.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

import jeaphunter.plugin.util.PluginConsole;

public class JeapHunterHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();

		IProject[] projects = root.getProjects();

		detectAnitPatterns(projects);

		return null;
	}

	public static void detectAnitPatterns(IProject[] projects) {
		PluginConsole.writeLine("Hello");
	}
}
