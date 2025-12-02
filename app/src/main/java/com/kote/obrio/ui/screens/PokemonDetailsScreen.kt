package com.kote.obrio.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kote.obrio.domain.model.PokemonDetails
import com.kote.obrio.domain.model.Stat
import com.kote.obrio.domain.model.Type
import com.kote.obrio.domain.model.Ability
import com.kote.obrio.ui.common.PokemonImage
import com.kote.obrio.ui.common.PokemonTopBar
import com.kote.obrio.ui.common.ShowError
import com.kote.obrio.ui.common.ShowLoadingProgress
import com.kote.obrio.ui.viewmodels.PokemonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailsScreen(
    viewModel: PokemonViewModel,
    navigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isFavorite = uiState.selectedPokemon?.id?.let { uiState.favoritePokemonIds.contains(it) } == true

    Scaffold(
        topBar = {
            PokemonTopBar(
                secondaryScreen = true,
                title = {
                    uiState.selectedPokemon?.let { pokemon ->
                        Text(text = pokemon.name.capitalize(Locale.current))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            ) {
                uiState.selectedPokemon?.let { pokemon ->
                    IconButton(onClick = { viewModel.toggleFavorites(pokemon.id) }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when {
                uiState.detailsLoading -> ShowLoadingProgress()
                uiState.detailsError != null -> ShowError(uiState.detailsError!!)
                uiState.selectedPokemon != null -> {
                    PokemonDetailsContent(
                        pokemonDetails = uiState.selectedPokemon!!,
                    )
                }
            }
        }
    }
}

@Composable
private fun PokemonDetailsContent(
    pokemonDetails: PokemonDetails,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        PokemonDetailImage(
            bmp = pokemonDetails.sprites.bmp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "#${pokemonDetails.id.toString().padStart(3, '0')}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        PokemonTypes(types = pokemonDetails.types)

        Spacer(modifier = Modifier.height(24.dp))

        BasicInfoCard(height = pokemonDetails.height, weight = pokemonDetails.weight)

        Spacer(modifier = Modifier.height(24.dp))

        StatsSection(stats = pokemonDetails.stats)

        Spacer(modifier = Modifier.height(24.dp))

        AbilitiesSection(abilities = pokemonDetails.abilities)

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun PokemonDetailImage(
    bmp: Bitmap?
) {
    Box(
        modifier = Modifier
            .size(250.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        PokemonImage(bmp)
    }
}

@Composable
private fun PokemonTypes(types: List<Type>) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        modifier = Modifier.fillMaxWidth()
    ) {
        types.sortedBy { it.slot }.forEach { type ->
            TypeBadge(typeName = type.name)
        }
    }
}

@Composable
private fun TypeBadge(typeName: String) {
    val typeColor = getTypeColor(typeName)
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(typeColor)
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(
            text = typeName.capitalize(Locale.current),
            color = Color.White,
            style = MaterialTheme.typography.labelLarge,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun BasicInfoCard(height: Int, weight: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            InfoItem(label = "Height", value = "${height / 10.0} m")
            InfoItem(label = "Weight", value = "${weight / 10.0} kg")
        }
    }
}

@Composable
private fun InfoItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun StatsSection(stats: List<Stat>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Base Stats",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            stats.forEach { stat ->
                StatBar(stat = stat)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun StatBar(stat: Stat) {
    val maxStat = 255f
    val progress = (stat.baseStat / maxStat).coerceIn(0f, 1f)
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatStatName(stat.name),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = stat.baseStat.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = getStatColor(stat.baseStat),
        )
    }
}

@Composable
private fun AbilitiesSection(abilities: List<Ability>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Abilities",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            abilities.forEach { ability ->
                AbilityItem(ability = ability)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun AbilityItem(ability: Ability) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "•",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = ability.name.replace("-", " ").capitalize(Locale.current),
            style = MaterialTheme.typography.bodyLarge
        )
        if (ability.isHidden) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "(Hidden)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

private fun formatStatName(name: String): String {
    return when (name) {
        "hp" -> "HP"
        "attack" -> "Attack"
        "defense" -> "Defense"
        "special-attack" -> "Sp. Atk"
        "special-defense" -> "Sp. Def"
        "speed" -> "Speed"
        else -> name.capitalize(Locale.current)
    }
}

private fun getStatColor(value: Int): Color {
    return when {
        value >= 150 -> Color.Green
        value >= 100 -> Color.Blue
        value >= 50 -> Color.Yellow
        else -> Color.Red
    }
}

private fun getTypeColor(typeName: String): Color {
    return when (typeName.lowercase()) {
        "normal" -> Color(0xFFA8A878)
        "fire" -> Color(0xFFF08030)
        "water" -> Color(0xFF6890F0)
        "electric" -> Color(0xFFF8D030)
        "grass" -> Color(0xFF78C850)
        "ice" -> Color(0xFF98D8D8)
        "fighting" -> Color(0xFFC03028)
        "poison" -> Color(0xFFA040A0)
        "ground" -> Color(0xFFE0C068)
        "flying" -> Color(0xFFA890F0)
        "psychic" -> Color(0xFFF85888)
        "bug" -> Color(0xFFA8B820)
        "rock" -> Color(0xFFB8A038)
        "ghost" -> Color(0xFF705898)
        "dragon" -> Color(0xFF7038F8)
        "dark" -> Color(0xFF705848)
        "steel" -> Color(0xFFB8B8D0)
        "fairy" -> Color(0xFFEE99AC)
        else -> Color(0xFF68A090)
    }
}