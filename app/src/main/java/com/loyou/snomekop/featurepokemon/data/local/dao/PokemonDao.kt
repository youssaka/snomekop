package com.loyou.snomekop.featurepokemon.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import androidx.room.OnConflictStrategy.Companion.REPLACE
import com.loyou.snomekop.featurepokemon.data.local.entity.PokemonEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PokemonDao {

@Insert(onConflict = REPLACE)
abstract suspend fun insertPokemonInfos(pokemonInfos: List<PokemonEntity>)

@Query("SELECT * FROM pokemons WHERE name LIKE '%' || :name || '%'")
abstract fun pokemonsByName(name: String): Flow<List<PokemonEntity>>

@Query("SELECT * FROM pokemons WHERE name = :name")
abstract fun pokemonByName(name: String): PokemonEntity?

@Query("SELECT * FROM pokemons")
abstract fun allPokemons(): PagingSource<Int, PokemonEntity>

@Query("DELETE FROM pokemons")
abstract suspend fun clearPokemons()

@Query("SELECT created_at FROM pokemons ORDER BY created_at DESC LIMIT 1")
abstract suspend fun getLastUpdateTime() : Long?

suspend fun insertWithTimestamp(pokemonEntity: List<PokemonEntity>) {
    val timeStampedSongs = pokemonEntity.map {
        it.apply {
            createdAt = System.currentTimeMillis()
        }
    }
    insertPokemonInfos(timeStampedSongs)
}

@Query("DELETE FROM pokemons")
abstract suspend fun clearAll()

@Transaction
open suspend fun clearAndInsert(songEntities: List<PokemonEntity>) {
    clearAll()
    insertWithTimestamp(songEntities)
}

}