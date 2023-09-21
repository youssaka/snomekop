package com.loyou.snomekop.featurepokemon.domain.use_case

import androidx.paging.PagingData
import com.loyou.snomekop.core.util.Resource
import com.loyou.snomekop.featurepokemon.domain.entities.PokemonItem
import com.loyou.snomekop.featurepokemon.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow

class GetPokemons (
    private val repository: PokemonRepository
)
{
    suspend operator fun invoke() : Flow<PagingData<PokemonItem>> {
        return repository.getPokemons()
    }

}