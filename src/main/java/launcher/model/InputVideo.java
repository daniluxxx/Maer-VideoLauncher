package launcher.model;

import launcher.exceptions.WrongVideoException;
import launcher.services.altVideoHandlers.AltVideoHandler;
import launcher.services.altVideoHandlers.AudioRemover;
import launcher.services.staticMaker.StaticChecker;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class InputVideo {
    @Autowired
    private AltVideoHandler videoHandler;

    private final MaerResolutions[] RESOLUTIONS = MaerResolutions.values();

    private MultipartFile file;

    private int width;
    private int height;

    private int videoLength;
    private int bitrate;

    private FFmpegFrameGrabber grabber;

    private String address;
    private boolean isCorrect = false;
    private boolean hasAudio = false;

    public InputVideo(MultipartFile file) {
        this.file = file;
        try {
            this.grabber = new FFmpegFrameGrabber(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (this.grabber != null) {
            try {
                grabber.start();
            } catch (FFmpegFrameGrabber.Exception e) {
                throw new RuntimeException(e);
            }
            if (grabber.getAudioCodec() != 0 && !StaticChecker.getStaticChecker().isStatic()) {
                this.hasAudio = true;
                try {
                    grabber.stop();
                    grabber.release();
                } catch (FFmpegFrameGrabber.Exception e) {
                    throw new RuntimeException(e);
                }
                videoHandler = new AudioRemover();
                grabber = videoHandler.handleVideo(file);
                try {
                    grabber.start();
                } catch (FFmpegFrameGrabber.Exception e) {
                    throw new RuntimeException(e);
                }
            }
            this.width = grabber.getImageWidth();
            this.height = grabber.getImageHeight();
            this.videoLength = grabber.getLengthInVideoFrames();
            this.bitrate = grabber.getVideoBitrate();
        }

        //проверка видео на соответствие разрешению из списка
        for (MaerResolutions res : RESOLUTIONS) {
            if ((width + " " + height).equals(res.getResolution())) {
                this.isCorrect = true;
                this.address = res.getAddress();
                break;
                //TODO выбросить эксепшн
            }
        }

    }

    public MultipartFile getMultipartFile() {
        return file;
    }


    public int getWidth() {
        return width;
    }


    public int getHeight() {
        return height;
    }


    public int getVideoLength() {
        return videoLength;
    }

    public void setVideoLength(int videoLength) {
        this.videoLength = videoLength;
    }

    public int getBitrate() {
        return bitrate;
    }


    public FFmpegFrameGrabber getGrabber() {
        return grabber;
    }
    public void setGrabber(FFmpegFrameGrabber grabber) {
        this.grabber = grabber;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public String getAddress() {
        return address;
    }

    public boolean isHasAudio() {
        return hasAudio;
    }

    public AltVideoHandler getVideoHandler() {
        return videoHandler;
    }

    public void setVideoHandler(AltVideoHandler videoHandler) {
        this.videoHandler = videoHandler;
    }


}

