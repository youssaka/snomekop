package com.loyou.snomekop.featurepokemon.domain.entities

data class PokemonInfo(
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
)