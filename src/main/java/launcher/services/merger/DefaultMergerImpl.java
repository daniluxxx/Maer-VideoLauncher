package launcher.services.merger;

import launcher.model.InputVideo;
import launcher.services.merger.specialMergers.*;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.OpenCVFrameConverter;
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

    @Override
    public File getOutputFile() {
        return outputFile;
    }

    @Override
    public void merge(List<InputVideo> videos, String address) {
        //ОБЩАЯ ЧАСТЬ
        int resultWidth = getResultWidth(address);
        int resultHeight = getResultHeight(address);
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        String fileName = videos.get(0).getAddress() + "_" + resultWidth + "x" + resultHeight;
        File outFile = new File(fileName);
        FrameRecorder recorder = new FFmpegFrameRecorder(outFile, resultWidth, resultHeight);
        recorder.setFrameRate(25);
        int maxBitrate = videos.stream().map(InputVideo::getBitrate)
                .max(Integer::compare).get();
        recorder.setVideoBitrate(10000000);
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder.setFormat("mp4");
        recorder.setAudioBitrate(0);


        Mat combinedMat = new Mat(resultHeight, resultWidth, CvType.CV_8UC3);

        try {
            recorder.start();
        } catch (FrameRecorder.Exception e) {
            throw new RuntimeException(e);
        }

        int length = videos.get(0).getVideoLength();
        specialMerger = getSpecialMerger(address); //здесь выбирается нужный обработчик видео в зависимости от МФ.
        //КОНЕЦ ОБЩЕЙ ЧАСТИ

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
            case "RivieraHorizontal" : return new RivieraHorizontalMerger();
            case "Kashirka" : return new KashirkaMerger();
            case "RivieraVertical" : return new RvrVerticalMerger();
            case "Okeania" : return new OkeaniaMerger();
            case "Velozavodskaya" : return new VelozavodskayaMerger();
            case "Atlantis" : return new AtlantisMerger();
            case "Konstruktor" : return new KonstruktorMerger();
            case "Khorosho" : return new KhoroshoMerger();
        }
        return null;
    }

    private int getResultWidth(String address) {
        switch (address) {
            case "RivieraHorizontal" : return 1952;
            case "RivieraVertical" : return 768;
            case "Kashirka" : return 3904;
            case "Okeania" :
            case "Khorosho" :
                return 1920;
            case "Velozavodskaya" : return 3008;
            case "Atlantis" : return 3168;
            case "Konstruktor" : return 960;
        }
        return 0;
    }
    private int getResultHeight(String address) {
        switch (address) {
            case "RivieraHorizontal" : return 288;
            case "RivieraVertical" : return 416;
            case "Kashirka" : return 896;
            case "Okeania" :
            case "Khorosho" :
                return 1080;
            case "Velozavodskaya" : return 960;
            case "Atlantis" : return 384;
            case "Konstruktor" : return 988;
        }
        return 0;
    }

}
