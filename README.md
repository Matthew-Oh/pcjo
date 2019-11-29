pcjo - Pre-Compiler Java Obfuscator
Matthew Oh
10/9/19 - 11/29/19

Pretty self-explainitory.
It's really just "a lot of" text parsing, so there will probably be lots of limitations to it.

Scans a single file or a folder of files and identifies class, imports, variables, and methods names.
Creates codenames for class, variable and methods.
Creates new files using codenames.
Can manually ignore statements by putting a comment before the next statements; "//PCJO_IGNORE_4" will ignore the next 4 declarations. Only works for a single digit, probably shouldn't need more than 9 realistically.

11/29/19
I guess this program is actually in a working state at this. The code is kind of a mess, and there is probably a lot of bugs and glitches, but it seems to work fine on bsptest. It might glitch and override the actual source code, so I would make sure to backup the source code just to be safe.

Stuff to be implemented:
-enumerations
-some kind of interface?
-make the code less garbage?
