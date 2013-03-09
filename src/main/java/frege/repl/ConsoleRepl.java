package frege.repl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.Writer;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Scanner;

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
    private StringBuilder history;
    private URLClassLoader classLoader;
    private String lastScriptFile;
    private Map<String, String> fileScripts;

    public ConsoleRepl(final InputStream in, final OutputStream out)
	throws Exception {
	final Writer writerout = new OutputStreamWriter(out,
							Charset.forName("UTF-8"));
	final ConsoleReader reader = new ConsoleReader(in, writerout);
	reader.setBellEnabled(false);
	final Terminal terminal = reader.getTerminal();
	terminal.disableEcho();
	this.reader = reader;
	this.history = new StringBuilder();
	this.classLoader = getClassLoader();
	this.lastScriptFile = "";
	this.fileScripts = 
	    new LinkedHashMap<String, String>();

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
	JFregeInterpreter.interpret("1", "", "T", getClassLoader()); //Warmup request
	return (String.format("Welcome to Frege %s (%s %s, %s)", 
			      JFregeInterpreter.interpret(":version", "", "", 
							  getClassLoader()).getValue(), System
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
	while (true) {
	    final String script = readScript();
	    if (script.isEmpty()) continue;
	    if (script.toLowerCase().startsWith(":q")) {
		break;
	    }
	    eval(script);
	    reader.printNewline();
	}
    }

    private void eval(final String script) throws IOException {
	if (script.isEmpty()) return;
	final String lowercaseScript = script.trim().toLowerCase();
	try {
	    if (lowercaseScript.startsWith(":help")) {
		printHelp();
	    } else if (lowercaseScript.startsWith(":h")) {
		final String hist = history.toString();
		if (!hist.isEmpty())
		    println(hist.trim(), MessageType.SUCCESS);
	    } else if (lowercaseScript.startsWith(":reset")) {
		reset();
		println("Reset!", MessageType.SUCCESS);
	    } else if (lowercaseScript.startsWith(":r")) {
		if (!lastScriptFile.isEmpty())
		    eval(":l " + lastScriptFile);
	    } else if (isLoadCommand(script)) {
		final String fileName = parseFileNameFromLoadCommand(script);
		final String fileScript = readFile(fileName);
		executeScript(fileScript);
		fileScripts.put(fileName, fileScript);
		this.lastScriptFile = fileName;
	    } else {
		final JInterpreterResult result = executeScript(script);
		history.append("\n" + result.getScript());
	    }
	} catch (final Exception e) {
	    println(getRootCause(e).getMessage(), MessageType.ERROR);
	}

    }

    public void reset() {
	history = new StringBuilder();
	lastScriptFile = "";
	fileScripts = new LinkedHashMap<String, String>();
    }

    public JInterpreterResult executeScript(final String script) throws Exception {
	final String existingScripts = combineScripts(script, history, fileScripts);
	final JInterpreterResult result = 
	    JFregeInterpreter.interpret(script, existingScripts, 
					"FregeScript", 
					classLoader);
	if (!result.getValue().isEmpty()) {
	    println(result.getValue(), MessageType.SUCCESS);
	}
	return result;
    }

    private static String combineScripts(final String currentScript, 
					 final StringBuilder history,
					 final Map<String, String> fileScripts) {
	final StringBuilder hist = new StringBuilder("\n");
	final boolean isLoadCommand = isLoadCommand(currentScript);
	final String fileName = isLoadCommand 
	    ? parseFileNameFromLoadCommand(currentScript) : "";
	for (final Map.Entry<String, String> fileScript: fileScripts.entrySet()) {
	    //If not loading an existing script
	    if (!(isLoadCommand && fileScript.getKey().equals(fileName))) {
		hist.append(fileScript.getValue() + "\n");
	    }
	}
	hist.append(history);
	return hist.toString();

    }

    public static boolean isLoadCommand(final String script) {
	final String lowercaseScript = script.trim().toLowerCase();
	return lowercaseScript.startsWith(":l ") ||
	    lowercaseScript.startsWith(":load ");
    }

    public static String parseFileNameFromLoadCommand(final String script) {
	final String[] parts = script.split(" ");
	if (parts.length >= 2) {
	    return parts[1].trim();
	} else {
	    throw new RuntimeException("Missing script file name!");
	}
    }

    public static String readFile(final String fileName) throws Exception {
	return new Scanner(new File(fileName)).useDelimiter("\\Z").next();
    }
  
    public static URLClassLoader getClassLoader() {
	final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	if (classLoader instanceof URLClassLoader) {
	    return (URLClassLoader) classLoader;
	} else {
	    return new URLClassLoader(new URL[] {}, classLoader);
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
    
    public static void printHelp() {
	System.out.println("At the prompt, you can enter Frege code snippets to get them evaluated.");
	System.out.println("The output or compilation errors will be printed below the prompt.");
	System.out.println("In addition to Frege code, the following commands are supported:");
	printHelp(":t <expression>", "To print the type of an expression");
	printHelp(":p", "To enter paste mode, for multi-line/multiple definitions");
	printHelp(":q", "To quit REPL or paste mode");
	printHelp(":list", "To list the identifiers along with types");
	printHelp(":h", "To display the scripts evaluated so far");
	printHelp(":version", "To display Frege version");
	printHelp(":l <path>", "To load Frege code snippets from a file");
	printHelp(":r", "To reload the last script file");
	printHelp(":reset", "To reset the session discarding all evaluated scripts");
	printHelp(":help", "To display this help message");
    }
  
    public static void printHelp(String cmd, String message) {
	System.out.printf("%-20s - %s\n", cmd, message);
    }

}
