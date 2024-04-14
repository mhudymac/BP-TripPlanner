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

/**
 * This composable function creates and returns a GalleryManager.
 * It sets up a launcher for picking visual media and handles the result.
 *
 * @param onResult The function to call with the URI of the picked media.
 * @return A GalleryManager.
 */
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

/**
 * This class represents a manager for a gallery.
 * It provides a function to launch the gallery.
 *
 * @property onLaunch The function to call to launch the gallery.
 */
class GalleryManager(
    private val onLaunch: () -> Unit
) {
    fun launch() {
        onLaunch()
    }
}

/**
 * This function saves an image to the internal storage.
 * It opens an input stream for the image URI, creates a new file, opens an output stream for the file,
 * copies the input stream to the output stream, and closes the streams.
 *
 * @param context The context to use to open the input stream and create the file.
 * @param imageUri The URI of the image to save.
 * @param filename The name of the file to create.
 * @return The URI for the saved image.
 */
fun saveImageToInternalStorage(context: Context, imageUri: Uri, filename: String): Uri {
    val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), filename)
    val outputStream = FileOutputStream(file)

    inputStream?.copyTo(outputStream)

    inputStream?.close()
    outputStream.close()

    return Uri.fromFile(file)
}