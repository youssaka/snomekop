package com.loyou.snomekop.featurepokemon.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.loyou.snomekop.featurepokemon.data.local.PokemonDatabase
import com.loyou.snomekop.featurepokemon.data.local.entity.PokemonEntity
import com.loyou.snomekop.featurepokemon.data.local.entity.RemoteKeys
import com.loyou.snomekop.featurepokemon.data.remote.PokedexApi
import com.loyou.snomekop.featurepokemon.data.remote.dto.Result
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPagingApi::class)
class PokemonRemoteMediator constructor (
    private val pokedexApi: PokedexApi,
    private val pokemonDatabase: PokemonDatabase,
    private val mapper: Mapper,
) : RemoteMediator<Int, PokemonEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PokemonEntity>
    ): MediatorResult {

        var endOfPaginationReached = false


        Log.d("DBG", "Load type = $loadType")

        if (loadType == LoadType.PREPEND) {
            endOfPaginationReached = true
        }

        if (loadType == LoadType.REFRESH) {
            val results = pokedexApi.getPokemonList(offset = 0, limit = 40).results
            insertPokemonsAndRemoteKeysInDb(
                pokemonList = results,
                nextKey = 40,
                previousKey = null
            )
        }

        if (loadType == LoadType.APPEND) {
            val id = state.lastItemOrNull()?.id
            if(id != null) {
                val rk = pokemonDatabase.remoteKeysDao.remoteKeysRepoId(id)
                val requestNextKey = rk?.nextKey
                if(requestNextKey != null) {
                    val pokemonListResult = pokedexApi.getPokemonList(offset = requestNextKey, limit = PokedexApi.LIMIT)
                    val nextKey = pokemonListResult.next?.let { PokedexApi.extractOffset(it) }
                    val previousKey = pokemonListResult.previous?.let { PokedexApi.extractOffset(it) }
                    val pokemonList = pokemonListResult.results
                    insertPokemonsAndRemoteKeysInDb(
                        pokemonList = pokemonList,
                        nextKey = nextKey,
                        previousKey = previousKey
                    )
                } else {
                    endOfPaginationReached = true
                }
            }
        }

        return MediatorResult.Success(
            endOfPaginationReached = endOfPaginationReached
        )

    }

    private suspend fun insertPokemonsAndRemoteKeysInDb(
        pokemonList: List<Result>,
        nextKey: Int?,
        previousKey: Int?
    ) {
        val pokemonsDeffered = mutableListOf<Deferred<PokemonEntity>>()
        pokemonList.forEach { result ->
            withContext(Dispatchers.IO) {
                val pokemonEntity = async {
                    val pokemonInfo = pokedexApi.getPokemonInfo(result.name)
                    mapper.pokemonInfoDtoToPokemonEntity(pokemonInfo)
                }
                pokemonsDeffered.add(pokemonEntity)
            }
        }
        val pokemons = pokemonsDeffered.awaitAll()
        val remoteKeys = mutableListOf<RemoteKeys>()
        pokemons.forEach {
            remoteKeys.add(
                RemoteKeys(
                    pokemonId = it.id,
                    prevKey = previousKey,
                    nextKey = nextKey
                )
            )
        }
        pokemonDatabase.withTransaction {
            pokemonDatabase.pokemonDao.insertWithTimestamp(pokemons)
            pokemonDatabase.remoteKeysDao.insertAll(remoteKeys)
        }
    }

    override suspend fun initialize(): InitializeAction {
        val shouldRefreshData = kotlin.run {
            val cacheTimeout = TimeUnit.MILLISECONDS.convert(1L, TimeUnit.HOURS)
            val lastUpdateTime: Long = pokemonDatabase.pokemonDao.getLastUpdateTime() ?: 0L
            (System.currentTimeMillis() - lastUpdateTime) >= cacheTimeout
        }
        return if (shouldRefreshData)
        {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            InitializeAction.SKIP_INITIAL_REFRESH
        }
    }

}