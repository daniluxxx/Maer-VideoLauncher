package launcher;

import org.bytedeco.ffmpeg.avcodec.AVCodec;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.Mat;
import org.opencv.core.CvType;

public class TEST {
    public static void main(String[] args) throws FrameGrabber.Exception, FrameRecorder.Exception {
        //String input = "C:\\Users\\danil\\OneDrive\\Рабочий стол\\VideoBuilder\\VideoSplitter\\Merger\\rvrVertical\\test1\\Comp 1.mp4";
        //String input = "C:\\Users\\danil\\OneDrive\\Рабочий стол\\VideoBuilder\\VideoSplitter\\Merger\\rvrVertical\\NPR_Znak_Nauka_Ривьера192.mp4";
        String input = "C:\\Users\\danil\\OneDrive\\Рабочий стол\\VideoBuilder\\VideoSplitter\\Merger\\rvrVertical\\Egor 192х416.mp4";
        //String input = "C:\\Users\\danil\\OneDrive\\Рабочий стол\\VideoBuilder\\VideoSplitter\\Merger\\rvrVertical\\4\\Рвр_192_1.mp4";
        String output = "C:\\Users\\danil\\OneDrive\\Рабочий стол\\VideoBuilder\\VideoSplitter\\Merger\\rvrVertical\\out.mp4";

        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(input);
        grabber.start();

        int width = grabber.getImageWidth();
        int height = grabber.getImageHeight();
        System.out.printf("Dimension of video is %d x %d \r", width, height);

        int outputWidth = 768;
        int outputHeight = 416;
        int frameCount = grabber.getLengthInVideoFrames();
        int count = 0;

        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        FrameRecorder recorder = new FFmpegFrameRecorder(output, outputWidth, outputHeight);
        //recorder.setVideoBitrate(grabber.getVideoBitrate());
        recorder.setVideoBitrate(10000000);
        recorder.setFrameRate(25);
        recorder.setFormat("mp4");
        recorder.setAudioBitrate(0);
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);

        Mat resultMat = new Mat(416, 768, CvType.CV_8UC3); //TODO указать CVType важно! без него не работает

        recorder.start();
        while (count<frameCount) {
            Frame frame = grabber.grabFrame();
            Mat mat = new Mat(converter.convert(frame));
            System.out.println(mat.size());
            System.out.println(resultMat.size());
            int startWidth = 0;
            int endWidth = 192;
            for (int i = 0; i < 4; i++) {
                mat.copyTo(resultMat.colRange(startWidth, endWidth));
                startWidth += 192;
                endWidth += 192;
            }
            Frame outputFrame = converter.convert(resultMat);
            recorder.record(outputFrame);
            count++;
        }
        grabber.stop();
        grabber.release();
        recorder.stop();
    }
}
