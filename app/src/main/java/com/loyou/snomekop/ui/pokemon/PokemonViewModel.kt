package com.loyou.snomekop.ui.pokemon

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.loyou.snomekop.core.util.Resource
import com.loyou.snomekop.ui.model.PokemonInfoModel
import com.loyou.snomekop.featurepokemon.domain.use_case.GetPokemon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(private val getPokemon: GetPokemon): ViewModel() {

    private val _pokemonInfoModel = mutableStateOf<Resource<PokemonInfoModel>>(Resource.Loading())
    val pokemonInfoModel : State<Resource<PokemonInfoModel>>
    get() = _pokemonInfoModel

    suspend fun getPokemonDetails(pokemonName: String) {
        _pokemonInfoModel.value = Resource.Loading()
        when(val pokemonModel = getPokemon(pokemonName)) {
            is Resource.Success -> {
                _pokemonInfoModel.value = Resource.Success(PokemonInfoModel.fromPokemonInfo(pokemonModel.data))
            }

            is Resource.Error -> {
                _pokemonInfoModel.value = Resource.Error("Oups! Ce pokémon n'existe pas !")
            }

            else -> {
                _pokemonInfoModel.value = Resource.Error("Erreur inconue")
            }
        }
    }


}