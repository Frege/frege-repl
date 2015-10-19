#Frege REPL [![Build Status](https://travis-ci.org/Frege/frege-repl.svg)](https://travis-ci.org/Frege/frege-repl)

This is the command line REPL for Frege. Frege code snippets can be entered on the prompt to get them evaluated.
In addition to Frege code, the following commands are supported:

<table>
<tr>
<th>Command</th>
<th>Description</th>
</tr>
<tr>
<td>:type &lt;expression&gt;</td>
<td>Display the type of an expression</td>
</tr>
<tr>
<td>:browse &lt;moduleName&gt;</td>
<td>Display the names in a module if a module name is provided otherwise display the names in the default REPL module</td>
</tr>
<tr>
<td>:java</td>
<td>View Java translation of last compiled Frege source</td>
</tr>
<tr>
<td>:{</td>
<td>Start multiline definitions</td>
</tr>
<tr>
<td>:}</td>
<td>End multiline definitions</td>
</tr>
<tr>
<td>:load &lt;url or file&gt;</td>
<td>Load Frege code snippets from an URL or file</td>
</tr>
<tr>
<td>:r</td>
<td>Reload the last script URL or file</td>
</tr>
<tr>
<td>:history</td>
<td>Display the source history for definitions in the default REPL module</td>
</tr>
<tr>
<td>:reset</td>
<td>Reset the REPL discarding all the evaluated scripts</td>
</tr>
<tr>
<td>:version</td>
<td>Display Frege version</td>
</tr>
<tr>
<td>:help &lt;name&gt;</td>
<td>Display the documentation for the given name. If the name is not provided, display this help message</td>
</tr>
<tr>
<td>:q or :quit</td>
<td>Quit REPL</td>
</tr>
</table>

##How to run?##
1. Download Frege REPL archive from [releases](https://github.com/Frege/frege-repl/releases).
1. Extract the archive
1. Run the following command: (JRE 7 or above required)
   * `$ frege-repl-<version>/bin/frege-repl`

##Build from sources##

1. ```~/workspace$ git clone https://github.com/Frege/frege-repl.git```
1. ```~/workspace/frege-repl$ ./gradlew install```
1. To run, ```~/workspace/frege-repl$ ./gradlew --no-daemon --console plain :frege-repl-core:run```.
   
## Continuous Integration

[Travis](https://travis-ci.org/Frege/frege-repl/)

[Sonatype](https://oss.sonatype.org/content/groups/public/org/frege-lang/)

