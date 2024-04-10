package kmp.android.shared.core.ui.util

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@Composable
fun rememberGalleryManager(onResult: (Uri) -> Unit): GalleryManager {
    val context = LocalContext.current

    val galleryLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri ->
                uri?.let {
                    val savedUri = saveImageToInternalStorage(context, uri, "picture_${System.currentTimeMillis()}.png")
                    onResult.invoke(savedUri)
                }
            }
        )

    return remember {
        GalleryManager(
            onLaunch = {
                galleryLauncher.launch(
                    PickVisualMediaRequest(
                        mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }
        )
    }
}

class GalleryManager(
    private val onLaunch: () -> Unit
) {
    fun launch() {
        onLaunch()
    }
}

fun saveImageToInternalStorage(context: Context, imageUri: Uri, filename: String): Uri {
    val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), filename)
    val outputStream = FileOutputStream(file)

    inputStream?.copyTo(outputStream)

    inputStream?.close()
    outputStream.close()

    return Uri.fromFile(file)
}