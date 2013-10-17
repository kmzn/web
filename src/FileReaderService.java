
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.TextArea;

/**
 *
 * @author kamizono
 */
public class FileReaderService extends Service {

    private String result;
    private final Lock lock = new ReentrantLock();
    public String filePath;

    public FileReaderService() {
    }

    public void load(TextArea textArea) {
        if (lock.tryLock()) {
            try {
                textArea.setText(result);
            } finally {
                lock.unlock();
            }
        } else {
            // perform alternative actions
        }
    }

    @Override
    protected Task createTask() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                if (lock.tryLock()) {
                    try {
                        byte[] lines = Files.readAllBytes(Paths.get(filePath));
                        result = new String(lines, "UTF-8");
                    } finally {
                        lock.unlock();
                    }
                } else {
                    // perform alternative actions
                }

                return null;
            }
        };
        return task;
    }
}
