package com.kote.obrio.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kote.obrio.ui.screens.PokemonListScreen
import com.kote.obrio.ui.screens.PokemonDetailsScreen
import com.kote.obrio.ui.viewmodels.PokemonViewModel

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun NaviGraph(
    navController: NavHostController,
    modifier: Modifier
) {

    val sharedViewModel = hiltViewModel<PokemonViewModel>()

    NavHost(
        navController = navController,
        startDestination = "pokemonListScreen"
    ) {
        composable(route = "pokemonListScreen") {
            PokemonListScreen(
                vm = sharedViewModel,
                onDetails = { pokemonId ->
                    sharedViewModel.selectPokemonAndLoad(pokemonId)
                    navController.navigate("pokemonDetailsScreen")
                }
            )
        }

        composable(route = "pokemonDetailsScreen") {
            PokemonDetailsScreen(
                viewModel = sharedViewModel,
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}