package com.loyou.snomekop.ui.list

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import androidx.palette.graphics.Palette
import com.loyou.snomekop.featurepokemon.data.Mapper
import com.loyou.snomekop.featurepokemon.data.PokemonRemoteMediator
import com.loyou.snomekop.featurepokemon.data.local.PokemonDatabase
import com.loyou.snomekop.featurepokemon.data.remote.PokedexApi
import com.loyou.snomekop.ui.model.PokemonModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val pokemonDatabase: PokemonDatabase,
    private val pokedexApi: PokedexApi,
    val mapper: Mapper
) : ViewModel() {


    val isSearching = mutableStateOf(false)

    val shouldDisplaySearchResults = mutableStateOf(true)

    val pokemonList: StateFlow<PagingData<PokemonModel>>
        get() {
            return _pokemonList
        }

    val pokemonSearchResult = mutableStateOf<List<PokemonModel>>(listOf())

    private val _pokemonList : MutableStateFlow<PagingData<PokemonModel>> = MutableStateFlow(PagingData.empty())

    @OptIn(ExperimentalPagingApi::class)
    fun getPokemons() {
        shouldDisplaySearchResults.value = false
        Pager(
            config = PagingConfig(pageSize = 20, initialLoadSize = 40),
            remoteMediator = PokemonRemoteMediator(pokedexApi, pokemonDatabase, mapper )
        ) {
            pokemonDatabase.pokemonDao.allPokemons()
        }.flow.cachedIn(viewModelScope).map { pagingData ->
            pagingData.map { pokemonEntity ->
                mapper.pokemonEntityToPokemonModel(pokemonEntity)
            }
        }.onEach {
            _pokemonList.value = it
        }.shareIn(viewModelScope, SharingStarted.Eagerly)
    }

    fun getDominantColors(drawable: Drawable, onFinish: (Pair<Color, Color>) -> Unit) {
        viewModelScope.launch {
            val dominantColorInt = withContext(Dispatchers.IO) {
                val bitmap = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)
                val palette = Palette.from(bitmap).generate()
                val swatches = palette.swatches
                val mutableSwatches = swatches.toMutableList()
                mutableSwatches.sortByDescending { it.population }
                val mostDominantSwatch = mutableSwatches.getOrNull(0)
                val secondMostDominantSwatch = mutableSwatches.getOrNull(1)
                val mostDominantColor: Color? = mostDominantSwatch?.rgb?.let { Color(it) }
                val secondMostDominantColor: Color? = secondMostDominantSwatch?.rgb?.let { Color(it) }
                return@withContext Pair(mostDominantColor!!, secondMostDominantColor!!)
            }
            onFinish(dominantColorInt)
        }
    }

    fun getPokemonsByName(query: String) {
        shouldDisplaySearchResults.value = true
        isSearching.value = true
/*        if(query.isBlank()) {
            pokemonSearchResult.value = listOf()
            isSearching.value = false
            return
        }*/
        viewModelScope.launch {
            val allPokemons = pokedexApi.getPokemonList(offset = 0, limit = PokedexApi.POKEMON_NUMBER)
            val result = allPokemons.results.filter {
                it.name.contains(query)
            }
            pokemonSearchResult.value = result.map {
                mapper.pokemonDtoToPokemonModel(it)
            }
            isSearching.value = false
        }
    }

}