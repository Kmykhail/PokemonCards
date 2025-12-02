package com.kote.obrio.ui.common

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonTopBar(
    title: @Composable () -> Unit,
    secondaryScreen: Boolean = false,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable () -> Unit? = {},
) {
    TopAppBar(
        title = title,
        navigationIcon = {
            if (secondaryScreen) {
                navigationIcon()
            }
        },
        actions = {
            if (secondaryScreen) {
                actions()
            }
        }
    )
}
