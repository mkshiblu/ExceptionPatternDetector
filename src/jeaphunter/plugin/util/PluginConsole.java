package jeaphunter.plugin.util;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class PluginConsole {
	private static final String CONSOLE_NAME = "Jeap Hunter";
	private static MessageConsole messageConsole;
	private static MessageConsoleStream consoleStream;

	public PluginConsole() {
		init();
	}	

	private void init() {
		messageConsole = createConsoleIfNotExists(CONSOLE_NAME);
		consoleStream = messageConsole.newMessageStream();
	}

	private MessageConsole createConsoleIfNotExists(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager consoleManager = plugin.getConsoleManager();
		IConsole[] existing = consoleManager.getConsoles();

		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];

		// no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		consoleManager.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}

	// To print messages into the Debug view, not just in the console here.
	static public void writeLine(String message) {
		consoleStream.println(message);
	}
}
