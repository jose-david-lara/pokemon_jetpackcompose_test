package com.chelo.pokemon.core.di

import android.content.Context
import android.content.SharedPreferences
import com.chelo.pokemon.core.store.EncryptedPreferences
import com.chelo.pokemon.feature.home.data.repository.PokemonRepositoryImpl
import com.chelo.pokemon.feature.home.data.source.PokemonApiService
import com.chelo.pokemon.feature.home.data.source.PokemonLocalDataSource
import com.chelo.pokemon.feature.home.domain.repository.PokemonRepository
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {




    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun providePokemonApiService(retrofit: Retrofit): PokemonApiService {
        return retrofit.create(PokemonApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences =
        context.getSharedPreferences("pokemon_prefs", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun providePokemonLocalDataSource(
        gson: Gson,
        @ApplicationContext context: Context,
    ): PokemonLocalDataSource {
        return PokemonLocalDataSource(
            gson = gson,
            context = context
        )
    }

    @Provides
    @Singleton
    fun providePokemonRepository(
        apiService: PokemonApiService,
        pokemonLocalDataSource: PokemonLocalDataSource
    ): PokemonRepository {
        return PokemonRepositoryImpl(apiService, pokemonLocalDataSource)
    }
}