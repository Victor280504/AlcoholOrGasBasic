package com.example.alcoholorgas.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.alcoholorgas.R
import com.example.alcoholorgas.ui.components.PrimaryButton
import com.example.alcoholorgas.ui.components.AppTextField
import com.example.alcoholorgas.data.model.GasStation
import com.example.alcoholorgas.data.repository.GasStationRepository
import com.example.alcoholorgas.ui.components.ActionButton
import com.example.alcoholorgas.ui.components.PriceInput
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alcoholorgas.ui.viewmodels.EditViewModel
import android.Manifest
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.alcoholorgas.ui.components.PermissionRationaleDialog
import com.example.alcoholorgas.ui.components.getPermission
import com.example.alcoholorgas.ui.components.openAppSettings
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import java.text.DateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    navController: NavHostController,
    stationRepo: GasStationRepository,
    station: GasStation?,
    viewModel: EditViewModel = viewModel(
        key = station?.uuid?.toString(),
        factory = EditViewModel.provideFactory(stationRepo, station)
    )
) {
    val context = LocalContext.current
    val name by viewModel.name.collectAsStateWithLifecycle()
    val alcoholPrice by viewModel.alcoholPrice.collectAsStateWithLifecycle()
    val gasolinePrice by viewModel.gasolinePrice.collectAsStateWithLifecycle()
    val mutableStation by viewModel.station.collectAsStateWithLifecycle()

    val topBarTitle = if (station != null) {
        val displayName = station.name.ifBlank { stringResource(R.string.not_identified) }
        stringResource(R.string.edit_title, displayName)
    } else {
        stringResource(R.string.not_identified)
    }

    val showLocationRationale = remember { mutableStateOf(false) }
    val showSettingsDialog = remember { mutableStateOf(false) }
    val showDeleteDialog = remember { mutableStateOf(false) }

    val stationSavedMessage = stringResource(R.string.station_saved)
    val failedToObtainLocationMessage = stringResource(R.string.failed_to_obtain_location)

    val handlePermissionGranted = {
        getLastLocation(
            context = context,
            onResult = { lat, lng ->
                viewModel.updateLocation(lat, lng)
                Toast.makeText(context, stationSavedMessage, Toast.LENGTH_SHORT).show()
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

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            handlePermissionGranted()
        } else {
            val activity = context as? ComponentActivity
            if (activity?.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) == false) {
                showSettingsDialog.value = true
            } else {
                showLocationRationale.value = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(topBarTitle) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    if (station != null) {
                        IconButton(onClick = {
                            showDeleteDialog.value = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.delete_station),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        },
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

            if (station != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.details),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = DateFormat.getDateTimeInstance()
                                    .format(station.registerDate),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Lat: ${
                                    String.format(
                                        "%.5f",
                                        mutableStation?.coord?.lat ?: station.coord.lat
                                    )
                                }",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Long: ${
                                    String.format(
                                        "%.5f",
                                        mutableStation?.coord?.lgt ?: station.coord.lgt
                                    )
                                }",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        ActionButton(
                            onClick = {
                                getPermission(
                                    context = context,
                                    permission = Manifest.permission.ACCESS_FINE_LOCATION,
                                    onGranted = handlePermissionGranted,
                                    onShowRationale = {
                                        showLocationRationale.value = true
                                    },
                                    onLaunchPermission = {
                                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                    }
                                )
                            },
                            text = stringResource(R.string.update_coordinates),
                            icon = Icons.Default.MyLocation,
                            color = MaterialTheme.colorScheme.secondary
                        )

                    }
                }
                AppTextField(
                    value = name,
                    onValueChange = { viewModel.onNameChange(it) },
                    label = stringResource(R.string.station_name)
                )

                Spacer(modifier = Modifier.height(8.dp))

                PriceInput(
                    value = alcoholPrice,
                    onValueChange = { viewModel.onAlcoholPriceChange(it) },
                    label = stringResource(R.string.alcohol_price),
                )
                PriceInput(
                    value = gasolinePrice,
                    onValueChange = { viewModel.onGasolinePriceChange(it) },
                    label = stringResource(R.string.gasoline_price),
                )
                PrimaryButton(
                    onClick = {
                        viewModel.save {
                            navController.popBackStack()
                        }
                    },
                    text = stringResource(R.string.save),
                )

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

            if (showDeleteDialog.value) {
                PermissionRationaleDialog(
                    title = stringResource(R.string.delete_station),
                    message = stringResource(R.string.delete_station_message),
                    confirmText = stringResource(R.string.yes),
                    onConfirm = {
                        showDeleteDialog.value = false
                        viewModel.deleteStation(mutableStation) {
                            navController.popBackStack()
                        }
                    },
                    onDismiss = { showDeleteDialog.value = false }
                )
            }
        }
    }
}
