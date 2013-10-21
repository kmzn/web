
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
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
    public String userCommand = PANDOC;
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
                        final String command = userCommand + filePath + " -o " + htmlPath;
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
