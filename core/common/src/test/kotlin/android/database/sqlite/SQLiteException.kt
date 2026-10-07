package android.database.sqlite

// JVM stand-ins for the Android classes: core:common matches SQLite errors by class name.
open class SQLiteException(
    message: String,
) : RuntimeException(message)

class SQLiteConstraintException(
    message: String,
) : SQLiteException(message)
