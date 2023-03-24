package launcher.services.merger.specialMergers;

import launcher.model.InputVideo;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.Mat;

import java.util.List;

public class RvrHorizontalMerger implements SpecialMerger {

    private int frameCount = 0;

    @Override
    public void merge(List<InputVideo> videos, OpenCVFrameConverter.ToMat converter, FrameRecorder recorder, Mat combinedMat, int length) {
        while (frameCount < length) {
            for (InputVideo video : videos) {
                FFmpegFrameGrabber grabber = video.getGrabber();
                Frame frame;
                try{
                    frame = grabber.grabFrame();
                } catch (FrameGrabber.Exception e) {
                    throw new RuntimeException(e);
                }
                Mat mat = new Mat (converter.convert(frame));
                int startWidth;
                if (videos.indexOf(video) == 0) {
                    startWidth = 0;
                } else {
                    startWidth = videos.get(videos.indexOf(video)-1).getWidth();
                }
                int endWidth = startWidth + video.getWidth();
                mat.copyTo(combinedMat.colRange(startWidth, endWidth));

                Frame outputFrame = converter.convert(combinedMat);
                try {
                    recorder.record(outputFrame);
                } catch (FrameRecorder.Exception e) {
                    throw new RuntimeException(e);
                }
            }
            frameCount++;
        }
    }
}
