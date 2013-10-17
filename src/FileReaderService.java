
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

    private String result;
    private final Lock lock = new ReentrantLock();
    public File file;
    
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
                        try (DataInputStream in = new DataInputStream(
                                new BufferedInputStream(new FileInputStream(file)))) {

                            String line = new String();
                            int readByte = 0, totalByte = 0;
                            byte[] b = new byte[1024 * 1024];
                            while (-1 != (readByte = in.read(b))) {
                                String xx = new String(b, "UTF-8");
                                line += xx;
                                //System.out.println("Read: " + readByte + " Total: " + totalByte);
                            }
                            result = line;
                        } catch (IOException ex) {
                            Logger.getLogger(BrowserViewController.class.getName()).log(Level.SEVERE, null, ex);
                        }
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
