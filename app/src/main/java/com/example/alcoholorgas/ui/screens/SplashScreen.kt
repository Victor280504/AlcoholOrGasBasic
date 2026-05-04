package com.example.alcoholorgas.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.alcoholorgas.R
import com.example.alcoholorgas.data.ApplicationSettings
import com.example.alcoholorgas.ui.components.PrimaryButton

@Composable
fun SplashScreen(navController: NavHostController, settingsRepository: ApplicationSettings) {

    LaunchedEffect(Unit) {
        settingsRepository.setIsFirstLaunch(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painterResource(id = R.drawable.welcome),
            stringResource(R.string.welcome_image),
            Modifier
                .size(300.dp),
        )
        Text(
            text = stringResource(R.string.alcohol_or_gasoline),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.discover_the_best),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.width(320.dp),
            textAlign = TextAlign.Center
        )
        PrimaryButton(
            { navController.navigate("home") },
            stringResource(R.string.lets_start),
        )
    }
}
