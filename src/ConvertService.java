
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
                try (BufferedReader in = new BufferedReader(new InputStreamReader(process
                        .getInputStream()))) {
                    String line;
                    StringBuilder total = new StringBuilder();
                    while ((line = in.readLine()) != null) {
                        total.append(line);
                    }
                    result = total.toString();
                } catch (IOException ex) {
                    Logger.getLogger(BrowserViewController.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        };
        return task;
    }
}
