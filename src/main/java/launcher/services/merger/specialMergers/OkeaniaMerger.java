package launcher.services.merger.specialMergers;

import launcher.model.InputVideo;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Size;
import org.opencv.core.CvType;

import java.util.List;

import static org.bytedeco.opencv.global.opencv_imgproc.resize;

public class OkeaniaMerger implements SpecialMerger {
    private int frameCount = 0;
    @Override
    public void merge(List<InputVideo> videos, OpenCVFrameConverter.ToMat converter, FrameRecorder recorder, Mat combinedMat, int length) {
        //TODO сделать постоянный битрейт 20мбит
        while (frameCount < length) {
            for (InputVideo video : videos) {
                Frame frame;
                try {
                    frame = video.getGrabber().grabFrame();
                } catch (FrameGrabber.Exception e) {
                    throw new RuntimeException(e);
                }
                Mat mat = new Mat(converter.convert(frame));
                if (video.getWidth() == 4864) {
                    Rect roiFirst = new Rect(0,0,1920,144);
                    Rect roiSecond = new Rect(1920, 0, 1920, 144);
                    Rect roiThird = new Rect(3840, 0, 1024, 144);

                    Mat subMatFirst = new Mat(mat, roiFirst);
                    Mat subMatSecond = new Mat(mat, roiSecond);
                    Mat subMatThird = new Mat(mat, roiThird);

                    subMatFirst.copyTo(combinedMat.rowRange(0,144).colRange(0,1920));
                    subMatSecond.copyTo(combinedMat.rowRange(144,288).colRange(0, 1920));
                    subMatThird.copyTo(combinedMat.rowRange(288, 432).colRange(0,1024));

                } else if (video.getWidth() == 608) {
                    Size newSize = new Size(304, 352);
                    Mat resized = new Mat(newSize, CvType.CV_8UC3);
                    resize(mat, resized, newSize);
                    resized.copyTo(combinedMat.rowRange(432, 784).colRange(0,304));
                } else if (video.getWidth() == 544) {
                    Size newSize = new Size(272,192);
                    Mat resized = new Mat(newSize, CvType.CV_8UC3);
                    resize(mat, resized, newSize);
                    resized.copyTo(combinedMat.rowRange(432, 624).colRange(304,576));
                }
            }
            Frame outputFrame = converter.convert(combinedMat);
            try {
                recorder.record(outputFrame);
            } catch (FrameRecorder.Exception e) {
                throw new RuntimeException(e);
            }
            frameCount++;
        }
    }
}
