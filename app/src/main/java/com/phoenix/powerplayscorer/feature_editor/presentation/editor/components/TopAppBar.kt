package com.phoenix.powerplayscorer.feature_editor.presentation.editor.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = "New Match",
                style = MaterialTheme.typography.headlineMedium
            )
        },
        navigationIcon = {
            TeamsNrIcon(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    )
}