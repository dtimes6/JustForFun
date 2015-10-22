//load the dynamic link library
System.load("/opt/local/share/OpenCV/java/libopencv_java300.dylib");

import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfRect
import org.opencv.core.Point
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
//
// Detects faces in an image, draws boxes around them, and writes the results
// to "faceDetection.jpeg".
//
System.out.println("\nRunning DetectFaceDemo")
// Create a face detector from the cascade file in the resources
// directory.
var faceDetector:CascadeClassifier  = new CascadeClassifier("/opt/local/share/OpenCV/haarcascades/haarcascade_frontalface_alt.xml");
var image:Mat = Imgcodecs.imread("original.jpg");
// Detect faces in the image.
// MatOfRect is a special container class for Rect.
var faceDetections:MatOfRect = new MatOfRect();
faceDetector.detectMultiScale(image, faceDetections);
println("Detected %s faces", faceDetections.toArray().length);
// Draw a bounding box around each face.
for (rect:Rect <- faceDetections.toArray()) {
		println(rect);
    Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
}

// Save the visualized detection.
var filename:String  = "faceDetection.jpeg";
println("Writing: " + filename);
Imgcodecs.imwrite(filename, image);

///
/// scala -classpath /opt/local/share/OpenCV/java/opencv_java300.jar facedetect.scala
///
