package com.Yogify.birthdayreminder.ui.activitys

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.Yogify.birthdayreminder.R
import com.Yogify.birthdayreminder.util.scaner.BarcodeBoxView
import com.Yogify.birthdayreminder.util.scaner.QrCodeAnalyzer
import com.Yogify.birthdayreminder.databinding.ActivityBarCodeScanerBinding
import com.Yogify.birthdayreminder.util.utils
import com.Yogify.birthdayreminder.util.utils.Companion.toneWithVibariton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class BarCodeScanerActivity : BaseActivity() {
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var binding: ActivityBarCodeScanerBinding
    lateinit var barcodeBoxView: BarcodeBoxView
    lateinit var qrCodeAnalyzer: QrCodeAnalyzer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        binding = ActivityBarCodeScanerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraExecutor = Executors.newSingleThreadExecutor()
        barcodeBoxView = BarcodeBoxView(this)
        addContentView(
            barcodeBoxView, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        qrCodeAnalyzer = QrCodeAnalyzer(
            this,
            barcodeBoxView,
            binding.previewView.width.toFloat(),
            binding.previewView.height.toFloat()
        )


        binding.qrImg.setOnClickListener {
            showImagePicker()
        }

        startCamera()
    }


    fun showImagePicker() {
        pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    val pickImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            CoroutineScope(Dispatchers.IO).launch {
                var barcodeBitmap = utils.getBitmapFromUri(applicationContext, uri)
                //val frame = Frame.Builder().setBitmap(barcodeBitmap).build()
                qrCodeAnalyzer.processImage(barcodeBitmap!!)

            }
        }
    }

    override fun onPause() {
        super.onPause()
        cameraExecutor.shutdown()

    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder().build()
                .also { it.setSurfaceProvider(binding.previewView.surfaceProvider) }

            // Image analyzer
            qrCodeAnalyzer.barcodeData.observe(this, Observer {
                if (!it.isNullOrEmpty()) {
                    cameraExecutor.shutdown()
                    postReminderItem(it!!)
                }

            })

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build().also {
                    it.setAnalyzer(
                        cameraExecutor, qrCodeAnalyzer
                    )
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer
                )

            } catch (exc: Exception) {
                exc.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun postReminderItem(scandata: String) {
        var dataobject = utils.stringToReminderDataObject(scandata)
        if (dataobject != null) {
            toneWithVibariton(applicationContext)
            Log.d("DATASCAN", scandata)
            val intent = Intent()
            intent.putExtra(utils.QR_DATA, scandata)
            setResult(RESULT_OK, intent)
            finish()
        } else {
            utils.showSnackbar(binding.previewView, getString(R.string.remindercontentNotFound))
        }
    }
}