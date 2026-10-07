# App-specific R8 rules.
# Hilt, Room, Retrofit, OkHttp, WorkManager and kotlinx.serialization ship their own consumer rules,
# so only add rules here for code that is reached by reflection and not covered by those libraries.
