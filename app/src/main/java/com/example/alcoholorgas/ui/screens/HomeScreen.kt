package com.example.alcoholorgas.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.alcoholorgas.R
import com.example.alcoholorgas.ui.components.PrimaryButton
import com.example.alcoholorgas.ui.components.AppTextField
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alcoholorgas.data.ApplicationSettings
import com.example.alcoholorgas.data.repository.GasStationRepository
import com.example.alcoholorgas.ui.components.PercentageSwitch
import com.example.alcoholorgas.ui.components.PermissionRationaleDialog
import com.example.alcoholorgas.ui.components.PriceInput
import com.example.alcoholorgas.ui.components.getPermission
import com.example.alcoholorgas.ui.components.openAppSettings
import com.example.alcoholorgas.ui.viewmodels.HomeViewModel
import com.google.android.gms.location.LocationServices

@Composable
fun HomeScreen(
    navController: NavHostController,
    stationRepo: GasStationRepository,
    settingsRepo: ApplicationSettings,
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.provideFactory(stationRepo, settingsRepo)
    )
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val stationSavedMessage = stringResource(R.string.station_saved)
    val failedToObtainLocationMessage = stringResource(R.string.failed_to_obtain_location)

    val handlePermissionGranted = {
        val result = viewModel.onCalculateResult()
        if (result != null) {
            getLastLocation(
                context = context,
                onResult = { lat: Double, lng: Double ->
                    viewModel.saveStation(lat, lng) {
                        Toast.makeText(
                            context, stationSavedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                onError = {
                    Toast.makeText(
                        context,
                        failedToObtainLocationMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }

    val showLocationRationale = remember { mutableStateOf(false) }
    val showSettingsDialog = remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            handlePermissionGranted()
        } else {
            val activity = context as? ComponentActivity
            if (activity?.shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == false
            ) {
                showSettingsDialog.value = true
            } else {
                showLocationRationale.value = true
            }
        }
    }
    val homeScreenDescription = stringResource(R.string.home_screen)

    Scaffold(
        modifier = Modifier.semantics {
            contentDescription = homeScreenDescription
        },
        containerColor = Color.Transparent,
        topBar = {},
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("stations")
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    Icons.Filled.History,
                    contentDescription = stringResource(R.string.list_of_gas_stations),
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = stringResource(R.string.alcohol_or_gasoline),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            state.result?.let {
                Text(
                    text = stringResource(
                        if (it)
                            R.string.buy_alcohol
                        else
                            R.string.buy_gasoline
                    ),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            PriceInput(
                value = state.alcoholPrice,
                onValueChange = { viewModel.onAlcoholPriceChange(it) },
                label = stringResource(R.string.alcohol_price)
            )

            PriceInput(
                value = state.gasolinePrice,
                onValueChange = { viewModel.onGasolinePriceChange(it) },
                label = stringResource(R.string.gasoline_price)
            )

            AppTextField(
                value = state.station,
                onValueChange = { viewModel.onStationNameChange(it) },
                label = stringResource(R.string.station_placeholder)
            )

            PercentageSwitch(
                checked = state.checked,
                onChange = { viewModel.onTogglePercentage(it) }
            )

            PrimaryButton(
                onClick = {
                    if (state.alcoholPrice.isBlank() ||
                        state.gasolinePrice.isBlank()
                    ) return@PrimaryButton

                    getPermission(
                        context = context,
                        permission = Manifest.permission.ACCESS_FINE_LOCATION,
                        onGranted = handlePermissionGranted,
                        onShowRationale = {
                            showLocationRationale.value = true
                        },
                        onLaunchPermission = {
                            locationPermissionLauncher.launch(
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        }
                    )
                },
                text = stringResource(R.string.calculate),
                isEnabled = state.alcoholPrice.isNotBlank() &&
                        state.gasolinePrice.isNotBlank() &&
                        state.station.isNotBlank(),
            )
        }
    }

    if (showLocationRationale.value) {
        PermissionRationaleDialog(
            title = stringResource(R.string.location_permission),
            message = stringResource(R.string.location_pernission_message),
            onConfirm = {
                showLocationRationale.value = false
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            },
            onDismiss = { showLocationRationale.value = false },
        )
    }

    if (showSettingsDialog.value) {
        PermissionRationaleDialog(
            title = stringResource(R.string.location_permission),
            message = stringResource(R.string.location_permission_denied_permanently),
            confirmText = stringResource(R.string.open_settings),
            onConfirm = {
                showSettingsDialog.value = false
                openAppSettings(context)
            },
            onDismiss = { showSettingsDialog.value = false }
        )
    }
}

@SuppressLint("MissingPermission")
fun getLastLocation(
    context: Context,
    onResult: (Double, Double) -> Unit,
    onError: () -> Unit = {}
) {
    val client = LocationServices.getFusedLocationProviderClient(context)

    client.lastLocation
        .addOnSuccessListener { location ->
            if (location != null) {
                val lat = location.latitude
                val lng = location.longitude
                onResult(lat, lng)
            } else {
                onError()
            }
        }
        .addOnFailureListener {
            onError()
        }
}