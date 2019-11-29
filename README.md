pcjo - Pre-Compiler Java Obfuscator
Matthew Oh
10/9/19 - 10/23/19

Pretty self-explainitory.
It's really just "a lot of" text parsing, so there will probably be lots of limitations to it.

Scans a single file and identifies class, imports, variables, and methods names.
Creates codenames for class, variable and methods.
Creates new file using codenames.
Can manually ignore statements by putting a comment before the next statements; "//PCJO_IGNORE_4" will ignore the next 4 declarations

Stuff to be implemented:
-reading from multiple files or a folder
-enumerations
-some kind of interface?
