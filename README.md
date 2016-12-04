# Augmented Reality Template Matching for >= Android 4 #

<img src="/templatematching.png" alt="Augmented Reality template matching" width="400px"/>

### What is this repository for? ###
* Takes the real time camera picture and uses it for finding a specified template image using a template matching approach with OpenCV for Android (Java library). Lines are drawn on top of the camera image where the searched object is expected. This can for example be used to find a logo. 
* Be aware that template matching doesn't allow any rotations or zooming of the camera. Hold the camera without dip and at the 1:1 correct zoom. Otherwise the template image won't be found within the camera image.
* This app is using a gray scale live image for performance reason. This could easily be changed to search in color live images too.

### How do I get set up? ###
* IDE: Android Studio (tested with 2.1.2)
* Android SDK
* Dependencies: OpenCV 3.0.0 library (included)
* Template image location: res/drawable - Image is referenced in MainActivity
* Make sure the app has the required permission on start, as there is no runtime-check yet! (Camera)

### Test image ###
Used default template image:

<img src="/app/src/main/res/drawable/pxl.bmp" alt=""/>

Test with filming the logo at the top left at http://www.pxl.be/

### Who do I talk to? ###
* Repo owner and developer: android@michaeltroger.com

### Credits ###
* The template matching is based on the official OpenCV tutorial http://docs.opencv.org/2.4/doc/tutorials/imgproc/histograms/template_matching/template_matching.html Their version is using OpenCV 2 with normal images.
