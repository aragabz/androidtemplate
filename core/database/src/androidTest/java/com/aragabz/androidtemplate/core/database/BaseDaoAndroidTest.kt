package com.aragabz.androidtemplate.core.database

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aragabz.androidtemplate.core.database.dao.BaseDao
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BaseDaoAndroidTest {
    private lateinit var database: TestDatabase
    private lateinit var dao: TestEntityDao

    @Before
    fun setUp() {
        database =
            Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                TestDatabase::class.java,
            ).allowMainThreadQueries().build()
        dao = database.testEntityDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun upsertAndDelete_roundTripsEntity() = runBlocking {
        val entity = TestEntity(id = "1", value = "first")

        dao.upsert(entity)
        assertEquals(listOf(entity), dao.getAll())

        dao.delete(entity)
        assertEquals(emptyList<TestEntity>(), dao.getAll())
    }
}

@Entity(tableName = "test_entities")
data class TestEntity(
    @PrimaryKey val id: String,
    val value: String,
)

@Dao
interface TestEntityDao : BaseDao<TestEntity> {
    @Query("SELECT * FROM test_entities")
    suspend fun getAll(): List<TestEntity>
}

@Database(entities = [TestEntity::class], version = 1, exportSchema = false)
abstract class TestDatabase : RoomDatabase() {
    abstract fun testEntityDao(): TestEntityDao
}
