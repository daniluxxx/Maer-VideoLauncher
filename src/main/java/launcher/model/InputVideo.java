package launcher.model;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class InputVideo {

    private final MaerResolutions[] RESOLUTIONS = MaerResolutions.values();

    private MultipartFile file;

    private int width;
    private int height;

    private int videoLength;
    private int bitrate;

    private FFmpegFrameGrabber grabber;

    private String address;
    private boolean isCorrect = false;

    public InputVideo(MultipartFile file) {
        try {
            this.grabber = new FFmpegFrameGrabber(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (this.grabber!=null) {
            try {
                grabber.start();
            } catch (FrameGrabber.Exception e) {
                throw new RuntimeException(e);
            }
            this.width = grabber.getImageWidth();
            this.height = grabber.getImageHeight();
            this.videoLength = grabber.getLengthInVideoFrames();
            this.bitrate = grabber.getVideoBitrate();
        } else throw new RuntimeException();

        //проверка видео на соответствие разрешению из списка
        for (MaerResolutions res : RESOLUTIONS) {
            if ((width + " " + height).equals(res.getResolution())) {
                this.isCorrect = true;
                this.address = res.getAddress();
                break;
            }
        }

    }

    public MultipartFile getFile() {
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


    public boolean isCorrect() {
        return isCorrect;
    }

    public String getAddress() {
        return address;
    }
}
