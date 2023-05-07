package launcher.services.merger.specialMergers;

import launcher.model.InputVideo;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.Mat;
import org.opencv.core.CvType;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.List;

public class RvrVerticalMerger implements SpecialMerger {
    private int frameCount = 0;

    @Override
    public void merge(List<InputVideo> videos, OpenCVFrameConverter.ToMat converter, FrameRecorder recorder, Mat combinedMat, int length) {
            /*if (videos.size() == 4) {
                Java2DFrameConverter frameConverter = new Java2DFrameConverter();//test
                while (frameCount < length) {
                    for (InputVideo video : videos) {
                        FFmpegFrameGrabber grabber = video.getGrabber();
                        Frame frame;
                        Frame rotatedFrame;
                        try {
                            frame = grabber.grabFrame();
                            //rotatedFrame = frame.clone();
                            BufferedImage image = frameConverter.getBufferedImage(frame);
                            AffineTransform tx = new AffineTransform();
                            tx.rotate(Math.toRadians(90), image.getWidth() / 2, image.getHeight() / 2);
                            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
                            BufferedImage rotatedImage = op.filter(image, null);
                            rotatedFrame = frameConverter.getFrame(rotatedImage);

                        } catch (FrameGrabber.Exception e) {
                            throw new RuntimeException(e);
                        }
                        Mat mat = new Mat (converter.convert(rotatedFrame)); //test
                        int startWidth;
                        if (videos.indexOf(video) == 0) {
                            startWidth = 0;
                        } else {
                            startWidth = videos.get(videos.indexOf(video) - 1).getWidth();
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
                }
                frameCount++;
            }*/
            /*else*/ if (videos.size() == 1) {
                while (frameCount < length) {
                    InputVideo video = videos.get(0);
                    //FFmpegFrameGrabber grabber = video.getGrabber();
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
            System.out.println("!!!!!!DONE!!!!!");

            } else if (videos.size() == 2) {
                System.out.println("Дать пользователю возможность определить расположение экранов в веб-интерфейсе"); //TODO сделать
            }


        /*while (frameCount<length) {
            Frame frame = null;
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
        }*/

    }
}
