package com.aragabz.androidtemplate.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aragabz.androidtemplate.core.database.model.AccountEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for local accounts.
 */
@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts WHERE email = :email LIMIT 1")
    suspend fun getAccountByEmail(email: String): AccountEntity?

    @Query("SELECT * FROM accounts WHERE id = :id LIMIT 1")
    suspend fun getAccountById(id: String): AccountEntity?

    @Query("SELECT * FROM accounts")
    suspend fun getAccounts(): List<AccountEntity>

    @Query("SELECT * FROM accounts")
    fun getAccountsFlow(): Flow<List<AccountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: AccountEntity)
}
