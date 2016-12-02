package com.michaeltroger.templatematching;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.IOException;

/**
 * template matching example
 */
public class MainActivity extends Activity implements CvCameraViewListener2 {
    /**
     * class name for debugging with logcat
     */
    private static final String TAG = MainActivity.class.getName();
    /**
     * the camera view
     */
    private CameraBridgeViewBase mOpenCvCameraView;
    /**
     * the template image to use
     */
    private static final int TEMPLATE_IMAGE = R.drawable.pxl;
    /**
     * the result matrix
     */
    Mat result;
    /**
     * the camera image
     */
    Mat img;
    /**
     * the template image used for template matching
     * or for copying into the camera view
     */
    Mat templ;
    /**
     * the crop rectangle with the size of the template image
     */
    Rect rect;
    /**
     * selected area is the camera preview cut to the crop rectangle
     */
    Mat selectedArea;

    /**
     * frame size width
     */
    private static final int FRAME_SIZE_WIDTH = 640;
    /**
     * frame size height
     */
    private static final int FRAME_SIZE_HEIGHT = 480;
    /**
     * whether or not to use a fixed frame size -> results usually in higher FPS
     * 640 x 480
     */
    private static final boolean FIXED_FRAME_SIZE = true;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");

                    // load the specified image from file system in bgr color
                    Mat bgr = null;
                    try {
                        bgr = Utils.loadResource(getApplicationContext(), TEMPLATE_IMAGE, Imgcodecs.CV_LOAD_IMAGE_COLOR);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // convert the image to rgba
                    templ = new Mat();
                    Imgproc.cvtColor(bgr, templ, Imgproc.COLOR_BGR2GRAY);//Imgproc.COLOR_BGR2RGBA);

                    // Imgproc.Canny(templ, templ, 50.0, 200.0);

                    // init the crop rectangle, necessary for copying the image to the camera view
                    rect = new Rect(0, 0, templ.width(), templ.height());

                    // init the result matrix
                    result = new Mat();
                    img = new Mat();

                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_java_surface_view);

        // Michael Troger
        if (FIXED_FRAME_SIZE) {
            mOpenCvCameraView.setMaxFrameSize(FRAME_SIZE_WIDTH, FRAME_SIZE_HEIGHT);
        }

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {}

    public void onCameraViewStopped() {}


    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        img = inputFrame.gray();

        // copying the image into the camera preview
        // selectedArea = img.submat(rect);
         //  templ.copyTo(selectedArea);

        // template matching
        //Imgproc.Canny(img, img, 50.0, 200.0);

        /// Source image to display
        Mat img_display = new Mat();
        img.copyTo( img_display );

        /// Create the result matrix
        int result_cols =  img.cols() - templ.cols() + 1;
        int result_rows = img.rows() - templ.rows() + 1;
        result.create(result_rows, result_cols, CvType.CV_32FC1);

        /// Do the Matching and Normalize
        int match_method = Imgproc.TM_SQDIFF;
        Imgproc.matchTemplate(img, templ, result, match_method);

        Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        Core.MinMaxLocResult minMaxLocResult = Core.minMaxLoc(result, new Mat());

        Point matchLoc = null;
        /// For SQDIFF and SQDIFF_NORMED, the best matches are lower values. For all the other methods, the higher the better
        if( match_method  == Imgproc.TM_SQDIFF || match_method == Imgproc.TM_SQDIFF_NORMED )
        {
            matchLoc = minMaxLocResult.minLoc;
        }
        else
        {
            matchLoc = minMaxLocResult.maxLoc;
        }

        Imgproc.rectangle(img_display, matchLoc, new Point(matchLoc.x + templ.cols(), matchLoc.y + templ.rows() ), new Scalar(255,0,0) );
        Imgproc.rectangle(result, matchLoc, new Point(matchLoc.x + templ.cols(), matchLoc.y + templ.rows()), new Scalar(255, 0, 0));

        return img_display;
    }
}
