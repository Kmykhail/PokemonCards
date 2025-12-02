package com.kote.obrio.di

import com.kote.obrio.data.api.PokemonApiService
import com.kote.obrio.data.repository.ImageRepositoryImpl
import com.kote.obrio.data.repository.PokemonRepositoryImp
import com.kote.obrio.domain.ImageRepository
import com.kote.obrio.domain.PokemonRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private const val BASE_URL = "https://pokeapi.co/api/"

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun providePokemonApiService(retrofit: Retrofit): PokemonApiService {
        return retrofit.create(PokemonApiService::class.java)
    }

    @Provides
    @Singleton
    fun providePokemonRepository(pokemonApiService: PokemonApiService) : PokemonRepository {
        return PokemonRepositoryImp(pokemonApiService)
    }

    @Provides
    @Singleton
    fun provideImageRepository(): ImageRepository {
        return ImageRepositoryImpl()
    }
}