package launcher.services.merger.specialMergers;

import launcher.model.InputVideo;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;

import java.util.List;

public class RvrVerticalMerger implements SpecialMerger {
    private int frameCount = 0;
    private int dynamicWidth = 0; //+192 each round

    @Override
    public void merge(List<InputVideo> videos, OpenCVFrameConverter.ToMat converter, FrameRecorder recorder, Mat combinedMat, int length) {
            if (videos.size() == 1) {
                while (frameCount < length) {
                    InputVideo video = videos.get(0);
                    Frame frame;
                    try {
                        frame = videos.get(0).getGrabber().grabFrame();
                    } catch (FrameGrabber.Exception e) {
                        throw new RuntimeException(e);
                    }
                    Mat mat = new Mat(converter.convert(frame));
                    int startWidth = 0;
                    int endWidth = 192;
                    for (int i = 0; i < 4; i++) {
                        mat.copyTo(combinedMat.colRange(startWidth, endWidth));
                        startWidth += 192;
                        endWidth += 192;
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
            else if (videos.size() == 4) {
                while (frameCount < length) {
                    for (InputVideo video : videos) {
                        Frame frame;
                        try {
                            frame = video.getGrabber().grabFrame();
                        } catch (FrameGrabber.Exception e) {
                            throw new RuntimeException(e);
                        }
                        Mat mat = new Mat(converter.convert(frame));
                            mat.copyTo(combinedMat.colRange(dynamicWidth, (dynamicWidth + 192)));
                            dynamicWidth += 192;
                    }
                    Frame outputFrame = converter.convert(combinedMat);
                    try {
                        recorder.record(outputFrame);
                    } catch (FrameRecorder.Exception e) {
                        throw new RuntimeException(e);
                    }
                    frameCount++;
                    dynamicWidth = 0;
                }
            }

    }
}
