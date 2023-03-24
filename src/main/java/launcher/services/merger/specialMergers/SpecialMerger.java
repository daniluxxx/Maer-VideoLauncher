package launcher.services.merger.specialMergers;

import launcher.model.InputVideo;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;

import java.util.List;

public interface SpecialMerger {
    void merge(List<InputVideo> videos, OpenCVFrameConverter.ToMat converter, FrameRecorder recorder, Mat combinedMat, int length);
}
