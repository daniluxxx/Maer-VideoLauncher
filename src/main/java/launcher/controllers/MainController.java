package launcher.controllers;

import launcher.model.InputVideo;
import launcher.services.merger.VideoMerger;
import launcher.services.sorter.BlockForOneAddress;
import launcher.services.staticMaker.StaticChecker;
import launcher.services.staticMaker.StaticMaker;
import launcher.validators.Validator;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
public class MainController {

    private final Logger LOGGER = Logger.getLogger(MainController.class.getName());

    private final VideoMerger videoMerger;
    private final StaticMaker staticMaker;
    private final Validator validator;
    private final BlockForOneAddress currentBlock;

    public MainController(VideoMerger videoMerger, StaticMaker staticMaker, Validator validator, BlockForOneAddress currentBlock) {
        this.videoMerger = videoMerger;
        this.staticMaker = staticMaker;
        this.validator = validator;
        this.currentBlock = currentBlock;
    }


    @PostMapping(value = "/merge")
    public ResponseEntity<?> uploadVideo(@RequestParam("file") MultipartFile[] files) {

        StaticChecker.getStaticChecker().setStatic(false);
        ArrayList<InputVideo> videoList = new ArrayList<>();
        for (MultipartFile input : files) {
            InputVideo videoFromMultipart = new InputVideo(input);
            videoList.add(videoFromMultipart);
        }
        List<File> processedFiles = new ArrayList<>();
        InputVideo currentVideo = null;
        while (videoList.size() > 0) {
            Iterator<InputVideo> videoIterator = videoList.iterator();
            while (videoIterator.hasNext()) {
                currentVideo = videoIterator.next();
                int currentSize = currentBlock.getSize();
                currentBlock.add(currentVideo);
                int newSize = currentBlock.getSize();
                if (newSize > currentSize) {
                    videoIterator.remove();
                    currentVideo = null;
                }
                if (currentBlock.isFull()) {
                    videoMerger.merge(currentBlock.getBlock(), currentBlock.getCurrentAddress());
                    File processedVideo = videoMerger.getOutputFile();
                    processedFiles.add(processedVideo);
                    currentBlock.clear();
                }
            }
            if (!currentBlock.isFull() && currentBlock.getSize() == 1 && currentBlock.getCurrentAddress().equals("RivieraVertical")) {
                videoMerger.merge(currentBlock.getBlock(), currentBlock.getCurrentAddress());
                File processedVideo = videoMerger.getOutputFile();
                processedFiles.add(processedVideo);
                currentBlock.clear();
            }
            if (currentVideo != null) {
                currentVideo = null;
                currentBlock.clear();
            }
        }
        Resource resource;
        if (processedFiles.size() > 0) {
            File zipFile = createZipFile(processedFiles);
            try {
                resource = new UrlResource(zipFile.toURI());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + zipFile.getName() + "\"")
                    .body(resource);
        }
        return null;//TODO вернуть ошибку

    }

    @PostMapping(value = "/makeStatic")
    public ResponseEntity<?> getStaticFromVideo(
            @RequestParam("file") MultipartFile[] files,
            @RequestParam(value = "frameNum", required = false) Integer frameNum) {
        LOGGER.info("Static in process");
        if (frameNum != null) {
            LOGGER.info(frameNum.toString());
        }
        StaticChecker.getStaticChecker().setStatic(true);
        ArrayList<InputVideo> inputVideos = new ArrayList<>();
        for (MultipartFile input : files) {
            InputVideo videoFromMultipart = new InputVideo(input);
            inputVideos.add(videoFromMultipart);
        }
        if (inputVideos.size() == 1) {
            InputVideo video = inputVideos.get(0);
            staticMaker.getStaticFromVideo(video, frameNum);
            File processedVideo = staticMaker.getOutputFile();
            Resource staticVideo = null;
            try {
                staticVideo = new UrlResource(processedVideo.toURI().toURL());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + staticVideo.getFilename());
            try {
                headers.setContentLength(staticVideo.contentLength());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            headers.setContentType(MediaType.parseMediaType("video/mp4"));
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("video/mp4"))
                    .body(staticVideo);
        } else if (inputVideos.size() > 1) {
            List<File> processedFiles = new ArrayList<>();
            for (InputVideo video : inputVideos) {
                staticMaker.getStaticFromVideo(video, frameNum);
                File processedVideo = staticMaker.getOutputFile();
                processedFiles.add(processedVideo);
            }
            File zipFile = createZipFile(processedFiles);
            Resource resource;
            try {
                resource = new UrlResource(zipFile.toURI());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + zipFile.getName() + "\"")
                    .body(resource);
        }
        StaticChecker.getStaticChecker().setStatic(false);
        return null;
    }

    private File createZipFile(List<File> processedFiles) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String zipFileName = "Processed Files\\" + "processed_files_" + timeStamp + ".zip";
        File zipFile = new File(zipFileName);
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            for (File file : processedFiles) {
                ZipEntry zipEntry = new ZipEntry(file.getName());
                zos.putNextEntry(zipEntry);
                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                }
                zos.closeEntry();
                file.delete();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return zipFile;
    }

}