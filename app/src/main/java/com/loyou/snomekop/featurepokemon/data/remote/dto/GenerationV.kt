package com.loyou.snomekop.featurepokemon.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GenerationV(
    @SerializedName("black-white")
    val blackWhite: BlackWhite
)