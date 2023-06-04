package launcher.services.sorter;

import launcher.model.InputVideo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BlockForOneAddress {
    private ArrayList<InputVideo> currentBlock = new ArrayList<>();
    private boolean isFull = false;
    private int amountOfScreens;
    private String currentAddress;

    public void add(InputVideo video) {
        if(isFull) {
            return;
        }
        else if(currentBlock.isEmpty()) {
            currentBlock.add(video);
            currentAddress = video.getAddress();
            amountOfScreens = getAmountOfScreens(video);
        }
        else {
            boolean correctVideo = true;
            if (!video.getAddress().equals(currentAddress)) {
                correctVideo = false;
                return;
            }
            for (InputVideo v : currentBlock) {
                if (video.getWidth() == v.getWidth() && !video.getAddress().equals("RivieraVertical")) {
                    correctVideo = false;
                    break;
                }
            }
            if (correctVideo) currentBlock.add(video);
        }
        if (currentBlock.size() == amountOfScreens) isFull = true;
    }

    public void clear() {
            currentBlock.clear();
            isFull = false;
            amountOfScreens = 0;
            currentAddress = "";
    }

    public boolean isFull() {
        return isFull;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }
    public List<InputVideo> getBlock() {
        return currentBlock;
    }
    public int getSize() {
        return currentBlock.size();
    }

    private int getAmountOfScreens(InputVideo video) {
        return switch (video.getAddress()) {
            case "Konstruktor" -> 1;
            case "Atlantis", "Velozavodskaya", "Kashirka", "Khorosho", "RivieraHorizontal" -> 2;
            case "Okeania" -> 3;
            case "RivieraVertical" -> 4;
            default -> 0;
        };
    }
}
