

/**
 *
 * @author kamizono
 */
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
    public File file;
    
    public FileReaderService() {
    }
    
    public void load(TextArea textArea) {
        textArea.setText(result);
    }

    @Override
    protected Task createTask() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                
                try (DataInputStream in =  new DataInputStream(
                        new BufferedInputStream(new FileInputStream(file))) ) {
                    
                    String line = new String();
                    int readByte = 0, totalByte = 0;
                    byte[] b = new byte[1024 * 1024];
                    while(-1 != (readByte = in.read(b))){
                        String xx = new String(b, "UTF-8");
                        line += xx;
                        System.out.println("Read: " + readByte + " Total: " + totalByte);
                    }
                    result = line;
                } catch (IOException ex) {
                    Logger.getLogger(BrowserViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        return task;
    }
}
