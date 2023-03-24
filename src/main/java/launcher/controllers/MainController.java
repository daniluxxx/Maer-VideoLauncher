package launcher.controllers;

import launcher.exceptions.ListIsIncorrectException;
import launcher.model.InputVideo;
import launcher.services.merger.DefaultMergerImpl;
import launcher.services.merger.VideoMerger;
import launcher.services.sorter.Sorter;
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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.logging.Logger;

@RestController
public class MainController {

    private final Logger LOGGER = Logger.getLogger(MainController.class.getName());

    private final VideoMerger videoMerger;
    private final Validator validator;
    private final Sorter sorter;

    public MainController(VideoMerger videoMerger, Validator validator, Sorter sorter) {
        this.videoMerger = videoMerger;
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
            /*for (InputVideo v :
                    inputVideos) {
                System.out.println(v.getWidth());
            }*/
            videoMerger.merge(inputVideos, videoListValidator.getAddress());

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
                    .contentType(MediaType.parseMediaType("video/mp4"))
                    .body(mergedVideo);

        } else throw new ListIsIncorrectException();

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
}