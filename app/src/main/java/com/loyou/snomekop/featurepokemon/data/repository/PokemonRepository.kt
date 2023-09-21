package com.loyou.snomekop.featurepokemon.data.repository

import android.util.Log
import androidx.paging.PagingData
import com.loyou.snomekop.core.util.Resource
import com.loyou.snomekop.featurepokemon.data.Mapper
import com.loyou.snomekop.featurepokemon.data.local.PokemonDatabase
import com.loyou.snomekop.featurepokemon.data.local.dao.PokemonDao
import com.loyou.snomekop.featurepokemon.data.remote.PokedexApi
import com.loyou.snomekop.featurepokemon.domain.entities.PokemonInfo
import com.loyou.snomekop.featurepokemon.domain.entities.PokemonItem
import com.loyou.snomekop.featurepokemon.domain.repository.PokemonRepository
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject


@ActivityScoped
class PokemonRepositoryImpl @Inject constructor(
    private val api: PokedexApi,
    private val database: PokemonDatabase,
    private val mapper: Mapper
) : PokemonRepository {
    override suspend fun getPokemons(): Flow<PagingData<PokemonItem>> {
        TODO("Not yet implemented")
    }

    override suspend fun getPokemonsByName(name: String): Resource<List<PokemonItem>> {
        TODO("Not yet implemented")
    }

    override suspend fun getPokemon(name: String): Resource<PokemonInfo> {
        val response = try {
            withContext(Dispatchers.IO) {
                database.pokemonDao.pokemonByName(name)
            }
        } catch(e: Exception) {
            Log.e("DBG", "Error = ${e.message}\n\n${e.stackTraceToString()}")
            return Resource.Error("An unknown error occured.")
        }
        return if(response == null) {
            try {
                val apiResponse = api.getPokemonInfo(name)
                Resource.Success(mapper.pokemonInfoDtoToPokemonInfo(apiResponse))
            } catch(e: Exception) {
                Resource.Error("An unknown error occured.")
            }
        } else {
            Log.d("DBG", "response = ${response}")
            Resource.Success(mapper.pokemonEntityToPokemonInfo(response))
        }
    }

}