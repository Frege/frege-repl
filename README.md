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
<td>To quit REPL or Paste mode</td>
</tr>
<tr>
<td>:list</td>
<td>To list the identifiers along with types</td>
</tr>
<tr>
<td>:h</td>
<td>To display the scripts evaluated so far</td>
</tr>
<tr>
<td>:version</td>
<td>To display Frege version</td>
</tr>
<tr>
<td>:l &lt;path&gt;</td>
<td>To load Frege code snippets from a file. All the declarations from the file will be compiled into default
REPL module. Hence the Frege source file cannot have module declaration.
cannot have a module name.</td>
</tr>
<tr>
<td>:r</td>
<td>To reload the last script file</td>
</tr>
<tr>
<td>:reset</td>
<td>To reset the REPL. All the evaluated scripts will be discarded.</td>
</tr>
<tr>
<td>:help</td>
<td>To display this help message</td>
</tr>
</table>

##How to run?##
1. Download frege-repl-XX.zip from http://code.google.com/p/frege/downloads/list
2. Extract the archive into some folder
3. Run the following command from that folder: (JRE 7 required) <BR/>
   ```java -jar frege-repl-1.0.jar```
