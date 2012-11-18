#Frege REPL#

This is the command line REPL for Frege. Frege code snippets can be entered on the prompt to get them evaluated.
In addition to Frege code, the following commands are supported:

<table>
<tr>
<th>Command</th>
<th>Description</th>
</tr>
<tr>
<td>:t expression</td>
<td>To print the type of an expression</td>
</tr>
<tr>
<td>:p</td>
<td>Paste mode: To enter multi-line/multiple definitions</td>
</tr>
<tr>
<td>:q</td>
<td>To quit the REPL or Paste mode</td>
</tr>
<tr>
<td>:l</td>
<td>To list the identifiers along with types</td>
</tr>
<tr>
<td>:h</td>
<td>To list the scripts evaluated so far</td>
</tr>
<tr>
<td>:r</td>
<td>To reset the REPL. All the evaluated scripts will be discarded.</td>
</tr>
<tr>
<td>:version</td>
<td>To print Frege version</td>
</tr>
</table>

##How to run?##
1. Download frege-repl-XX.jar, frege-java6-XX.jar, frege-scripting-XX.jar from Downloads page: 
https://github.com/Frege/frege/downloads where XX indicates the latest version.
2. Download the following external dependencies from Maven repository:
  * ecj-3.7.jar  : http://mvnrepository.com/artifact/org.eclipse.jdt.core.compiler/ecj/3.7
  * jline-1.0.jar: http://mvnrepository.com/artifact/jline/jline/1.0
3. Run the following command from the folder containing all the above jars: (XX - version number, Minimum JRE: Java 6)
   ```java -jar frege-repl-XX.jar```

##Known Issues##
1. Instance and Derive declarations cannot be redefined in an REPL session. As a workaround, we can copy the scripts
evaluated so far using ":h" command, then reset the REPL with ":r" and run again with the new declarations. 