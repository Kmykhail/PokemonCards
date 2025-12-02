package com.kote.obrio.data.mapper

import com.kote.obrio.data.model.PokemonBasicDto
import com.kote.obrio.data.model.PokemonDto
import com.kote.obrio.data.model.SpritesDto
import com.kote.obrio.data.model.StatDto
import com.kote.obrio.data.model.TypeDto
import com.kote.obrio.data.model.AbilityDto
import com.kote.obrio.domain.model.Pokemon
import com.kote.obrio.domain.model.PokemonDetails
import com.kote.obrio.domain.model.Sprites
import com.kote.obrio.domain.model.Stat
import com.kote.obrio.domain.model.Type
import com.kote.obrio.domain.model.Ability

fun PokemonBasicDto.toDomain(): Pokemon {
    return Pokemon(
        id = this.getId(),
        name = this.name,
        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${this.getId()}.png"
    )
}

fun PokemonDto.toDomain(): PokemonDetails {
    return PokemonDetails(
        id = this.id,
        name = this.name,
        height = this.height,
        weight = this.weight,
        sprites = this.sprites.toDomain(),
        stats = this.stats.map { it.toDomain() },
        types = this.types.map { it.toDomain() },
        abilities = this.abilities.map { it.toDomain() }
    )
}

fun SpritesDto.toDomain(): Sprites {
    return Sprites(
        frontDefault = this.frontDefault
    )
}

fun StatDto.toDomain(): Stat {
    return Stat(
        name = this.stat.name,
        baseStat = this.baseStat
    )
}

fun TypeDto.toDomain(): Type {
    return Type(
        name = this.type.name,
        slot = this.slot
    )
}

fun AbilityDto.toDomain(): Ability {
    return Ability(
        name = this.ability.name,
        isHidden = this.isHidden
    )
}
