package com.example.alcoholorgas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.alcoholorgas.data.ApplicationSettings
import com.example.alcoholorgas.data.repository.GasStationRepository
import com.example.alcoholorgas.ui.screens.EditScreen
import com.example.alcoholorgas.ui.screens.HomeScreen
import com.example.alcoholorgas.ui.theme.AlcoholOrGasTheme
import com.example.alcoholorgas.ui.screens.SplashScreen
import com.example.alcoholorgas.ui.screens.StationsScreen
import java.util.UUID

// https://as2.ftcdn.net/jpg/02/01/91/19/1000_F_201911983_23wbt3yzx4tv8dbZ82BDlml4TTZmOMTH.jpg

class MainActivity : ComponentActivity() {
    private lateinit var gasStationRepository: GasStationRepository
    private lateinit var applicationSettings: ApplicationSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gasStationRepository = GasStationRepository.createSharedPrefsStore(this)
        applicationSettings = ApplicationSettings(this)

        val startDestination = if (applicationSettings.isFirstLaunch()) "splash" else "home"

        setContent {
            AlcoholOrGasTheme(dynamicColor = false) {
                Surface(color = MaterialTheme.colorScheme.surface) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.shape),
                            contentDescription = stringResource(R.string.app_name),
                            modifier = Modifier
                                .size(150.dp)
                                .align(Alignment.TopStart),
                            contentScale = ContentScale.Fit
                        )
                        val navController: NavHostController = rememberNavController()
                        NavHost(
                            navController = navController,
                            startDestination = startDestination
                        ) {
                            composable("splash") {
                                SplashScreen(
                                    navController,
                                    applicationSettings
                                )
                            }
                            composable("home") {
                                HomeScreen(
                                    navController,
                                    gasStationRepository,
                                    applicationSettings
                                )
                            }
                            composable("stations") {
                                StationsScreen(
                                    navController,
                                    gasStationRepository,
                                    applicationSettings
                                )
                            }
                            composable("stations/{id}") { backStackEntry ->
                                val stationIdString = backStackEntry.arguments?.getString("id")
                                val stationId =
                                    runCatching { UUID.fromString(stationIdString) }.getOrNull()

                                if (stationId != null) {
                                    val station = gasStationRepository.findById(stationId)
                                    EditScreen(navController, gasStationRepository, station)
                                } else {
                                    LaunchedEffect(Unit) {
                                        navController.popBackStack()
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



