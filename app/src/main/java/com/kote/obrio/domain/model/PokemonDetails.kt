package com.kote.obrio.domain.model

data class PokemonDetails(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val sprites: Sprites,
    val stats: List<Stat>,
    val types: List<Type>,
    val abilities: List<Ability>
)

data class Stat(
    val name: String,
    val baseStat: Int
)

data class Type(
    val name: String,
    val slot: Int
)

data class Ability(
    val name: String,
    val isHidden: Boolean
)
