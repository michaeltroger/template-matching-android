# Augmented Reality Template Matching for >= Android 4 #

### What is this repository for? ###
* Takes the real time camera picture and uses it for finding a specified template image using a template matching approach with OpenCV for Android (Java library). Lines are drawn on top of the camera image where the searched object is expected. This can for example be used to find a logo. 

### How do I get set up? ###
* IDE: Android Studio (tested with 2.1.2)
* Android SDK
* Dependencies: OpenCV 3.0.0 library (included)
* Template image location: res/drawable - Image is referenced in MainActivity

### Who do I talk to? ###
* Repo owner and developer: android@michaeltroger.com

### Test image ###
The logo at the top left at http://www.pxl.be/

### Credits ###
* The template matching is based on the official OpenCV tutorial http://docs.opencv.org/2.4/doc/tutorials/imgproc/histograms/template_matching/template_matching.html Their version is using OpenCV 2 with normal images.
