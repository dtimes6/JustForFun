#include <iostream>
#include <opencv/cv.h>
#include <opencv/highgui.h>

IplImage* doDetectFace(IplImage* in, CvMemStorage* storage, CvHaarClassifierCascade* cascade, double scale = 1.3) {
	static CvScalar colors[] = {
		CvScalar(0,0,255),
		CvScalar(0,128,255),
		CvScalar(0,255,255),
		CvScalar(0,255,0),

		CvScalar(255,128,0),
		CvScalar(255,255,0),
		CvScalar(255,0,0),
		CvScalar(255,0,255)
	};

	IplImage* gray = cvCreateImage(cvGetSize(in), IPL_DEPTH_8U, 1);
	cvCvtColor(in, gray, CV_BGR2GRAY);
	cvEqualizeHist( gray, gray);

	cvClearMemStorage(storage);
	CvSeq* objects = cvHaarDetectObjects(gray, cascade, storage, 1.1, 2, 0, cvSize(30,30));

	for (int i = 0; i < (objects ? objects->total : 0); ++i) {
		CvRect* r = (CvRect*)cvGetSeqElem(objects, i);
		cvRectangle(in,cvPoint(r->x, r->y), cvPoint(r->x + r->width, r->y + r->height), colors[i%8]);
	}

	cvReleaseImage(&gray);
	return in;
}

int main (int argc, char** argv) {
	CvMemStorage* storage = cvCreateMemStorage(0);
	CvHaarClassifierCascade* cascade = (CvHaarClassifierCascade*)cvLoad("haarcascade_frontalface_alt.xml", 0, 0, 0);
	if (!cascade ) {
		std::cerr  << "ERROR: Could not load classifier cascade\n";
		return -1;
	}
	CvCapture* capture[2] = { 0, 0 };
	const char* window0;
	const char* window1;
	if (argc == 1) {
		capture[0] = cvCreateCameraCapture(0);
		capture[1] = cvCreateCameraCapture(1);
		window0 = "Camera 1";
		window1 = "Camera 2";
	} else {
		capture[0] = cvCreateFileCapture(argv[1]);
		capture[1] = 0;
		window0 = argv[1];
	}

	cvNamedWindow(window0, CV_WINDOW_AUTOSIZE);
	if (capture[1]) {
		cvNamedWindow(window1, CV_WINDOW_AUTOSIZE);
	}

	IplImage* frame;
	while(1) {
		frame = cvQueryFrame(capture[0]);
		if (!frame) break;
		IplImage* img = doDetectFace(frame, storage, cascade);
		cvShowImage(window0, frame);
		if (capture[1]) {
			frame = cvQueryFrame(capture[1]);
			if (!frame) break;
			IplImage* img = doDetectFace(frame, storage, cascade);
			cvShowImage(window1, frame);
		}
		char c = cvWaitKey(33);
		if (c == 27) break;
	}

	cvReleaseCapture(&capture[0]);
	cvDestroyWindow(window0);
	if (capture[1]) {
		cvReleaseCapture(&capture[1]);
		cvDestroyWindow(window1);
	}

	return 0;
}
