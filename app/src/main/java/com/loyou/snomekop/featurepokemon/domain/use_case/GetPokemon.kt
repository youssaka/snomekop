package com.loyou.snomekop.featurepokemon.domain.use_case

import com.loyou.snomekop.core.util.Resource
import com.loyou.snomekop.featurepokemon.domain.entities.PokemonInfo
import com.loyou.snomekop.featurepokemon.domain.repository.PokemonRepository

class GetPokemon (
    private val repository: PokemonRepository
        ) {

    suspend operator fun invoke(pokemon: String) : Resource<PokemonInfo> {
        return repository.getPokemon(pokemon)
    }

}