package launcher.validators;

import launcher.model.InputVideo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VideoListValidator implements Validator {
    private String address;

    public String getAddress() {
        return address;
    }

    @Override
    public boolean validate(List<InputVideo> list) {
        boolean isCorrect = list.stream().allMatch(v -> v.getAddress().equals(list.get(0).getAddress()));
        if(isCorrect) {
            address = list.get(0).getAddress();
        }
        return isCorrect;
    }
}
