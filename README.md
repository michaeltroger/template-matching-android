# Augmented Reality Template Matching using OpenCV 4 for Android 
## Approach: Sum of squared differences (SSD)
[![Android CI](https://github.com/michaeltroger/template-matching-android/actions/workflows/android.yml/badge.svg)](https://github.com/michaeltroger/template-matching-android/actions/workflows/android.yml)

Attention: This app was created in 2016. I was a beginner to Android development and Computer Vision back then. So don't expect a perfect code please. Over the years I updated the dependencies and converted it to Kotlin, while the business logic remained unchanged.

Note: Originally I targeted min SDK 15 (Android 4), more architectures ("mips", "mips64", "armeabi") and OpenCV 3 with this project. Nowadays the repo uses newer versions. If you need to support older devices, then you can look back in the repo's Git history (app version 1.3 / Git tag 4)

<img src="/screenshots/demo.gif" alt="Augmented Reality template matching" width="800px"/>
Copyright of the logo: Hogeschool PXL

### What is this repository for? ###
* Takes the real time camera picture and uses it for finding a specified template image using a template matching approach with OpenCV for Android (Java library). Lines are drawn on top of the camera image where the searched object is expected. This can for example be used to find a logo. 
* Be aware that template matching doesn't allow any rotations or zooming of the camera. Hold the camera without dip and at the 1:1 correct zoom. Otherwise the template image won't be found within the camera image.
* This app is using a gray scale live image for performance reason. This could easily be changed to search in color live images too.
* More computer vision projects at https://michaeltroger.com/computervision/

### How do I get set up? ###
* IDE: Android Studio  (tested with 2023.3.1)
* Android SDK
* Template image location: res/drawable - Image is referenced in MainActivity

### Test image ###
Used default template image:

<img src="/app/src/main/res/drawable/logo.png" alt=""/>
Copyright of the logo: Hogeschool PXL  

Test with filming the logo at the top left at http://www.pxl.be/

### Author ###
[Michael Troger](https://michaeltroger.com)

### Credits ###
* The template matching is based on the official OpenCV tutorial http://docs.opencv.org/2.4/doc/tutorials/imgproc/histograms/template_matching/template_matching.html Their version is using OpenCV 2 with static images files.
