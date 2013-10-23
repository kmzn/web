
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;



/**
 *
 * @author hide
 */
public class CommandHistory {
    private static final String filePath = "command_history";
    private static CommandHistory instance = new CommandHistory();
    private LinkedHashSet<String> commands = new LinkedHashSet<>();
    private FileReaderService fileReaderService = new FileReaderService();
    // １行目は必ず履歴の個数
    public int commandNumber = 100;
    
    private CommandHistory() {
        fileReaderService.filePath = filePath;
        File file = new File(filePath);
        if (file == null) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(CommandHistory.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
        }
        fileReaderService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
          
                try {
                    String ret = System.getProperty("line.separator");
                    String[] lines = new String(fileReaderService.getLines(), "UTF-8").split(ret);
                    commandNumber = Integer.parseInt(lines[0]);
                    final int len = lines.length;
                    for (int i = 1; i < len; ++i) {
                        commands.add(lines[i]);
                    }
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(CommandHistory.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        fileReaderService.restart();
    }
    
    // インスタンス取得メソッド
    public static CommandHistory getInstance() {
        return instance;
    }
}
