
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
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
    public int commandMax = 10;
    public int commandNumber = 0;
    
    private CommandHistory() {
        fileReaderService.filePath = filePath;
        File file = new File(filePath);
        Boolean isNew = false;
        if (file == null) {
            try {
                isNew = true;
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
                    commandMax = Integer.parseInt(lines[0]);
                    final int len = lines.length;
                    for (int i = 1; i < len; ++i) {
                        commands.add(lines[i]);
                    }
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(CommandHistory.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        if ( ! isNew)
            fileReaderService.restart();
    }
    
    public void add(String command, String[] oriCommands) {
        //System.out.println("add " + command + "  "  + commandNumber);
        int cNum = commands.size();
        commands.add(command);
        if (cNum < commands.size()) {
            ++commandNumber;
        }
        if (commandNumber > commandMax) {
            commandNumber = commandMax - 1;
        }
        //System.out.println("add " + command + "  "  + commandNumber);
        String[] src = new String[commands.size()];
        Iterator<String> setIterator = commands.iterator();
        int i = 0;
        while (setIterator.hasNext()) {
            //
            src[i++] = setIterator.next();
            
        }
        //for(String c : src) System.out.println(c);
        final int last = commands.size()-1;
        int len = 0;
        if (last >= commandMax) len = last - commandMax;
        i = 0;
        for (int j = last; j >= len; --j) {
            System.out.println(src[j] + "  " + j);
            oriCommands[i++] = src[j];
        }
        
    }

    public String[] get() {
        String[] ret = null;
        if (commands.size() == 0) {
            ret = new String[commandMax];
            ret[0] = ConvertService.PANDOC;
            commands.add(ConvertService.PANDOC);
            commandNumber = 1;
        } else {
            ret = new String[commands.size()];
            Iterator<String> setIterator = commands.iterator();
            int i = 0;
            while (setIterator.hasNext()) {
                //System.out.println(setIterator.next());
                ret[i++] = setIterator.next();
            }
            commandNumber = i;

        }
        return ret;
    }
    
    // インスタンス取得メソッド
    public static CommandHistory getInstance() {
        return instance;
    }
}
