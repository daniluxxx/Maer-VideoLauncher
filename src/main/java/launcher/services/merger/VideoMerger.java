package launcher.services.merger;

import launcher.model.InputVideo;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface VideoMerger {
    void merge(List<InputVideo> file, String address);
}
