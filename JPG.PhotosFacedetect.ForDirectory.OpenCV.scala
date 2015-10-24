#!/usr/bin/env scala -classpath /opt/local/share/OpenCV/java/opencv-300.jar -feature
/**
 * 
 * ImageProcessor
 * 
 **/
import java.net.URL;
import java.net.URLClassLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

ImageProcessor.main(args);

object ImageProcessor extends App {
  private var faceDetectors:Array[CascadeClassifier] = null;
  private var colors:Array[Scalar] = null;
  def setup_opencv() = {
    val OPENCV_ROOT = "/opt/local/share/OpenCV/";
    System.load(OPENCV_ROOT+"java/libopencv_java300.dylib");
    var filesHere = new java.io.File(OPENCV_ROOT+"haarcascades").listFiles();
    faceDetectors = filesHere
      .filter(_.getName.endsWith(".xml"))
      .filter(_.getName.matches(".*face.*"))
      .filter(! _.getName.matches(".*cat.*"))
      .collect{ case file:java.io.File => new CascadeClassifier(file.getAbsolutePath) };
    val step = 256 * 3 / faceDetectors.length;
    
    def getColor(x:Int):Scalar = {
      var r = 0;
      var g = 0;
      var b = 0;
      var offset = x * step;
      if (offset < 256) { r = offset; }
      else if (offset < 256 * 2) { g = offset - 256; }
      else { b = offset - 256 * 2; }
      return new Scalar(r, g, b);
    }
    var numbers = new Array[Int](faceDetectors.length);
    for (x <- 0 to faceDetectors.length - 1) { numbers(x) = x; }
    colors = numbers.collect { case x:Int => getColor(x) }
  }
  def detect_face(file:String, result:String) = {
    // Create a face detector from the cascade file in the resources
    // directory.
    var image:Mat = Imgcodecs.imread(file);
    // Detect faces in the image.
    // MatOfRect is a special container class for Rect.
    var i = 0;
    for (faceDetector <- faceDetectors) {
      var faceDetections:MatOfRect = new MatOfRect();
      faceDetector.detectMultiScale(image, faceDetections);
      // Draw a bounding box around each face.
      for (rect:Rect <- faceDetections.toArray()) {
        Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), colors(i));
      }
      i += 1;
    }
    // Save the visualized detection.
    Imgcodecs.imwrite(result, image);
  }
  def process(directory:String) = {
    var dir = directory;
    if (!dir.endsWith("/")) {
      dir += "/";
    }
    Console.println("processing images for: %s and write to %sresult".format(dir, dir));
    var sourcedir = new java.io.File(dir);
    var resultdir = new java.io.File(dir+"result");
    resultdir.mkdir();
    
    var filesHere = sourcedir.listFiles;
    var count = 0;
    var length = 0;
    def spaceAccordingTo(length:Int):String = {
      var ret = "";
      for (x <- 0 to length) ret += " ";
      return ret;
    }
    for (file <- filesHere 
         if file.getName.endsWith(".jpeg") || 
            file.getName.endsWith(".jpg")) {
      var fileName = file.getAbsolutePath;
      var resultName = resultdir.getAbsolutePath + '/' + file.getName;
      Console.print("\r%s".format(spaceAccordingTo(length)));
      count += 1;
      val msg = "\rprocessing %s [%s]".format(file.getName, count);
      Console.print(msg);
      detect_face(fileName, resultName);
      length = msg.length;
    }
    Console.print("\r%s".format(spaceAccordingTo(length)));
    Console.println("\rDone for all %s pictures !".format(count));
  }

  if (args.length > 0) {
    this.setup_opencv();
    this.process(args(0));
  } else {
    Console.println("Usage: facedetect_for <directory>");
  }
}

