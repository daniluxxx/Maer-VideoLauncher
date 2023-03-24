package launcher.aspects;

import launcher.services.merger.DefaultMergerImpl;
import launcher.services.merger.VideoMerger;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
@Aspect
public class DeleteFileAfterMergingAspect {

    private final VideoMerger videoMerger;

    private final Logger LOGGER = Logger.getLogger(DeleteFileAfterMergingAspect.class.getName());

    public DeleteFileAfterMergingAspect(VideoMerger videoMerger) {
        this.videoMerger = videoMerger;
    }

    @After("execution(* launcher.controllers.MainController.downloadVideo(..))")
    public void delete() throws InterruptedException {
        Thread.sleep(10000);

        LOGGER.info("Aspect is running");
        DefaultMergerImpl merger = (DefaultMergerImpl) videoMerger;
    }
}
