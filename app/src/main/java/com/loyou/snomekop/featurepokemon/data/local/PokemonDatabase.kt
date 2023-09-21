package com.loyou.snomekop.featurepokemon.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.loyou.snomekop.featurepokemon.data.local.dao.PokemonDao
import com.loyou.snomekop.featurepokemon.data.local.dao.RemoteKeysDao
import com.loyou.snomekop.featurepokemon.data.local.entity.PokemonEntity
import com.loyou.snomekop.featurepokemon.data.local.entity.RemoteKeys

@Database(
    version = 1,
    entities = [PokemonEntity::class, RemoteKeys::class]
)
@TypeConverters(Converters::class)
abstract class PokemonDatabase : RoomDatabase() {
    abstract val pokemonDao: PokemonDao
    abstract val remoteKeysDao: RemoteKeysDao

    companion object {

        @Volatile
        private var INSTANCE: PokemonDatabase? = null

        fun getInstance(context: Context): PokemonDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                PokemonDatabase::class.java, "snomekop.db")
                .fallbackToDestructiveMigration()
                .build()
    }
}