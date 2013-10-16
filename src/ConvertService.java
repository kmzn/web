
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
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

    private String result;
    public String command;
    
    public ConvertService() {
    }
    
    public void load(WebEngine webEngine) {
        webEngine.loadContent(result);
    }

    @Override
    protected Task createTask() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                Process process = null;
                try {
                    process = Runtime.getRuntime().exec(command);
                } catch (IOException ex) {
                    Logger.getLogger(BrowserViewController.class.getName()).log(Level.SEVERE, null, ex);

                }
                
                try (DataInputStream in =  new DataInputStream(
                        new BufferedInputStream(process.getInputStream())) ) {
                    
                    String line = new String();
                    int readByte = 0, totalByte = 0;
                    byte[] b = new byte[1024];
                    while(-1 != (readByte = in.read(b))){
                        String xx = new String(b, "UTF-8");
                        System.out.println(xx.length());
                        line += xx;
                        System.out.println("Read: " + readByte + " Total: " + totalByte);
                    }
                    result = line;
                }
                catch (IOException ex) {
                    Logger.getLogger(BrowserViewController.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        };
        return task;
    }
}
