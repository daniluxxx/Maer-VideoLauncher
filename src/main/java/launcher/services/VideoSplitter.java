package launcher.services;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;

public class VideoSplitter {

    public static void main(String[] args) throws FFmpegFrameGrabber.Exception, FrameRecorder.Exception {

        String videoFile = "C:\\Users\\danil\\OneDrive\\Рабочий стол\\VideoBuilder\\VideoSplitter\\t.mp4";

        //параметры 1го видео
        String outputFile1 = "C:\\Users\\danil\\OneDrive\\Рабочий стол\\VideoBuilder\\VideoSplitter\\out1.mp4";
        int outputWidth1 = 1312;
        int outputHeight1 = 288;

        //параметры 2го видео
        String outputFile2 = "C:\\Users\\danil\\OneDrive\\Рабочий стол\\VideoBuilder\\VideoSplitter\\out2.mp4";
        int outputWidth2 = 640;
        int outputHeight2 = 288;

        //чтение видео
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoFile);
        grabber.start();

        int originalFrameRate = (int) grabber.getFrameRate();
        int originalBitrate = grabber.getVideoBitrate();

        //настройки 1го видео
        FrameRecorder recorder1 = new FFmpegFrameRecorder(outputFile1, outputWidth1, outputHeight1);
        recorder1.setFrameRate(originalFrameRate);
        recorder1.setVideoBitrate(originalBitrate);
        recorder1.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder1.setAudioBitrate(0);
        recorder1.start();

        //настройки 2го видео
        FrameRecorder recorder2 = new FFmpegFrameRecorder(outputFile2, outputWidth2, outputHeight2);
        recorder2.setFrameRate(originalFrameRate);
        recorder2.setVideoBitrate(originalBitrate);
        recorder2.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder2.setAudioBitrate(0);
        recorder2.start();

        //Convert frames to Mat objects
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        Mat mat = new Mat();

        Frame frame;
        while ((frame = grabber.grab()) != null) {
            mat = converter.convert(frame);

            //видео1
            Rect roi1 = new Rect(0,0, 1312, 288);
            Mat croppedMat1 = mat.apply(roi1);
            Frame outputFrame1 = converter.convert(croppedMat1);
            recorder1.record(outputFrame1);
            //видео2
            Rect roi2 = new Rect(1312, 0, 640, 288);
            Mat croppedMat2 = mat.apply(roi2);
            Frame outputFrame2 = converter.convert(croppedMat2);
            recorder2.record(outputFrame2);
        }

        grabber.stop();
        grabber.release();
        recorder1.stop();
        recorder2.stop();

    }
}
