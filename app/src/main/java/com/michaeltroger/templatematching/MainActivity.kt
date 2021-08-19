package com.michaeltroger.templatematching

import android.Manifest
import android.content.pm.PackageManager
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.view.SurfaceView
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import org.opencv.android.*
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame
import org.opencv.core.*
import java.io.IOException

/**
 * template matching example
 */
class MainActivity : ComponentActivity(), CvCameraViewListener2 {
    /**
     * the camera view
     */
    private var mOpenCvCameraView: CameraBridgeViewBase? = null

    /**
     * the result matrix
     */
    var result: Mat? = null

    /**
     * the camera image
     */
    var img: Mat? = null

    /**
     * the template image used for template matching
     * or for copying into the camera view
     */
    var templ: Mat? = null

    /**
     * the crop rectangle with the size of the template image
     */
    var rect: Rect? = null

    /**
     * selected area is the camera preview cut to the crop rectangle
     */
    var selectedArea: Mat? = null
    private val mLoaderCallback: BaseLoaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                SUCCESS -> {
                    Log.i(TAG, "OpenCV loaded successfully")

                    // load the specified image from file system in bgr color
                    var bgr: Mat? = null
                    try {
                        bgr = Utils.loadResource(
                            applicationContext,
                            TEMPLATE_IMAGE,
                            Imgcodecs.CV_LOAD_IMAGE_COLOR
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    // convert the image to rgba
                    templ = Mat()
                    Imgproc.cvtColor(bgr, templ, Imgproc.COLOR_BGR2GRAY) //Imgproc.COLOR_BGR2RGBA);

                    // Imgproc.Canny(templ, templ, 50.0, 200.0);

                    // init the crop rectangle, necessary for copying the image to the camera view
                    rect = Rect(0, 0, templ!!.width(), templ!!.height())

                    // init the result matrix
                    result = Mat()
                    img = Mat()
                    mOpenCvCameraView!!.enableView()
                }
                else -> {
                    super.onManagerConnected(status)
                }
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                onPermissionGranted()
            } else {
                checkPermissonAndInitialize()
            }
        }

    private fun checkPermissonAndInitialize() {
        if (ContextCompat.checkSelfPermission(baseContext, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            onPermissionGranted()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    fun onPermissionGranted() {
        if (FIXED_FRAME_SIZE) {
            mOpenCvCameraView!!.setMaxFrameSize(FRAME_SIZE_WIDTH, FRAME_SIZE_HEIGHT)
        }
        mOpenCvCameraView!!.visibility = SurfaceView.VISIBLE
        mOpenCvCameraView!!.setCvCameraViewListener(this)
    }

    /** Called when the activity is first created.  */
    public override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "called onCreate")
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_main)
        mOpenCvCameraView =
            findViewById<View>(R.id.tutorial1_activity_java_surface_view) as CameraBridgeViewBase

        checkPermissonAndInitialize()
    }

    public override fun onPause() {
        super.onPause()
        if (mOpenCvCameraView != null) mOpenCvCameraView!!.disableView()
    }

    public override fun onResume() {
        super.onResume()
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization")
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback)
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!")
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (mOpenCvCameraView != null) mOpenCvCameraView!!.disableView()
    }

    override fun onCameraViewStarted(width: Int, height: Int) {}
    override fun onCameraViewStopped() {}
    override fun onCameraFrame(inputFrame: CvCameraViewFrame): Mat {
        img = inputFrame.gray()

        // copying the image into the camera preview
        // selectedArea = img.submat(rect);
        //  templ.copyTo(selectedArea);

        // template matching
        //Imgproc.Canny(img, img, 50.0, 200.0);

        /// Source image to display
        val img_display = Mat()
        img!!.copyTo(img_display)

        /// Create the result matrix
        val result_cols = img!!.cols() - templ!!.cols() + 1
        val result_rows = img!!.rows() - templ!!.rows() + 1
        result!!.create(result_rows, result_cols, CvType.CV_32FC1)

        /// Do the Matching and Normalize
        val match_method = Imgproc.TM_SQDIFF
        Imgproc.matchTemplate(img, templ, result, match_method)
        Core.normalize(result, result, 0.0, 1.0, Core.NORM_MINMAX, -1, Mat())
        val minMaxLocResult = Core.minMaxLoc(result, Mat())
        /// For SQDIFF and SQDIFF_NORMED, the best matches are lower values. For all the other methods, the higher the better
        val matchLoc: Point = if (match_method == Imgproc.TM_SQDIFF || match_method == Imgproc.TM_SQDIFF_NORMED) {
                minMaxLocResult.minLoc
            } else {
                minMaxLocResult.maxLoc
            }
        Imgproc.rectangle(
            img_display,
            matchLoc,
            Point(matchLoc.x + templ!!.cols(), matchLoc.y + templ!!.rows()),
            Scalar(255.0, 0.0, 0.0)
        )
        Imgproc.rectangle(
            result,
            matchLoc,
            Point(matchLoc.x + templ!!.cols(), matchLoc.y + templ!!.rows()),
            Scalar(255.0, 0.0, 0.0)
        )
        return img_display
    }

    companion object {
        /**
         * class name for debugging with logcat
         */
        private val TAG = MainActivity::class.java.name

        /**
         * the template image to use
         */
        private const val TEMPLATE_IMAGE = R.drawable.pxl

        /**
         * frame size width
         */
        private const val FRAME_SIZE_WIDTH = 640

        /**
         * frame size height
         */
        private const val FRAME_SIZE_HEIGHT = 480

        /**
         * whether or not to use a fixed frame size -> results usually in higher FPS
         * 640 x 480
         */
        private const val FIXED_FRAME_SIZE = true
    }

    init {
        Log.i(TAG, "Instantiated new " + this.javaClass)
    }
}