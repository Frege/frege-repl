package frege.repl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import jline.ConsoleReader;
import jline.Terminal;
import frege.script.JFregeInterpreter;
import frege.script.JInterpreterResult;

/**
 * A console Frege REPL
 * 
 */
public class ConsoleRepl {
	private static final String PROMPT = "frege> ";
	private final ConsoleReader reader;

	public ConsoleRepl(final InputStream in, final OutputStream out)
			throws Exception {
		final Writer writerout = new OutputStreamWriter(out,
				Charset.forName("UTF-8"));
		final ConsoleReader reader = new ConsoleReader(in, writerout);
		reader.setBellEnabled(false);
		final Terminal terminal = reader.getTerminal();
		terminal.disableEcho();
		this.reader = reader;
	}

	/**
	 * reads a single line of script
	 * @return the script
	 * @throws IOException
	 */
	public String readLine() throws IOException {
		return reader.readLine(PROMPT);
	}

	/**
	 * reads multiple line of input in "paste" mode until ":q" is typed on a new line
	 * @return the multi-line script
	 * @throws IOException
	 */
	public String readLines() throws IOException {
		final StringWriter script = new StringWriter();
		final String newLine = newLine();
		String line;
		//No prompt on each line in paste mode
		while ((line = reader.readLine("")) != null) { 
			if (line.startsWith(":q")) {
				println("Interpreting...", MessageType.INFO);
				break;
			}
			script.append(line);
			script.append(newLine);
		}
		return script.toString();
	}

	private static final String newLine() {
		return System.getProperty("line.seperator", "\n");
	}

	public void print(final String message, final MessageType messageType)
			throws IOException {
		reader.printString(message);
		reader.flushConsole();
	}

	public void println(final Object scriptResult, final MessageType messageType)
			throws IOException {
		reader.printString(scriptResult != null ? scriptResult.toString() : "");
		reader.printNewline();
		reader.flushConsole();
	}

	public static void main(final String[] args) {
		try {
			final ConsoleRepl consoleRepl = new ConsoleRepl(System.in,
					new PrintStream(System.out, true, "UTF-8"));
			consoleRepl.loop();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * returns Frege compiler version and the JVM version
	 * @return
	 * @throws Exception
	 */
	public String welcome() throws Exception {
		JFregeInterpreter.interpret("1", ""); //Warmup request
		return (String.format("Welcome to Frege %s (%s %s, %s)", 
				JFregeInterpreter.interpret(":version", "").getValue(), System
				.getProperty("java.vm.vendor"), System
				.getProperty("java.vm.name"), System
				.getProperty("java.version")));
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void loop() throws Exception {
		println(welcome(), MessageType.INFO);
		reader.printNewline();
		String history = "";
		while (true) {
			final String script = readScript();
			final String lowercaseScript = script.trim().toLowerCase();
			try {
				if (script.isEmpty()) continue;
				if (lowercaseScript.startsWith(":q")) {
					break;
				} else if (lowercaseScript.startsWith(":h")) {
					println(history.trim(), MessageType.SUCCESS);
				} else if (lowercaseScript.startsWith(":r")) {
					history = "";
					println("Reset!", MessageType.SUCCESS);
				} else {
					final JInterpreterResult result = 
							JFregeInterpreter.interpret(script, history);
					println(result.getValue(), MessageType.SUCCESS);
					history = result.getScript();
				}
			} catch (final Exception e) {
				println(getRootCause(e).getMessage(), MessageType.ERROR);
			}
		}
	}

	/**
	 * Utility to return the exception root cause. Recursive causes are not 
	 * handled.
	 * @param e
	 * @return the root cause
	 */
	private Throwable getRootCause(final Throwable e) {
		return e.getCause() == null ? e : getRootCause(e.getCause());
	}

	/**
	 * reads a script from the console. Unless in paste mode, it returns a single
	 * line of script. In paste mode, triggered by ":p" on the prompt, lines until
	 * ":q" on a new line are returned.
	 * @return the script
	 * @throws IOException
	 */
	private String readScript() throws IOException {
		String line = "";
		while ((line = readLine()) == null || line.isEmpty())
			;
		if (line.trim().toLowerCase().startsWith(":p")) {
			line = readLines();
		} 
		return line;
	}

}
