package com.kote.obrio.domain

import com.kote.obrio.domain.model.Pokemon
import com.kote.obrio.domain.model.PokemonDetails

interface PokemonRepository {
    suspend fun getPokemonList(offset: Int, limit: Int): Result<List<Pokemon>>
    suspend fun getPokemonDetails(name: String): Result<PokemonDetails>
}