package com.example.alcoholorgas.ui.screens

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FmdGood
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import com.example.alcoholorgas.R
import com.example.alcoholorgas.data.repository.GasStationRepository
import com.example.alcoholorgas.data.ApplicationSettings
import com.example.alcoholorgas.util.calculateIsAlcoholBetter
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alcoholorgas.data.model.GasStation
import com.example.alcoholorgas.ui.viewmodels.StationsViewModel
import java.text.DateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationsScreen(
    navController: NavHostController,
    repository: GasStationRepository,
    settingsRepository: ApplicationSettings,
    viewModel: StationsViewModel = viewModel(
        factory = StationsViewModel.provideFactory(repository, settingsRepository)
    )
) {
    val context = LocalContext.current
    val storedStations by viewModel.stations.collectAsStateWithLifecycle()
    val is75Percent = viewModel.is75Percent
    val dateFormatter = remember {
        DateFormat.getDateTimeInstance(
            DateFormat.SHORT,
            DateFormat.SHORT
        )
    }

    LaunchedEffect(Unit) {
        viewModel.loadStations()
    }

    BackHandler {
        navController.popBackStack("home", false)
    }

    val homeScreenDescription = stringResource(R.string.home_screen)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.list_of_gas_stations),
                        modifier = Modifier.semantics { heading() },
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(
                                R.string.back,
                                homeScreenDescription
                            )
                        )
                    }
                },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (storedStations.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_stations_found),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(storedStations, key = { it.uuid }) { item ->
                        val isAlcoholBetter = calculateIsAlcoholBetter(
                            item.alcoholPrice,
                            item.gasolinePrice,
                            is75Percent
                        )
                        val editStationLabel = stringResource(R.string.edit_station, item.name)
                        val viewOnMapLabel = stringResource(R.string.view_on_map, item.name)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .semantics {
                                    onClick(
                                        label = editStationLabel,
                                        action = null
                                    )
                                    customActions = listOf(
                                        CustomAccessibilityAction(
                                            label = viewOnMapLabel,
                                            action = {
                                                openStationOnMap(context, item)
                                                ; true
                                            }
                                        )
                                    )
                                },
                            onClick = {
                                navController.navigate("stations/${item.uuid}")
                            },
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.name.ifBlank { stringResource(R.string.station_name) },
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "(${dateFormatter.format(item.registerDate)})",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(vertical = 3.dp)
                                    )
                                    Text(
                                        text = "${stringResource(R.string.alcohol_price)}: R$ ${item.alcoholPrice}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                    Text(
                                        text = "${stringResource(R.string.gasoline_price)}: R$ ${item.gasolinePrice}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    val resultText = when (isAlcoholBetter) {
                                        true -> {
                                            stringResource(R.string.buy_alcohol)
                                        }

                                        false -> {
                                            stringResource(R.string.buy_gasoline)
                                        }

                                        else -> {
                                            ""
                                        }
                                    }

                                    if (resultText.isNotEmpty()) {
                                        Text(
                                            text = resultText,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color =
                                                if (isAlcoholBetter == true)
                                                    MaterialTheme.colorScheme.primary
                                                else
                                                    MaterialTheme.colorScheme.secondary,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }

                                Row {
                                    IconButton(
                                        modifier = Modifier.clearAndSetSemantics { },
                                        onClick = {
                                            openStationOnMap(context, item)
                                        }) {
                                        Icon(
                                            imageVector = Icons.Default.FmdGood,
                                            contentDescription = viewOnMapLabel,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


fun openStationOnMap(context: Context, station: GasStation) {
    val gmmIntentUri =
        "geo:${station.coord.lat},${station.coord.lgt}".toUri()
    val mapIntent =
        Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            setPackage("com.google.android.apps.maps")
        }
    try {
        context.startActivity(mapIntent)
    } catch (e: Exception) {
        Log.e("LOC", e.toString())
        Toast.makeText(
            context,
            context.getString(R.string.map_not_found),
            Toast.LENGTH_SHORT
        ).show()
    }
}
