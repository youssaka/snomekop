package com.loyou.snomekop.featurepokemon.data.repository

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.loyou.snomekop.featurepokemon.data.Mapper
import com.loyou.snomekop.ui.model.PokemonModel
import com.loyou.snomekop.featurepokemon.data.remote.PokedexApi
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class PokemonSource @Inject constructor(private val api: PokedexApi, private val mapper: Mapper) : PagingSource<Int, PokemonModel>() {

    override val keyReuseSupported: Boolean
        get() = true

    override fun getRefreshKey(state: PagingState<Int, PokemonModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(20)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(20)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PokemonModel> {
        return try {
            val offset = params.key ?: 0
            val limit = params.loadSize ?: 20
            val pokemonList = api.getPokemonList(offset = offset, limit = limit)
            var prevKey : Int? = null
            var nextKey : Int? = 20
            pokemonList.next?.also {
                nextKey = getOffsetFromUrl(it)
            }
            pokemonList.previous?.also {
                prevKey = getOffsetFromUrl(it)
            }

            LoadResult.Page(
                data = pokemonList.results.map { mapper.pokemonDtoToPokemonModel(it) },
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (ex : IOException) {
            LoadResult.Error(ex)
        } catch(ex: HttpException) {
            LoadResult.Error(ex)
        }
    }

    private fun getOffsetFromUrl(url: String): Int? {
        val regex = """(?<=offset=)\d+""".toRegex()
        val matchResult = regex.find(url)
        return matchResult?.value?.toInt()
    }
}