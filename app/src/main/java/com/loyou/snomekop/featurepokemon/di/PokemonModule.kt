package com.loyou.snomekop.featurepokemon.di

import android.app.Application
import com.loyou.snomekop.BuildConfig
import com.loyou.snomekop.featurepokemon.data.Mapper
import com.loyou.snomekop.featurepokemon.data.local.PokemonDatabase
import com.loyou.snomekop.featurepokemon.data.local.dao.PokemonDao
import com.loyou.snomekop.featurepokemon.data.remote.PokedexApi
import com.loyou.snomekop.featurepokemon.data.repository.PokemonRepositoryImpl
import com.loyou.snomekop.featurepokemon.domain.repository.PokemonRepository
import com.loyou.snomekop.featurepokemon.domain.use_case.GetPokemon
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PokemonModule {

    @Provides
    @Singleton
    fun providePokemonRepository(api: PokedexApi, database: PokemonDatabase, mapper: Mapper) : PokemonRepository {
        return PokemonRepositoryImpl(api, database, mapper)
    }

    @Provides
    @Singleton
    fun providePokemonDatabase(app: Application): PokemonDatabase {
        return PokemonDatabase.getInstance(app)
    }

    @Provides
    @Singleton
    fun providePokemonApi(client: OkHttpClient): PokedexApi {

        return Retrofit.Builder()
            .baseUrl(PokedexApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(PokedexApi::class.java)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val client = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            client
                .addInterceptor(HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BASIC))

        }
        return client.build()
    }

    @Provides
    @Singleton
    fun provideGetPokemonUseCase(repository: PokemonRepository): GetPokemon {
        return GetPokemon(repository)
    }

    @Provides
    @Singleton
    fun provideMapper(): Mapper {
        return Mapper()
    }

}