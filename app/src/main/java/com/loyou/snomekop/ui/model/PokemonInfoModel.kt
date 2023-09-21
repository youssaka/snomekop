package com.loyou.snomekop.ui.model

import com.loyou.snomekop.featurepokemon.domain.entities.PokemonInfo

data class PokemonInfoModel(
    val id: Int = 0,
    val name: String,
    val picture: String?,
    val height: Int,
    val weight: Int,
    val baseXp: Int,
    val types: List<String>,
    val hp: Int,
    val attack: Int,
    val defense: Int,
    val speed: Int,
    val specialAttack: Int,
    val specialDefense: Int
    ) {

    companion object {
        fun fromPokemonInfo(pokemonInfo: PokemonInfo): PokemonInfoModel {
            return PokemonInfoModel(
                name= pokemonInfo.name,
                picture= pokemonInfo.picture,
                height= pokemonInfo.height,
                weight= pokemonInfo.height,
                baseXp = pokemonInfo.baseXp,
                types= pokemonInfo.types,
                hp= pokemonInfo.hp,
                attack= pokemonInfo.attack,
                defense= pokemonInfo.defense,
                speed= pokemonInfo.speed,
                specialAttack= pokemonInfo.specialAttack,
                specialDefense= pokemonInfo.specialDefense
            )
        }
    }

}