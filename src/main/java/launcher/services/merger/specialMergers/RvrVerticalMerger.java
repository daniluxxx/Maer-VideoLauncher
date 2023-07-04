package launcher.services.merger.specialMergers;

import launcher.model.InputVideo;
import launcher.services.altVideoHandlers.AltVideoHandler;
import launcher.services.altVideoHandlers.AudioRemover;
import org.bytedeco.javacv.*;
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
                        frame = video.getGrabber().grabFrame();

                    } catch (FrameGrabber.Exception e) {
                        throw new RuntimeException(e);
                    }
                    Mat mat = null;
                    try {
                        mat = new Mat(converter.convert(frame));
                    } catch (NullPointerException e) {
                        try {
                            video.getGrabber().stop();
                            video.getGrabber().release();
                        } catch (FFmpegFrameGrabber.Exception ex) {
                            throw new RuntimeException(ex);
                        }
                        if (video.getVideoHandler() == null) {
                            video.setVideoHandler(new AudioRemover());
                        }
                        AltVideoHandler videoHandler = video.getVideoHandler();
                        FFmpegFrameGrabber newGrabber = videoHandler.handleVideo(video.getMultipartFile());
                        video.setGrabber(newGrabber);
                        try {
                            video.getGrabber().start();
                            frame = video.getGrabber().grabFrame();
                        } catch (FrameGrabber.Exception ex) {
                            throw new RuntimeException(ex);
                        }
                        mat = new Mat(converter.convert(frame)); //TODO В ЧЕМ ДЕЛО? почему nullpointerex?
                    }
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
                        Mat mat = null;
                        try {
                            mat = new Mat(converter.convert(frame));
                        } catch (NullPointerException e) {
                            try {
                                video.getGrabber().stop();
                                video.getGrabber().release();
                            } catch (FFmpegFrameGrabber.Exception ex) {
                                throw new RuntimeException(ex);
                            }
                            if (video.getVideoHandler() == null) {
                                video.setVideoHandler(new AudioRemover());
                            }
                            AltVideoHandler videoHandler = video.getVideoHandler();
                            FFmpegFrameGrabber newGrabber = videoHandler.handleVideo(video.getMultipartFile());
                            video.setGrabber(newGrabber);
                            try {
                                video.getGrabber().start();
                                frame = video.getGrabber().grabFrame();
                            } catch (FrameGrabber.Exception ex) {
                                throw new RuntimeException(ex);
                            }
                            mat = new Mat(converter.convert(frame));
                        }
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
