# Common Android Studio Modules

This project contains several common Android "libraries" that can be imported into other Android Studio projects as modules.  All modifications/additions should be done in this project and then re-imported into others using this code.

# Modules
## AppCache (Java library)
- A global singleton app cache
- Provides type safety through AppCacheTypeReference
- AppCacheTypeReference currently DOES NOT support objects that do not have an empty constructor