package com.Yogify.birthdayreminder.util.scaner

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.RectF
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.lifecycle.MutableLiveData
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class QrCodeAnalyzer(
    private val context: Context,
    private val barcodeBoxView: BarcodeBoxView,
    private val previewViewWidth: Float,
    private val previewViewHeight: Float
) : ImageAnalysis.Analyzer {

    /**
     * This parameters will handle preview box scaling
     */
    private var scaleX = 1f
    private var scaleY = 1f

    private fun translateX(x: Float) = x * scaleX
    private fun translateY(y: Float) = y * scaleY

    private val _barcodeData = MutableLiveData<String>()
    val barcodeData = _barcodeData

    private fun adjustBoundingRect(rect: Rect) = RectF(
        translateX(rect.left.toFloat()),
        translateY(rect.top.toFloat()),
        translateX(rect.right.toFloat()),
        translateY(rect.bottom.toFloat())
    )

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        val img = image.image
        if (img != null) {
            // Update scale factors
            scaleX = previewViewWidth / img.height.toFloat()
            scaleY = previewViewHeight / img.width.toFloat()

            val inputImage = InputImage.fromMediaImage(img, image.imageInfo.rotationDegrees)
            processImage(inputImage)
        }

        image.close()
    }

    fun processsWithBitmap(bitmap: Bitmap) {
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        processImage(inputImage)

    }

    private fun processImage(inputImage: InputImage) {
        // Process image searching for barcodes
        val options = BarcodeScannerOptions.Builder().build()

        val scanner = BarcodeScanning.getClient(options)

        scanner.process(inputImage).addOnSuccessListener { barcodes ->
            if (barcodes.isNotEmpty()) {
                for (barcode in barcodes) {
                    // Handle received barcodes...
                    _barcodeData.postValue(barcode.rawValue)
                    // Update bounding rect
                    barcode.boundingBox?.let { rect ->
                        barcodeBoxView.setRect(
                            adjustBoundingRect(
                                rect
                            )
                        )
                    }
                }
            } else {
                // Remove bounding rect
                barcodeBoxView.setRect(RectF())
            }
        }.addOnFailureListener { }
    }
}