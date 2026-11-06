package com.simats.aiskillmirror.ui.upload

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.media.MediaRecorder
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import java.io.File
import java.io.FileOutputStream

class LiveCameraManager(private val context: Context) {

    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private var cameraDevice: CameraDevice? = null
    private var captureSession: CameraCaptureSession? = null
    private var previewRequestBuilder: CaptureRequest.Builder? = null

    private var backgroundThread: HandlerThread? = null
    private var backgroundHandler: Handler? = null

    private var currentTextureView: TextureView? = null
    private var mediaRecorder: MediaRecorder? = null
    private var currentVideoOutputFile: File? = null

    var isFrontCamera: Boolean = false
        private set

    var isRecording: Boolean = false
        private set

    var isPaused: Boolean = false
        private set

    private fun startBackgroundThread() {
        if (backgroundThread == null) {
            backgroundThread = HandlerThread("SkillMirrorCameraBackground").apply {
                start()
                backgroundHandler = Handler(looper)
            }
        }
    }

    private fun stopBackgroundThread() {
        backgroundThread?.quitSafely()
        try {
            backgroundThread?.join()
        } catch (_: InterruptedException) {}
        backgroundThread = null
        backgroundHandler = null
    }

    fun getCameraId(front: Boolean): String? {
        try {
            val facing = if (front) CameraCharacteristics.LENS_FACING_FRONT else CameraCharacteristics.LENS_FACING_BACK
            for (id in cameraManager.cameraIdList) {
                val chars = cameraManager.getCameraCharacteristics(id)
                if (chars.get(CameraCharacteristics.LENS_FACING) == facing) {
                    return id
                }
            }
        } catch (e: Exception) {
            Log.e("LiveCameraManager", "Error finding camera: ${e.message}")
        }
        return cameraManager.cameraIdList.firstOrNull()
    }

    @SuppressLint("MissingPermission")
    fun startCamera(textureView: TextureView, front: Boolean = false, onError: (String) -> Unit = {}) {
        currentTextureView = textureView
        isFrontCamera = front
        startBackgroundThread()

        if (textureView.isAvailable) {
            openCamera(front, onError)
        } else {
            textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                    openCamera(front, onError)
                }

                override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}
                override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                    stopCamera()
                    return true
                }

                override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun openCamera(front: Boolean, onError: (String) -> Unit) {
        val cameraId = getCameraId(front)
        if (cameraId == null) {
            onError("No camera available on this device.")
            return
        }

        try {
            cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    cameraDevice = camera
                    createCameraPreviewSession(onError)
                }

                override fun onDisconnected(camera: CameraDevice) {
                    camera.close()
                    cameraDevice = null
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    camera.close()
                    cameraDevice = null
                    onError("Camera device error: $error")
                }
            }, backgroundHandler)
        } catch (e: Exception) {
            onError("Failed to open camera: ${e.message}")
        }
    }

    private fun createCameraPreviewSession(onError: (String) -> Unit) {
        val texture = currentTextureView?.surfaceTexture ?: return
        val device = cameraDevice ?: return

        try {
            // Set default buffer size
            texture.setDefaultBufferSize(1280, 720)
            val surface = Surface(texture)

            previewRequestBuilder = device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
                addTarget(surface)
                set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
            }

            device.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    if (cameraDevice == null) return
                    captureSession = session
                    try {
                        previewRequestBuilder?.let { builder ->
                            session.setRepeatingRequest(builder.build(), null, backgroundHandler)
                        }
                    } catch (e: Exception) {
                        Log.e("LiveCameraManager", "Error starting camera preview: ${e.message}")
                    }
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    onError("Failed to configure camera preview.")
                }
            }, backgroundHandler)

        } catch (e: Exception) {
            onError("Error creating preview session: ${e.message}")
        }
    }

    fun switchCamera(onError: (String) -> Unit = {}) {
        val targetFront = !isFrontCamera
        stopCamera()
        currentTextureView?.let { view ->
            startCamera(view, front = targetFront, onError = onError)
        }
    }

    fun capturePhoto(onSuccess: (Bitmap, File) -> Unit, onError: (String) -> Unit) {
        val textureView = currentTextureView
        if (textureView == null) {
            onError("Camera preview is not active.")
            return
        }

        try {
            var bitmap = textureView.bitmap
            if (bitmap == null) {
                onError("Failed to capture image from camera viewfinder.")
                return
            }

            // Mirror if front camera for natural selfie orientation
            if (isFrontCamera) {
                val matrix = Matrix().apply { preScale(-1f, 1f) }
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            }

            val file = File(context.cacheDir, "live_photo_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
            }

            onSuccess(bitmap, file)

        } catch (e: Exception) {
            onError("Capture error: ${e.message}")
        }
    }

    fun startVideoRecording(onStarted: () -> Unit, onError: (String) -> Unit) {
        val device = cameraDevice
        val texture = currentTextureView?.surfaceTexture
        if (device == null || texture == null) {
            onError("Camera preview not ready.")
            return
        }

        try {
            stopPreviewSession()

            val outputFile = File(context.cacheDir, "live_video_${System.currentTimeMillis()}.mp4")
            currentVideoOutputFile = outputFile

            val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            recorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setVideoSource(MediaRecorder.VideoSource.SURFACE)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setOutputFile(outputFile.absolutePath)
                setVideoEncodingBitRate(3_000_000)
                setVideoFrameRate(30)
                setVideoSize(1280, 720)
                setVideoEncoder(MediaRecorder.VideoEncoder.H264)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                // Orientation hint
                setOrientationHint(if (isFrontCamera) 270 else 90)
                prepare()
            }
            mediaRecorder = recorder

            texture.setDefaultBufferSize(1280, 720)
            val previewSurface = Surface(texture)
            val recorderSurface = recorder.surface

            val recordRequestBuilder = device.createCaptureRequest(CameraDevice.TEMPLATE_RECORD).apply {
                addTarget(previewSurface)
                addTarget(recorderSurface)
                set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO)
            }

            device.createCaptureSession(listOf(previewSurface, recorderSurface), object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    captureSession = session
                    try {
                        session.setRepeatingRequest(recordRequestBuilder.build(), null, backgroundHandler)
                        recorder.start()
                        isRecording = true
                        isPaused = false
                        onStarted()
                    } catch (e: Exception) {
                        onError("Failed to start recorder: ${e.message}")
                    }
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    onError("Failed to configure recording session.")
                }
            }, backgroundHandler)

        } catch (e: Exception) {
            onError("Video recording setup error: ${e.message}")
        }
    }

    fun pauseVideoRecording() {
        if (isRecording && !isPaused) {
            try {
                mediaRecorder?.pause()
                isPaused = true
            } catch (e: Exception) {
                Log.e("LiveCameraManager", "Pause error: ${e.message}")
            }
        }
    }

    fun resumeVideoRecording() {
        if (isRecording && isPaused) {
            try {
                mediaRecorder?.resume()
                isPaused = false
            } catch (e: Exception) {
                Log.e("LiveCameraManager", "Resume error: ${e.message}")
            }
        }
    }

    fun stopVideoRecording(onSuccess: (File) -> Unit, onError: (String) -> Unit) {
        if (!isRecording) return

        try {
            isRecording = false
            isPaused = false

            mediaRecorder?.apply {
                try {
                    stop()
                } catch (_: Exception) {}
                reset()
                release()
            }
            mediaRecorder = null

            val file = currentVideoOutputFile
            if (file != null && file.exists() && file.length() > 0) {
                onSuccess(file)
            } else {
                onError("Recorded video file is empty or missing.")
            }

            // Restore normal preview session
            createCameraPreviewSession { /* ignore */ }

        } catch (e: Exception) {
            onError("Failed to stop recording: ${e.message}")
        }
    }

    private fun stopPreviewSession() {
        try {
            captureSession?.stopRepeating()
            captureSession?.close()
            captureSession = null
        } catch (_: Exception) {}
    }

    fun stopCamera() {
        try {
            if (isRecording) {
                mediaRecorder?.apply {
                    try { stop() } catch (_: Exception) {}
                    release()
                }
                mediaRecorder = null
                isRecording = false
                isPaused = false
            }
            stopPreviewSession()
            cameraDevice?.close()
            cameraDevice = null
            stopBackgroundThread()
        } catch (_: Exception) {}
    }
}

