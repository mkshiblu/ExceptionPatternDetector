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
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.dom.*;

import jeaphunter.JeapHunterProject;
import jeaphunter.util.ASTUtil;
import jeaphunter.JeapHunter;

public class JeapHunterHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		// Init plugin workspace console
		PluginConsole.init();
		JeapHunter.Console = new PluginConsole();
		ASTUtil.clearCache();
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();

		IProject[] projects = root.getProjects();

		new Thread(new Runnable() {
			@Override
			public void run() {
				detectAntiPatterns(projects);
			}
			
		}).start();
		
		return null;
	}

	public static void detectAntiPatterns(IProject[] projects) {
		JeapHunter hunter;
		for (IProject project : projects) {
			hunter = new JeapHunter(new JeapHunterProject(project));
			hunter.detectAllExceptionAntiPatterns();
		}
	}
}
