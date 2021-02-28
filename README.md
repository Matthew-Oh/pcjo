# pcjo - Pre-Compiler Java Obfuscator


Obfuscates java code by reading java source files and creating a new version with obfuscated identifiers.  
It is just a lot of text parsing, and there is likely to be a lot of limitations.  

## Usage

Pass the name of a java file or folder of java files that is in the input folder as a command line argument to Obfuscator's main method.  
You can ignore obfuscation for new identifiers by commenting `//<PJCO_IGNORE_#>`, replacing the # with the number of new indentifiers to be ignored after the comment.

##

Matthew Oh  
10/9/19 - 11/29/19  