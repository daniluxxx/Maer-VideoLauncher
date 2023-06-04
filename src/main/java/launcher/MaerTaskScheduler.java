package launcher;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class MaerTaskScheduler {
    @Scheduled(cron = "0 0 5 * * ?")
    public void deleteProcessedFiles() {
        System.out.println("Deleting is starting...");
        String directoryPath = "Processed Files";

        // Получаем путь к папке
        Path directory = Paths.get(directoryPath);

        // Проверяем, существует ли папка
        if (Files.exists(directory)) {
            // Получаем список файлов в папке
            try {
                Files.walk(directory)
                        .filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (Exception e) {
                // Обработка ошибок удаления файлов
                e.printStackTrace();
            }
        }
    }
}
