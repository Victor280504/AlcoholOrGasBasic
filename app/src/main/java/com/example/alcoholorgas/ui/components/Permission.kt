package com.example.alcoholorgas.ui.components

import android.content.Context
import android.content.pm.PackageManager
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.example.alcoholorgas.R

@Composable
fun PermissionRationaleDialog(
    title: String,
    message: String,
    confirmText: String = stringResource(R.string.allow),
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(confirmText) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}

fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}

// https://developer.android.com/develop/sensors-and-location/location/retrieve-current
fun getPermission(
    context: Context,
    permission: String,
    onGranted: () -> Unit,
    onShowRationale: () -> Unit,
    onLaunchPermission: () -> Unit
) {
    val activity = context as? ComponentActivity
    when {
        ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED -> {
            onGranted()
        }

        activity?.shouldShowRequestPermissionRationale(permission) == true -> {
            onShowRationale()
        }

        else -> {
            onLaunchPermission()
        }
    }
}