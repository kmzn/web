
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.TextArea;

/**
 *
 * @author kamizono
 */
public class FileReaderService extends Service {

    private byte[] lines;
    private final Lock lock = new ReentrantLock();
    public String filePath;

    public FileReaderService() {
    }

    public void load(TextArea textArea) {
        if (lock.tryLock()) {
            try {
                String result = new String(lines, "UTF-8");
                textArea.setText(result);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(FileReaderService.class.getName()).log(Level.SEVERE, null, ex);
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
                        lines = Files.readAllBytes(Paths.get(filePath));
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
