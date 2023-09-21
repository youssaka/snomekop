package com.loyou.snomekop.featurepokemon.data.remote

import com.loyou.snomekop.featurepokemon.data.remote.dto.Pokemon
import com.loyou.snomekop.featurepokemon.data.remote.dto.PokemonList
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokedexApi {

    @GET("pokemon/{pokemon}/")
    suspend fun getPokemonInfo(@Path("pokemon") pokemon : String) : Pokemon

    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): PokemonList

    companion object {
        const val BASE_URL = "https://pokeapi.co/api/v2/"

        fun extractOffset(url: String): Int? {
            val offsetPattern = Regex("offset=(\\d+)")
            val match = offsetPattern.find(url)
            return match?.groupValues?.get(1)?.toIntOrNull()
        }

        const val LIMIT = 20

        const val POKEMON_NUMBER = 1281
    }

}