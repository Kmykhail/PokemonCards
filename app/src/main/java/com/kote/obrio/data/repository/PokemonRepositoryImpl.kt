package com.kote.obrio.data.repository

import com.kote.obrio.data.api.PokemonApiService
import com.kote.obrio.data.mapper.toDomain
import com.kote.obrio.domain.PokemonRepository
import com.kote.obrio.domain.model.Pokemon
import com.kote.obrio.domain.model.PokemonDetails
import javax.inject.Inject

class PokemonRepositoryImp @Inject constructor(
    private val pokemonApiService: PokemonApiService,
) : PokemonRepository {
    override suspend fun getPokemonList(offset: Int, limit: Int): Result<List<Pokemon>> {
        return try {
            val response = pokemonApiService.getPokemonList(offset, limit)
            Result.success(
                response.results.map { it.toDomain() }
            )
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    override suspend fun getPokemonDetails(name: String): Result<PokemonDetails> {
        return try {
            val response = pokemonApiService.getPokemonDetails(name)
            Result.success(response.toDomain())
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }
}