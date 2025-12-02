package com.kote.obrio.data.model

import androidx.compose.runtime.Stable
import com.google.gson.annotations.SerializedName

data class PokemonListResponseDto(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonBasicDto>
)

@Stable
data class PokemonBasicDto(
    val name: String,
    val url: String,
    val isDeleted: Boolean = false
) {
    fun getId(): Int { // imageId
        return url.split("/").dropLast(1).last().toInt()
    }
}

@Stable
data class PokemonDto(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val sprites: SpritesDto,
    val stats: List<StatDto>,
    val types: List<TypeDto>,
    val abilities: List<AbilityDto>
)

@Stable
data class SpritesDto(
    @SerializedName("front_default")  val frontDefault: String?
)

@Stable
data class StatDto(
    @SerializedName("base_stat") val baseStat: Int,
    val stat: StatNameDto
)

@Stable
data class StatNameDto(
    val name: String
)

@Stable
data class TypeDto(
    val slot: Int,
    val type: TypeNameDto
)

@Stable
data class TypeNameDto(
    val name: String
)

@Stable
data class AbilityDto(
    val ability: AbilityNameDto,
    @SerializedName("is_hidden") val isHidden: Boolean
)

@Stable
data class AbilityNameDto(
    val name: String
)
