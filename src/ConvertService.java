
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

    static final String PANDOC = "pandoc -s -f markdown -t html5 --highlight-style=tango ";
    private final Lock lock = new ReentrantLock();
    public String filePath;

    public ConvertService() {
    }

    public void load(WebEngine webEngine) {

        if (lock.tryLock()) {
            try {
                
                final String htmlPath = filePath.replaceAll("\\.md", ".html");
                webEngine.load("file:" + htmlPath);
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

                        final String htmlPath = filePath.replaceAll("\\.md", ".html");
                        final String command = PANDOC + filePath + " -o " + htmlPath;
                        Process p = Runtime.getRuntime().exec(command);
                        p.waitFor();
                        
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
