package launcher.validators;

import launcher.model.InputVideo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface Validator {
    boolean validate(List<InputVideo> list);
}
