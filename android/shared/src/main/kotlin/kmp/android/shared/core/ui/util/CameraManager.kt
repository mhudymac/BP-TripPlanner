package kmp.android.shared.core.ui.util

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import kmp.android.shared.R
import java.io.File
import java.util.Objects

@Composable
fun rememberCameraManager(onResult: (Uri?) -> Unit): CameraManager {
    val context = LocalContext.current
    var tempPhotoUri by remember { mutableStateOf(value = Uri.EMPTY) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                val savedUri = saveImageToInternalStorage(context, tempPhotoUri, "picture_${System.currentTimeMillis()}.png")
                onResult.invoke(savedUri)
            }
        }
    )
    return remember {
        CameraManager(
            onLaunch = {
                tempPhotoUri = ComposeFileProvider.getImageUri(context)
                cameraLauncher.launch(tempPhotoUri)
            }
        )
    }
}

class CameraManager(
    private val onLaunch: () -> Unit,
) {
    fun launch() {
        onLaunch()
    }
}

class ComposeFileProvider : FileProvider(
    R.xml.path_provider
) {
    companion object {
        fun getImageUri(context: Context): Uri {
            val tempFile = File.createTempFile(
                "picture_${System.currentTimeMillis()}", ".png", context.cacheDir
            ).apply {
                createNewFile()
            }
            val authority = context.applicationContext.packageName + ".provider"

            return getUriForFile(
                Objects.requireNonNull(context),
                authority,
                tempFile,
            )
        }
    }
}