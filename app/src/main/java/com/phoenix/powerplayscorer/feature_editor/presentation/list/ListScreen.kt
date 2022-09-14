package com.phoenix.powerplayscorer.feature_editor.presentation.list

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.phoenix.powerplayscorer.feature_editor.domain.util.Order
import com.phoenix.powerplayscorer.feature_editor.domain.util.OrderType
import com.phoenix.powerplayscorer.feature_editor.presentation.Screen
import com.phoenix.powerplayscorer.feature_editor.presentation.list.components.ItemCard
import com.phoenix.powerplayscorer.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ListScreen(
    viewModel: ListViewModel = hiltViewModel(),
    navController: NavController
) {
    val state = viewModel.state.collectAsState()
    val selected by remember { derivedStateOf { state.value.selectedItems.isEmpty().not() } }

    BackHandler(enabled = selected) {
        viewModel.clearSelectedItems()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.EditorScreen.route)
                },
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                if (selected) {
                    Icon(
                        modifier = Modifier.size(32.dp),
                        painter = painterResource(id = R.drawable.remove),
                        contentDescription = stringResource(id = R.string.create_match)
                    )
                } else {
                    Icon(
                        modifier = Modifier.size(32.dp),
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(id = R.string.create_match)
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
            }
            itemsIndexed(
                items = state.value.list,
                key = { _, item ->
                    val order = state.value.order

                    when(order.orderType) {
                        is OrderType.Ascending -> {
                            when(order) {
                                is Order.Name -> item.key.plus(1)
                                is Order.Date -> item.key.plus(2)
                                is Order.Points -> item.key.plus(3)
                            }
                        }
                        is OrderType.Descending -> {
                            when(order) {
                                is Order.Name -> item.key.plus(4)
                                is Order.Date -> item.key.plus(5)
                                is Order.Points -> item.key.plus(6)
                            }
                        }
                    }
                }
            ) { index, item ->
                val itemSelected by remember { derivedStateOf { state.value.selectedItems.contains(item.key) } }
                Box {
                    ItemCard(
                        item = item,
                        index = index,
                        onClick = {
                            if (selected) {
                                viewModel.selectItem(item.key)
                            } else navController.navigate(Screen.EditorScreen.withArgs(item.key))
                        },
                        onHold = {
                            viewModel.selectItem(item.key)
                        },
                        modifier = Modifier
                            .animateItemPlacement()
                            .padding(vertical = 4.dp, horizontal = 8.dp),
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                color = if (itemSelected)
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                else Color.Transparent
                            )
                    ) {}
                }

            }
            item {
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}