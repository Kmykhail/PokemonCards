package com.kote.obrio.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CatchingPokemon
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kote.obrio.domain.model.Pokemon
import com.kote.obrio.ui.common.PokemonImage
import com.kote.obrio.ui.common.PokemonTopBar
import com.kote.obrio.ui.common.ShowError
import com.kote.obrio.ui.common.ShowLoadingProgress
import com.kote.obrio.ui.viewmodels.PokemonViewModel
import timber.log.Timber

@Composable
fun PokemonListScreen(
    vm: PokemonViewModel,
    onDetails: (Int) -> Unit
) {
    val state by vm.uiState.collectAsState()

    Scaffold(
        topBar = {
            PokemonTopBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.CatchingPokemon, contentDescription = null)
                        Text("Pokémon")
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "favorites: ${state.favoritePokemonIds.size}",
                            modifier = Modifier.padding(end = 10.dp)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
            Timber.i("Loading state(1): ${state.isLoading}")
            ShowPokemonList(
                pokemonList = state.pokemonList,
                isFavoriteFun = {state.favoritePokemonIds.contains(it)},
                isPaginationLoading = state.isPaginationLoading,
                loadNext = vm::loadNext,
                loadImage = vm::loadImage,
                toggleFavorite = vm::toggleFavorites,
                deletePokemon = vm::deleteFromList,
                onDetails = onDetails
            )

            if (state.isLoading) {
                ShowLoadingProgress()
            }

            state.error?.let {
                ShowError(it)
            }
        }
    }
}

@Composable
fun ShowPokemonList(
    pokemonList: List<Pokemon>,
    isFavoriteFun: (Int) -> Boolean,
    isPaginationLoading: Boolean,
    loadNext: () -> Unit,
    loadImage: (Pokemon) -> Unit,
    toggleFavorite: (Int) -> Unit,
    deletePokemon: (Int) -> Unit,
    onDetails: (Int) -> Unit
) {
    Timber.i("Loading state(2): pagination: $isPaginationLoading")
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
    ) {
        itemsIndexed(
            items = pokemonList,
            key = { _, pokemon -> pokemon.id }
        ) { index, pokemon ->
            PokemonCard(
                pokemon = pokemon,
                isFavorite = isFavoriteFun(pokemon.id),
                loadImage = loadImage,
                toggleFavorite = {toggleFavorite(pokemon.id)},
                deletePokemon = {deletePokemon(pokemon.id)},
                onDetails = {onDetails(pokemon.id)}
            )
        }

        if (isPaginationLoading) {
            Timber.i("Pagination loading: True")
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }
        } else {
            Timber.i("Pagination loading: False")
        }
    }

    LaunchedEffect(listState, isPaginationLoading) {
        snapshotFlow { Pair(listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index, pokemonList.size) }
            .collect { (lastIndex, size) ->
                if (lastIndex != null && lastIndex >= size - 3 && !isPaginationLoading) {
                    loadNext()
                }
            }
    }
}

@Composable
private fun PokemonCard(
    pokemon: Pokemon,
    isFavorite: Boolean,
    loadImage: (Pokemon) -> Unit,
    toggleFavorite: () -> Unit,
    deletePokemon: () -> Unit,
    onDetails: () -> Unit
) {
    LaunchedEffect(pokemon) {
        if (pokemon.bmp == null) loadImage(pokemon)
    }

    Card(
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable(onClick = onDetails)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PokemonImage(pokemon.bmp)
                Column(modifier = Modifier.weight(1f)){
                    Text("${pokemon.id}")
                    Text(pokemon.name)
                }
                IconButton(onClick = toggleFavorite) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = deletePokemon) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = null
                    )
                }
            }
        }
    }
}
