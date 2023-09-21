package com.loyou.snomekop.featurepokemon.data.remote.dto

import com.google.gson.annotations.SerializedName

data class Species(
    @SerializedName("name")
    val name: String,
    @SerializedName("url")
    val url: String
)