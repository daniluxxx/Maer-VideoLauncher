package launcher.services.staticMaker;

import launcher.model.InputVideo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class StaticMaker {
    private File outputFile;

    public File getOutputFile() {
        return outputFile;
    }

    public void getStaticFromVideo(InputVideo video, Integer frameNum) {
        String resolution = String.format("%sx%s", video.getWidth(), video.getHeight());
        int staticFrame = video.getVideoLength()-1;
        System.out.println(staticFrame);//test
        if (frameNum != null) {
            staticFrame = frameNum;
        }
        String timeStamp = new SimpleDateFormat("ddMM_HHmmss").format(new Date());
        String fileName = video.getAddress() + "_" + video.getWidth() + "x" + video.getHeight() + "_" + timeStamp + "_Night.mp4";
        String outputFile = "Processed Files\\" + fileName;
        MultipartFile file = video.getMultipartFile();
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
        String variableFilter = String.format("[0:v]select='eq(n,%s)',setpts=PTS-STARTPTS[selected];[selected]loop=-1:1500,fade=in:0:250,fade=out:1250:250", staticFrame);
        String[] cmd = {
                "ffmpeg",
                "-i", inputFile,

                "-vf", variableFilter,

                "-framerate", "25",
                "-frames:v", "1500",
                "-an", // audio off
                "-c:v", "libx264",
                "-preset", "slow",
                "-crf", "18",
                "-threads", "4",
                outputFile
        };

        ProcessBuilder processBuilder = new ProcessBuilder(cmd);
        processBuilder.redirectErrorStream(false);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
        Process process = null;
        try {
            process = processBuilder.start();
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            // Reading errors FFmpeg
            InputStream errorStream = process.getErrorStream();
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
            String errorLine;
            while ((errorLine = errorReader.readLine()) != null) {
                System.err.println(errorLine);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("FFmpeg process failed with exit code: " + exitCode);
            }
            this.outputFile = new File(outputFile);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (process != null) {
                    process.getOutputStream().close();
                    process.getInputStream().close();
                    process.getErrorStream().close();
                    process.destroy();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (tempFile != null) {
                tempFile.delete();
            }
        }
    }
}
