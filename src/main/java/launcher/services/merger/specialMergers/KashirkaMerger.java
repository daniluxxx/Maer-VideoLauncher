package launcher.services.merger.specialMergers;

import launcher.model.InputVideo;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.Mat;

import java.util.List;

public class KashirkaMerger implements SpecialMerger {
    private int middleWidth = 1888;
    private int endWidth = 3904;
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
                if (video.getWidth() == middleWidth) {
                    mat.copyTo(combinedMat.colRange(0, middleWidth));
                } else mat.copyTo(combinedMat.colRange(middleWidth, endWidth));
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