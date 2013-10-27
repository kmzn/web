
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;

/**
 *
 * @author hide
 */
public class CommandHistory {

    private static final String filePath = "command_history";
    private static CommandHistory instance = new CommandHistory();
    private LinkedHashSet<String> commands = new LinkedHashSet<>();
    private FileReaderService fileReaderService = new FileReaderService();
    // コマンド履歴ファイルの１行目は必ず履歴の個数で、その値を代入する変数
    private int commandMax = 10;
    private int commandNumber = 0;
    private int commandIndex = 0;

    private CommandHistory() {
        fileReaderService.filePath = filePath;

    }

    public void fileReadStart(final TextField commandField) {

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
                        //System.out.println("setOnSucceeded " + lines[i]);
                    }
                    commandNumber = commands.size();
                    // コマンド履歴ファイルがあるならそれを反映させる
                    commandField.setText(lines[1]);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(CommandHistory.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        // コマンド履歴ファイルを開いて読み込む、なければ作る
        File file = new File(filePath);
        if (file == null) {
            try {
                file.createNewFile();
                // コマンド履歴がないときの最初に表示されるコマンドを登録
                commands.add(ConvertService.PANDOC);
                commandNumber = 1;
            } catch (IOException ex) {
                Logger.getLogger(CommandHistory.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            fileReaderService.restart();
        }
    }

    public void add(String command) {
        // LinkedHashSetなので同じコマンドは連続して保存されないので、そのチェック
        final int cNum = commands.size();
        commands.add(command);
        if (cNum < commands.size()) {
            ++commandNumber;
        }
        if (commandNumber > commandMax) {
            commandNumber = commandMax - 1;
        }
    }

    public String getNext() {
        if (++commandIndex > commandNumber - 1) {
            commandIndex = 0;
        }
        return getImpl();

    }

    public String getPrevious() {
        if (--commandIndex < 0) {
            commandIndex = commandNumber - 1;
        }
        return getImpl();
    }

    private String getImpl() {
        Iterator<String> setIterator = commands.iterator();
        int i = 0;
        while (setIterator.hasNext()) {
            if (i++ == commandIndex) {
                return setIterator.next();
            }
            System.out.println("getImpl : " + setIterator.next() + " i " + i + " commandIndex : " + commandIndex);
        }
        assert (false);
        return null;
    }

    public void save() {

        String res = "" + commandMax + System.getProperty("line.separator");

        String[] src = new String[commands.size()];
        Iterator<String> setIterator = commands.iterator();
        int i = 0;
        while (setIterator.hasNext()) {
            src[i++] = setIterator.next();
        }
        //for(String c : src) System.out.println(c);
        final int last = commands.size() - 1;
        int len = 0;
        if (last >= commandMax) {
            len = last - commandMax;
        }
        i = 0;
        for (int j = last; j >= len; --j) {
            //System.out.println(src[j] + "  " + j);
            //oriCommands[i++] = src[j];
            res += src[j] + System.getProperty("line.separator");
        }

        //System.out.println("" + res);
        byte[] bytes = res.getBytes();
        Path dest = Paths.get(filePath);
        try {
            Files.write(dest, bytes);
        } catch (IOException ex) {
            Logger.getLogger(BrowserViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // インスタンス取得メソッド
    public static CommandHistory getInstance() {
        return instance;
    }
}
