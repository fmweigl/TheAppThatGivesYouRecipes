# R8 rules for the release build. The libraries (kotlinx.serialization, Ktor, Koin, Coil, Compose,
# Navigation 3) ship their own consumer rules; only what they don't cover goes here.

# Keep line numbers for readable stack traces (Play Console deobfuscates with the mapping file),
# but hide the source file names.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
