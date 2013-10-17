
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.web.WebEngine;

/**
 *
 * @author kamizono
 */
public class ConvertService extends Service {

    private byte[] lines;
    private final Lock lock = new ReentrantLock();
    public String command;

    public ConvertService() {
    }

    public void load(WebEngine webEngine) {

        if (lock.tryLock()) {
            try {
                String result = new String(lines, "UTF-8");
                webEngine.loadContent(result);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(ConvertService.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                lock.unlock();
            }
        } else {
            // perform alternative actions
        }
    }

    private byte[] readAll(InputStream inputStream) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        while (true) {
            int len = inputStream.read(buffer);
            if (len < 0) {
                break;
            }
            bout.write(buffer, 0, len);
        }
        return bout.toByteArray();
    }

    @Override
    protected Task createTask() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                if (lock.tryLock()) {
                    try {
                        Process process = null;
                        try {
                            process = Runtime.getRuntime().exec(command);
                        } catch (IOException ex) {
                            Logger.getLogger(BrowserViewController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        lines = readAll(process.getInputStream());

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
