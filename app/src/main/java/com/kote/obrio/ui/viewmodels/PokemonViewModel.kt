package com.kote.obrio.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.kote.obrio.domain.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import com.kote.obrio.domain.ImageRepository
import com.kote.obrio.domain.model.Pokemon
import com.kote.obrio.domain.model.PokemonDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UiState(
    val pokemonList: List<Pokemon> = emptyList(),
    val favoritePokemonIds: Set<Int> = emptySet(),
    val isLoading: Boolean = false,
    val isPaginationLoading: Boolean = false,
    val error: String? = null,
    val selectedPokemon: PokemonDetails? = null,
    val detailsLoading: Boolean = false,
    val detailsError: String? = null
)

@HiltViewModel
class PokemonViewModel @Inject constructor(
    private val remoteRepository: PokemonRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState(isLoading = false))
    val uiState = _uiState.asStateFlow()

    private var offset = 0
    private var limit = 20

    init {
        loadNext()
    }

    fun loadNext() {
        if (_uiState.value.isLoading || _uiState.value.isPaginationLoading) return

        if (offset == 0) {
            _uiState.update { it.copy(isLoading = true, error = null) }
        } else {
            _uiState.update { it.copy(isPaginationLoading = true, error = null) }
        }

        viewModelScope.launch {
            try {
                val result = remoteRepository.getPokemonList(offset, limit)
                if (result.isSuccess) {
                    val receivedPokemonList = result.getOrDefault(emptyList())
                    _uiState.update { currentState ->
                        currentState.copy(
                            pokemonList = currentState.pokemonList + receivedPokemonList,
                            isLoading = false,
                            isPaginationLoading = false
                        )
                    }
                    offset += limit
                } else {
                    _uiState.update { it.copy(isLoading = false, isPaginationLoading = false, error = "Failed to load") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, isPaginationLoading = false, error = e.toString()) }
            }
        }
    }

    fun loadImage(pokemon: Pokemon) {
        if (pokemon.bmp != null) return

        viewModelScope.launch {
            val bmp = imageRepository.downloadBitmap(url = pokemon.imageUrl)
            bmp?.let { bitmap ->
                _uiState.update { currentState ->
                    val updatedList = currentState.pokemonList.map {
                        if (it.id == pokemon.id) it.copy(bmp = bitmap) else it
                    }
                    currentState.copy(pokemonList = updatedList)
                }
            }
        }
    }

    fun toggleFavorites(id: Int) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                val updatedFavorites = currentState.favoritePokemonIds.toMutableSet().apply {
                    if (contains(id)) remove(id) else add(id)
                }
                currentState.copy(favoritePokemonIds = updatedFavorites)
            }
        }
    }

    fun deleteFromList(id: Int) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                val updatePokemonList = currentState.pokemonList.filterNot { it.id == id }
                if (currentState.favoritePokemonIds.contains(id)) {
                    val updatedFavoriteIds = currentState.favoritePokemonIds.toMutableSet().apply { remove(id) }
                    currentState.copy(pokemonList = updatePokemonList, favoritePokemonIds = updatedFavoriteIds)
                } else {
                    currentState.copy(pokemonList = updatePokemonList)
                }
            }
        }
    }

    fun loadPokemonDetails(pokemonId: Int) {
        _uiState.update { it.copy(detailsLoading = true, detailsError = null, selectedPokemon = null) }

        viewModelScope.launch {
            try {
                val result = remoteRepository.getPokemonDetails(pokemonId.toString())
                if (result.isSuccess) {
                    val original = result.getOrNull()
                    val updatedDetails = original?.let { details ->
                        if (details.sprites.bmp == null && details.sprites.frontDefault != null) {
                            val bitmap = imageRepository.downloadBitmap(details.sprites.frontDefault, addToCacheIfNotExist = false)
                            if (bitmap != null) {
                                details.copy(sprites = details.sprites.copy(bmp = bitmap))
                            } else details
                        } else details
                    }

                    _uiState.update { currentState ->
                        currentState.copy(
                            selectedPokemon = updatedDetails,
                            detailsLoading = false
                        )
                    }
                } else {
                    _uiState.update { it.copy(detailsLoading = false, detailsError = "Failed to load Pokemon details") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(detailsLoading = false, detailsError = e.toString()) }
            }
        }
    }

    fun selectPokemonAndLoad(pokemonId: Int) {
        loadPokemonDetails(pokemonId)
    }
}
