package com.loyou.snomekop.featurepokemon.data

import com.loyou.snomekop.featurepokemon.data.local.entity.PokemonEntity
import com.loyou.snomekop.ui.model.PokemonModel
import com.loyou.snomekop.featurepokemon.data.remote.dto.Pokemon
import com.loyou.snomekop.featurepokemon.data.remote.dto.Result
import com.loyou.snomekop.featurepokemon.data.remote.dto.Type
import com.loyou.snomekop.featurepokemon.domain.entities.PokemonInfo

class Mapper {
    fun pokemonDtoToPokemonModel(pokemonDto: Result): PokemonModel {
        return PokemonModel(name = pokemonDto.name, url = pokemonDto.url)
    }

    fun pokemonInfoDtoToPokemonInfo(pokemonDto: Pokemon): PokemonInfo {
        return PokemonInfo(
            name = pokemonDto.name,
            picture = pokemonDto.sprites.frontDefault,
            height = pokemonDto.height,
            weight = pokemonDto.weight,
            baseXp = pokemonDto.baseExperience,
            types = pokemonDto.types.toListOfStrings(),
            hp = pokemonDto.stats[0].baseStat,
            attack = pokemonDto.stats[1].baseStat,
            defense = pokemonDto.stats[2].baseStat,
            speed = pokemonDto.stats[5].baseStat,
            specialAttack = pokemonDto.stats[3].baseStat,
            specialDefense = pokemonDto.stats[4].baseStat,
        )
    }

    fun pokemonInfoDtoToPokemonEntity(pokemonDto: Pokemon): PokemonEntity {
        return PokemonEntity(
            name = pokemonDto.name,
            picture = pokemonDto.sprites.frontDefault,
            height = pokemonDto.height,
            weight = pokemonDto.weight,
            baseXp = pokemonDto.baseExperience,
            types = pokemonDto.types.toListOfStrings(),
            hp = pokemonDto.stats[0].baseStat,
            attack = pokemonDto.stats[1].baseStat,
            defense = pokemonDto.stats[2].baseStat,
            speed = pokemonDto.stats[5].baseStat,
            specialAttack = pokemonDto.stats[3].baseStat,
            specialDefense = pokemonDto.stats[4].baseStat,
            id = pokemonDto.id
        )
    }

    fun pokemonEntityToPokemonInfo(pokemonEntity: PokemonEntity): PokemonInfo{
        return PokemonInfo(
            id = pokemonEntity.id,
            name = pokemonEntity.name,
            picture = pokemonEntity.picture,
            height = pokemonEntity.height,
            weight = pokemonEntity.weight,
            baseXp = pokemonEntity.baseXp,
            types = pokemonEntity.types,
            hp = pokemonEntity.hp,
            attack = pokemonEntity.attack,
            defense = pokemonEntity.defense,
            speed = pokemonEntity.speed,
            specialAttack = pokemonEntity.specialAttack,
            specialDefense = pokemonEntity.specialDefense,
        )
    }

    fun pokemonEntityToPokemonModel(pokemonEntity: PokemonEntity): PokemonModel {
        return PokemonModel(
            id = pokemonEntity.id,
            name = pokemonEntity.name,
        )
    }

    private fun List<Type>.toListOfStrings(): List<String> {
        return this.map { it.type.name }
    }
}