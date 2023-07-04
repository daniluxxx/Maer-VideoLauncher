package launcher.services.altVideoHandlers;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class AudioRemover implements AltVideoHandler {

    @Override
    public FFmpegFrameGrabber handleVideo(MultipartFile file) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("temp", file.getOriginalFilename());
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(file.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String inputFile = tempFile.getAbsolutePath();
        String timeStamp = new SimpleDateFormat("ddMM_HHmmss").format(new Date());
        String fileName = "no_audio_" + timeStamp + ".mp4";
        String outputFile = "Processed Files\\" + fileName;

        String[] cmd = {
                "ffmpeg",
                "-i", inputFile,
                "-map_metadata", "-1", //!metadata fix
                "-f", "mp4",
                "-an", // !audio off
                "-c:v", "libx264",
                "-preset", "veryslow",
                "-brand", "mp42",
                "-crf", "18",
                outputFile
        };

        try {
            Process process = new ProcessBuilder(cmd).start();
            process.getOutputStream().close();
            process.getInputStream().close();
            process.getErrorStream().close();
            process.waitFor();
            process.destroy();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (tempFile != null) {
            tempFile.delete();
        }
        return new FFmpegFrameGrabber(outputFile);
    }
}
