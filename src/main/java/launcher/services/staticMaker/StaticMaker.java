package launcher.services.staticMaker;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Scalar;

import static org.bytedeco.opencv.global.opencv_core.addWeighted;

public class StaticMaker {

    public static void main(String[] args) throws FFmpegFrameGrabber.Exception, FFmpegFrameRecorder.Exception {

        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber("C:\\Users\\danil\\OneDrive\\Рабочий стол\\VideoBuilder\\VideoSplitter\\StaticMaker\\t.mp4");
        grabber.start();
        int videoLength = grabber.getLengthInFrames();
        int bitrate = grabber.getVideoBitrate();
        int fadeDuration = 250;

        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder("C:\\Users\\danil\\OneDrive\\Рабочий стол\\VideoBuilder\\VideoSplitter\\StaticMaker\\output.mp4", grabber.getImageWidth(), grabber.getImageHeight());
        recorder.setFrameRate(25);
        recorder.setVideoBitrate(bitrate);
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder.setAudioBitrate(0);
        recorder.start();


        grabber.setFrameNumber(videoLength-1);
        Frame staticFrame = grabber.grab();


        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();

        double opacity = 0;

        for (int i = 0; i<1500; i++) {

            Mat mat = Java2DFrameUtils.toMat(staticFrame);

            if (i < fadeDuration) {
                //opacity = i / (double) fadeDuration;
                opacity += 0.0036;
            }
            if (i > 1250) {
                opacity -= 0.0036;
            }
            Scalar solid = new Scalar(0,0,0,0);

            Mat alpha = new Mat(mat.size(), mat.type(), solid);

            Mat result = new Mat(mat.size(), mat.type());

            addWeighted(mat, opacity, alpha, 1, 0, result);

            Frame outputFrame = converter.convert(result);

            recorder.record(outputFrame);

        }

        grabber.stop();
        grabber.release();
        recorder.stop();


    }

}
