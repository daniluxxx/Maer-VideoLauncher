package launcher.services.sorter;

import launcher.model.InputVideo;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class Sorter {
    public List<InputVideo> sortByWidth(List<InputVideo> inputVideos) {

        //сортировка видео по ширине
        inputVideos.stream()
                .sorted(Comparator.comparingInt(v -> v.getWidth())).collect(Collectors.toList());
        return inputVideos;
    }
}
