package ph.reseta.app.data.scanner

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream
import kotlin.math.max
import kotlin.math.roundToInt

object ImageProcessingUtils {

    private const val MIN_LONG_SIDE_PX = 1600 // Rule 1: No downscale below 1600 px on the long side
    private const val MAX_LONG_SIDE_PX = 2800 // High-res ceiling to prevent mobile OutOfMemory

    /**
     * Reads image URI at full quality without downscaling below 1600 px on the long side.
     */
    suspend fun processImageUriToBytes(
        contentResolver: ContentResolver,
        uri: Uri
    ): ByteArray = withContext(Dispatchers.IO) {
        // 1. First read bounds without decoding entire bitmap into memory
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        contentResolver.openInputStream(uri).use { stream ->
            BitmapFactory.decodeStream(stream, null, options)
        }

        val origWidth = options.outWidth
        val origHeight = options.outHeight
        val originalLongSide = max(origWidth, origHeight)

        // Calculate sample size: ensure we NEVER scale down below 1600 px on the long side
        var sampleSize = 1
        if (originalLongSide > MAX_LONG_SIDE_PX) {
            var tempLongSide = originalLongSide
            while ((tempLongSide / 2) >= MIN_LONG_SIDE_PX) {
                sampleSize *= 2
                tempLongSide /= 2
            }
        }

        val decodeOptions = BitmapFactory.Options().apply {
            inSampleSize = sampleSize
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }

        val decodedBitmap: Bitmap = contentResolver.openInputStream(uri).use { stream ->
            BitmapFactory.decodeStream(stream, null, decodeOptions)
        } ?: throw IllegalArgumentException("Could not decode image from URI: $uri")

        // 2. Handle EXIF rotation so cursive orientation is upright for OCR
        val rotatedBitmap = try {
            contentResolver.openInputStream(uri).use { stream ->
                if (stream != null) fixExifOrientation(stream, decodedBitmap) else decodedBitmap
            }
        } catch (_: Exception) {
            decodedBitmap
        }

        // 3. Compress to full-quality JPEG (95% quality)
        val outputStream = ByteArrayOutputStream()
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)
        if (rotatedBitmap != decodedBitmap) {
            decodedBitmap.recycle()
        }
        rotatedBitmap.recycle()

        outputStream.toByteArray()
    }

    private fun fixExifOrientation(inputStream: InputStream, bitmap: Bitmap): Bitmap {
        val exif = ExifInterface(inputStream)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.preScale(1f, -1f)
            else -> return bitmap
        }

        return Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }
}
