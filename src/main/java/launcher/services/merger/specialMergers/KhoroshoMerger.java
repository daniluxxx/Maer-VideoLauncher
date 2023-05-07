package launcher.services.merger.specialMergers;

import launcher.model.InputVideo;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;

import java.util.List;

public class KhoroshoMerger implements SpecialMerger {
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
                if (video.getWidth() == 704) {
                    mat.copyTo(combinedMat.rowRange(0, 464).colRange(672, 1376));
                } else if (video.getWidth() == 672) {
                    mat.copyTo(combinedMat.rowRange(0, 184).colRange(0, 672));
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
