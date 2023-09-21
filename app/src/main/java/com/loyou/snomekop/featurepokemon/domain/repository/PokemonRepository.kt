package com.loyou.snomekop.featurepokemon.domain.repository

import androidx.paging.PagingData
import com.loyou.snomekop.core.util.Resource
import com.loyou.snomekop.featurepokemon.domain.entities.PokemonInfo
import com.loyou.snomekop.featurepokemon.domain.entities.PokemonItem
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {

    suspend fun getPokemons(): Flow<PagingData<PokemonItem>>

    suspend fun getPokemonsByName(name: String): Resource<List<PokemonItem>>

    suspend fun getPokemon(name: String): Resource<PokemonInfo>

}