package launcher.controllers;

import launcher.exceptions.ListIsIncorrectException;
import launcher.model.InputVideo;
import launcher.services.merger.DefaultMergerImpl;
import launcher.services.merger.VideoMerger;
import launcher.services.sorter.Sorter;
import launcher.services.staticMaker.StaticMaker;
import launcher.validators.Validator;
import launcher.validators.VideoListValidator;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private final Sorter sorter;

    public MainController(VideoMerger videoMerger, StaticMaker staticMaker, Validator validator, Sorter sorter) {
        this.videoMerger = videoMerger;
        this.staticMaker = staticMaker;
        this.validator = validator;
        this.sorter = sorter;
    }


    @PostMapping(value = "/merge")
    public ResponseEntity<Resource> uploadVideo(@RequestParam("file") MultipartFile[] files) {

        ArrayList<InputVideo> inputVideos = new ArrayList<>();
        for (MultipartFile input : files) {
            InputVideo videoFromMultipart = new InputVideo(input);
            inputVideos.add(videoFromMultipart);
        }

        VideoListValidator videoListValidator = (VideoListValidator) validator;
        boolean isCorrect = videoListValidator.validate(inputVideos);
        if (isCorrect) {
            sorter.sortByWidth(inputVideos);
            LOGGER.info("Array is sorted");
            videoMerger.merge(inputVideos, videoListValidator.getAddress());

            File processedVideo = videoMerger.getOutputFile();
            Resource mergedVideo;
            try {
                mergedVideo = new UrlResource(processedVideo.toURI().toURL());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + mergedVideo.getFilename());
            try {
                headers.setContentLength(mergedVideo.contentLength());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            headers.setContentType(MediaType.parseMediaType("video/mp4"));
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("video/mp4"))
                    .body(mergedVideo);

        } else throw new ListIsIncorrectException();

    }

    /*@PostMapping(value = "/makeStatic")
    public void getStaticFromVideo(@RequestParam("file") MultipartFile[] files) {
        LOGGER.info("StaticVideo Handler is choosing");
        ArrayList<InputVideo> inputVideos = new ArrayList<>();
        for (MultipartFile input : files) {
            InputVideo videoFromMultipart = new InputVideo(input);
            inputVideos.add(videoFromMultipart);
        }
        if (inputVideos.size() == 1) {
            InputVideo video = inputVideos.get(0);
            staticMaker.getStaticFromVideo(video);
            getStaticFromVideo();
        }
    }*/
    @PostMapping(value = "/makeStatic")
    public ResponseEntity<?> getStaticFromVideo(@RequestParam("file") MultipartFile[] files) {
        LOGGER.info("TESTTESTTEST");
        ArrayList<InputVideo> inputVideos = new ArrayList<>();
        for (MultipartFile input : files) {
            InputVideo videoFromMultipart = new InputVideo(input);
            inputVideos.add(videoFromMultipart);
        }
        if (inputVideos.size() == 1) {
            InputVideo video = inputVideos.get(0);
            staticMaker.getStaticFromVideo(video);
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
                staticMaker.getStaticFromVideo(video);
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
        return null;
    }

    private File createZipFile(List<File> processedFiles) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String zipFileName = "processed_files_" + timeStamp + ".zip";
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
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return zipFile;
    }

    @GetMapping(value = "/download")
    public ResponseEntity<Resource> downloadVideo() {
        LOGGER.info("download is started");
        DefaultMergerImpl defaultMerger = (DefaultMergerImpl) videoMerger;
        File processedVideo = defaultMerger.getOutputFile();
        Resource mergedVideo = null;
        try {
            mergedVideo = new UrlResource(processedVideo.toURI().toURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + mergedVideo.getFilename());
        try {
            headers.setContentLength(mergedVideo.contentLength());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        headers.setContentType(MediaType.parseMediaType("video/mp4"));
        return ResponseEntity.ok()
                .headers(headers)
                .body(mergedVideo);
    }

    @GetMapping(value = "/getStaticFromVideo")
    public ResponseEntity<Resource> getStaticFromVideo() {
        LOGGER.info("StaticMaker for one video is running");
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
    }
}