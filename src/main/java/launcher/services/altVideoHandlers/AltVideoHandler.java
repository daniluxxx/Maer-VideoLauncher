package launcher.services.altVideoHandlers;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.springframework.web.multipart.MultipartFile;

public interface AltVideoHandler {
    FFmpegFrameGrabber handleVideo(MultipartFile file);
}
