package com.elkiplangat.barcodescanner

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.elkiplangat.barcodescanner.barcode.BarCode
import com.elkiplangat.barcodescanner.databinding.FragmentCameraBinding
import com.elkiplangat.barcodescanner.utils.YuvToRgbConverter
import com.google.common.util.concurrent.ListenableFuture
import com.google.firebase.FirebaseApp
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import kotlinx.android.synthetic.main.top_action_bar_in_live_camera.view.*
import kotlinx.coroutines.*
import java.lang.Runnable
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.Exception


class CameraFragment : Fragment() {
    private val detector = FirebaseVision.getInstance().visionBarcodeDetector
    private var pauseAnalysis = false
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var cameraFragmentBinding: FragmentCameraBinding
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
    private lateinit var bitmapBuffer: Bitmap
    private var barcodeLists = MutableLiveData<BarCode>()
    private lateinit var cameraExecutor: ExecutorService
    private var imageRotationDegrees: Int = 0
    private var isFlashOn: Boolean = false
    private lateinit var camera: androidx.camera.core.Camera

    //private lateinit var previewView: PreviewView
    private val viewModel by lazy { ViewModelProvider(requireActivity()).get(CameraFragmentViewModel::class.java) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCamera()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inflater = LayoutInflater.from(requireContext())
        cameraFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_camera, container, false)
        cameraExecutor = Executors.newSingleThreadExecutor()
        FirebaseApp.initializeApp(requireContext())
        cameraFragmentBinding.actionBarCamera.close_button.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_cameraFragment_to_mainFragment)
        }
        cameraFragmentBinding.actionBarCamera.flash_button.setOnClickListener { toggleFlash() }
        return cameraFragmentBinding.root

    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private fun setupCamera(): Unit {
        // previewView.

        cameraProviderFuture =
            ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener(Runnable {

            cameraProvider = cameraProviderFuture.get()
            var preview: Preview = Preview.Builder()
                .build()


            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            var frameCounter = 0
            var lastFpsTimestamp = System.currentTimeMillis()
            val converter = YuvToRgbConverter(requireContext())


            try {

                imageAnalysis.setAnalyzer(cameraExecutor, ImageAnalysis.Analyzer { image ->

                    if (!::bitmapBuffer.isInitialized) {
                        Log.d("CAMERAFRAGMENT", "NOT INITIALIZED")
                        val rotationDegrees = image.imageInfo.rotationDegrees
                        bitmapBuffer = Bitmap.createBitmap(
                            image.width, image.height, Bitmap.Config.ARGB_8888
                        )
                    }

                    if (pauseAnalysis) {
                        image.close()
                        return@Analyzer
                    }
                    image.use {
                        converter.yuvToRgb(image.image!!, bitmapBuffer)

                    }

                    detect(bitmapBuffer)


                })
            } catch (e: Exception) {
                Log.d("CAMERAFRAGMENT", "ERROR: ${e.message}")
            }
            var cameraSelector: CameraSelector = CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()
            cameraProvider.unbindAll()

            camera =
                cameraProvider.bindToLifecycle(
                    this as LifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )


            preview.setSurfaceProvider(cameraFragmentBinding.previewView.createSurfaceProvider())


        }, ContextCompat.getMainExecutor(requireContext()))

    }

    private fun detect(bitmap: Bitmap) {
        val image = FirebaseVisionImage.fromBitmap(bitmap)
        detector.detectInImage(image).addOnSuccessListener {
            for (firebaseBarCode in it) {
                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO) {
                        setUpCompletionView(
                            BarCode(
                                firebaseBarCode.valueType,
                                firebaseBarCode.displayValue, Date()
                            )
                        )


                    }
                }
            }
            animateView(cameraFragmentBinding.cameraFrameView)
            Log.d("CAMERAFRAGMENT", "${it.toString()}")
        }.addOnFailureListener {
            Log.d("BARCODE", "ERROR ${it.message}")
        }.addOnCompleteListener {
            //setUpCompletionView(it)
            Log.d("BARCODE", "COMPLETE ${it.isSuccessful}")
        }

    }

    private suspend fun setUpCompletionView(barcode: BarCode) {
        withContext(Dispatchers.Main) {
            cameraFragmentBinding.apply {

                constraintLayout.visibility = View.VISIBLE

                textViewContents.text = barcode.value
                // textViewType.text = FirebaseVisionBarcode(barcode.type).format
            }
        }
        cameraFragmentBinding.saveButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    viewModel.insertBarCode(barcode)
                }
            }
        }


    }


    private fun toggleFlash() {
        if (isFlashOn) {
            camera.cameraControl.enableTorch(false)
            isFlashOn = false
            cameraFragmentBinding.actionBarCamera.flash_button.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_flash_off_vd_white_24,
                    null
                )
            )
        } else if (!isFlashOn) {
            camera.cameraControl.enableTorch(true)
            isFlashOn = true
            cameraFragmentBinding.actionBarCamera.flash_button.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_flash_on_vd_white_24,
                    null
                )
            )
        }


    }

    private fun animateView(view: View) {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.5f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.5F)
        val animator: ObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY)
        animator.repeatCount = 1
        animator.repeatMode = ObjectAnimator.REVERSE

        animator.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        try {
            cameraProvider.unbindAll()
        } catch (e: Exception) {

        }

    }

}