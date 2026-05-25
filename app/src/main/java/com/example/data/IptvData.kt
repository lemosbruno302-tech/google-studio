package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// --- ENTITIES ---

@Entity(tableName = "users")
data class IptvUser(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val pass: String,
    val role: String // "ADMIN", "EDITOR", "VIEWER"
)

@Entity(tableName = "plans")
data class IptvPlan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val price: Double,
    val durationDays: Int
)

@Entity(tableName = "clients")
data class IptvClient(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phone: String,
    val provider: String, // "UniTV", "StarPlay", "Blinder", or custom values
    val planId: Int,
    val planName: String,
    val price: Double,
    val startDate: Long,
    val dueDate: Long,
    val active: Boolean = true,
    val notes: String = "",
    val username: String = "",
    val password: String = ""
)

@Entity(tableName = "transactions")
data class IptvTransaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clientName: String,
    val amount: Double,
    val date: Long,
    val type: String = "RECIBO", // "RECIBO" or "AVULSO"
    val server: String = "Manual"
)

@Entity(tableName = "notes")
data class IptvNote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val dateModified: Long = System.currentTimeMillis()
)

// --- DAOS ---

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsersFlow(): Flow<List<IptvUser>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: IptvUser): Long

    @Delete
    suspend fun deleteUser(user: IptvUser)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
}

@Dao
interface PlanDao {
    @Query("SELECT * FROM plans ORDER BY price DESC")
    fun getAllPlansFlow(): Flow<List<IptvPlan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: IptvPlan): Long

    @Delete
    suspend fun deletePlan(plan: IptvPlan)

    @Query("SELECT COUNT(*) FROM plans")
    suspend fun getPlanCount(): Int
}

@Dao
interface ClientDao {
    @Query("SELECT * FROM clients ORDER BY name ASC")
    fun getAllClientsFlow(): Flow<List<IptvClient>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: IptvClient): Long

    @Update
    suspend fun updateClient(client: IptvClient)

    @Delete
    suspend fun deleteClient(client: IptvClient)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactionsFlow(): Flow<List<IptvTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: IptvTransaction): Long

    @Delete
    suspend fun deleteTransaction(transaction: IptvTransaction)
}

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY dateModified DESC")
    fun getAllNotesFlow(): Flow<List<IptvNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: IptvNote): Long

    @Update
    suspend fun updateNote(note: IptvNote)

    @Delete
    suspend fun deleteNote(note: IptvNote)
}

// --- DATABASE ---

@Database(
    entities = [IptvUser::class, IptvPlan::class, IptvClient::class, IptvTransaction::class, IptvNote::class],
    version = 4,
    exportSchema = false
)
abstract class IptvDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun planDao(): PlanDao
    abstract fun clientDao(): ClientDao
    abstract fun transactionDao(): TransactionDao
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: IptvDatabase? = null

        fun getDatabase(context: Context): IptvDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    IptvDatabase::class.java,
                    "iptv_manager_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// --- REPOSITORY GROUP ---

class IptvRepository(private val db: IptvDatabase) {
    val users: Flow<List<IptvUser>> = db.userDao().getAllUsersFlow()
    val plans: Flow<List<IptvPlan>> = db.planDao().getAllPlansFlow()
    val clients: Flow<List<IptvClient>> = db.clientDao().getAllClientsFlow()
    val transactions: Flow<List<IptvTransaction>> = db.transactionDao().getAllTransactionsFlow()
    val notes: Flow<List<IptvNote>> = db.noteDao().getAllNotesFlow()

    suspend fun insertUser(user: IptvUser) = db.userDao().insertUser(user)
    suspend fun deleteUser(user: IptvUser) = db.userDao().deleteUser(user)

    suspend fun insertPlan(plan: IptvPlan) = db.planDao().insertPlan(plan)
    suspend fun deletePlan(plan: IptvPlan) = db.planDao().deletePlan(plan)

    suspend fun insertClient(client: IptvClient) = db.clientDao().insertClient(client)
    suspend fun updateClient(client: IptvClient) = db.clientDao().updateClient(client)
    suspend fun deleteClient(client: IptvClient) = db.clientDao().deleteClient(client)

    suspend fun insertTransaction(transaction: IptvTransaction) = db.transactionDao().insertTransaction(transaction)
    suspend fun deleteTransaction(transaction: IptvTransaction) = db.transactionDao().deleteTransaction(transaction)

    suspend fun insertNote(note: IptvNote) = db.noteDao().insertNote(note)
    suspend fun updateNote(note: IptvNote) = db.noteDao().updateNote(note)
    suspend fun deleteNote(note: IptvNote) = db.noteDao().deleteNote(note)

    suspend fun prepopulateIfEmpty() {
        if (db.userDao().getUserCount() == 0) {
            db.userDao().insertUser(IptvUser(username = "lemos_admin", pass = "1234", role = "ADMIN"))
            db.userDao().insertUser(IptvUser(username = "bruno_editor", pass = "1234", role = "EDITOR"))
            db.userDao().insertUser(IptvUser(username = "visita_viewer", pass = "1234", role = "VIEWER"))
        }
        if (db.planDao().getPlanCount() == 0) {
            db.planDao().insertPlan(IptvPlan(name = "Mensal SD + HD", price = 30.00, durationDays = 30))
            db.planDao().insertPlan(IptvPlan(name = "Trimestral Completo", price = 80.00, durationDays = 90))
            db.planDao().insertPlan(IptvPlan(name = "Anual Ultra 4K", price = 250.00, durationDays = 365))
        }
    }
}
