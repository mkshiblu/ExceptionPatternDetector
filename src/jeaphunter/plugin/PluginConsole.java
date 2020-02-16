package jeaphunter.plugin;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

/**
 * Console representing the console of the workspace where the plugin is
 * installed
 */
public class PluginConsole {
	private static final String CONSOLE_NAME = "Jeap Hunter";
	private static MessageConsole messageConsole;
	private static MessageConsoleStream consoleStream;

	/**
	 * Creates a new Plugin console if not exists Should be called once before
	 * calling writeline
	 */
	public static void init() {
		messageConsole = createConsoleIfNotExists(CONSOLE_NAME);
		consoleStream = messageConsole.newMessageStream();
	}

	private static MessageConsole createConsoleIfNotExists(String name) {
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

	/**
	 * Prints the message to the plugin's workspace console
	 */
	static public void writeLine(String message) {
		consoleStream.println(message);
	}
}
