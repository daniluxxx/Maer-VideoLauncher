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

public class KonstruktorMerger implements SpecialMerger {
    private int frameCount = 0;
    @Override
    public void merge(List<InputVideo> videos, OpenCVFrameConverter.ToMat converter, FrameRecorder recorder, Mat combinedMat, int length) {
        while (frameCount < length) {
            for (InputVideo video : videos) {
                Frame frame;
                try {
                    frame = video.getGrabber().grabFrame();
                } catch (FrameGrabber.Exception e) {
                    throw new RuntimeException(e);
                }
                Mat mat = new Mat(converter.convert(frame));
                mat.copyTo(combinedMat.rowRange(0, 288).colRange(0, 960));
                mat.copyTo(combinedMat.rowRange(700, 988).colRange(0,960));
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
