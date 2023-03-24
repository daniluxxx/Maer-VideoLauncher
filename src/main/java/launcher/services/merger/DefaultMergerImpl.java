package launcher.services.merger;

import launcher.model.InputVideo;
import launcher.services.merger.specialMergers.RvrHorizontalMerger;
import launcher.services.merger.specialMergers.SpecialMerger;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.Mat;
import org.opencv.core.CvType;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;


@Service
public class DefaultMergerImpl implements VideoMerger {

    private final Logger logger = Logger.getLogger(DefaultMergerImpl.class.getName());

    private File outputFile;
    private SpecialMerger specialMerger;

    public File getOutputFile() {
        return outputFile;
    }

    @Override
    public void merge(List<InputVideo> videos, String address) {
        //ОБЩАЯ ЧАСТЬ
        int resultWidth = getResultWidth(address);
        int resultHeight = getResultHeight(address);
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        String fileName =videos.get(0).getAddress() + "_" + resultWidth + "x" + resultHeight + ".mp4";
        File outFile = new File(fileName);
        FrameRecorder recorder = new FFmpegFrameRecorder(outFile, resultWidth, resultHeight);
        recorder.setFrameRate(25);
        int maxBitrate = videos.stream().map(InputVideo::getBitrate)
                .max(Integer::compare).get();
        logger.info(String.valueOf(maxBitrate)); //TODO удалить
        recorder.setVideoBitrate(maxBitrate); // выбрать максимальный из списка видео
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder.setAudioBitrate(0);
        Mat combinedMat = new Mat(resultHeight, resultWidth, CvType.CV_8UC3); // TODO убрать константы

        try {
            recorder.start();
        } catch (FrameRecorder.Exception e) {
            throw new RuntimeException(e);
        }

        int length = videos.get(0).getVideoLength();
        specialMerger = getSpecialMerger(address);
        //КОНЕЦ ОБЩЕЙ ЧАСТИ
        /*int i = 0;
        while (i < length) {
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
            i++;
        }*/

        specialMerger.merge(videos, converter, recorder, combinedMat, length);

        for (InputVideo video : videos
             ) {
            try {
                video.getGrabber().stop();
                video.getGrabber().release();
            } catch (FFmpegFrameGrabber.Exception e) {
                throw new RuntimeException(e);
            }
        }
        try {
            recorder.stop();
        } catch (FrameRecorder.Exception e) {
            throw new RuntimeException(e);
        }
        this.outputFile = outFile; // TODO править назваие

    }

    private SpecialMerger getSpecialMerger(String address) {
        switch (address) {
            case "RivieraHorizontal" : return new RvrHorizontalMerger();
        }
        return null;
    }
    
    private int getResultWidth(String address) {
        switch (address) {
            case "RivieraHorizontal" : return 1952;
            //add other cases
        }
        return 0;
    }
    private int getResultHeight(String address) {
        switch (address) {
            case "RivieraHorizontal" : return 288;
        }
        return 0;
    }

}
