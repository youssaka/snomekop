package com.loyou.snomekop.featurepokemon.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemons")
data class PokemonEntity(
    val name: String,
    val picture: String?,
    val height: Int,
    val weight: Int,
    @ColumnInfo(name = "base_xp")
    val baseXp: Int,
    val types: List<String>,
    val hp: Int,
    val attack: Int,
    val defense: Int,
    val speed: Int,
    @ColumnInfo(name = "special_attack")
    val specialAttack: Int,
    @ColumnInfo(name = "special_defense")
    val specialDefense: Int,
    @PrimaryKey val id: Int = 0,
    @ColumnInfo(name = "created_at")
    var createdAt: Long = 0,
)
