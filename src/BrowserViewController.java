
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class BrowserViewController implements Initializable {
    @FXML
    private BorderPane root;
    @FXML
    private TextField urlField;
    @FXML
    private WebView webView;
    @FXML
    private TextArea editArea;
    private FileChooser fileChooser = new FileChooser();
    private WebEngine webEngine;
    private WebHistory webHistory;
    
    private ConvertService convertService = new ConvertService();
    private FileReaderService fileReaderService = new FileReaderService();
    
    private Boolean isFileLoading = false;
    private static String OutputFileName = "temp.md";
    private String mdFilePath;
    
    static String PANDOC = "pandoc -s -f markdown -t html5 --highlight-style=tango ";
    
    @FXML
    public void chooseFile(ActionEvent event) {
        File importFile = fileChooser.showOpenDialog(null);
        if (importFile != null) {
            
            convertService.command = PANDOC + importFile.getAbsolutePath();
            System.out.println(convertService.command);
            convertService.restart();
            
            fileReaderService.filePath = importFile.getAbsolutePath();
            fileReaderService.restart();
            
            isFileLoading = true;
            mdFilePath = importFile.getAbsolutePath();
       }
    }
    
    @FXML
    public void load(ActionEvent event) {
        String url = urlField.getText();
        webEngine.load(url);
    }
    
    @FXML
    public void backward(MouseEvent event) {
        // ヒストリがある場合はページを戻す
        if (webHistory.getCurrentIndex() > 0) {
            webHistory.go(-1);
        }
    }
    
    @FXML
    public void forward(MouseEvent event) {
        // ヒストリ現在位置より先にページがある場合は
        // ページを進める
        if (webHistory.getCurrentIndex() < webHistory.getEntries().size()) {
            webHistory.go(1);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // ファイル選択の設定
        fileChooser.setTitle("select markdown file");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().add(new ExtensionFilter("markdown", "*.md", "*.MD"));
        
        // 変換が終わったら反映させる
        convertService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                convertService.load(webEngine);
                isFileLoading = false;
            }
        });
        fileReaderService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                fileReaderService.load(editArea);
            }
        });
        editArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {

                // ファイルローディングのテキスト変更は無視
                if (isFileLoading) return;
                
                byte[] bytes = editArea.getText().getBytes();
                Path dest = Paths.get(OutputFileName);
                try {
                    Files.write(dest, bytes);
                } catch (IOException ex) {
                    Logger.getLogger(BrowserViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
                // 保存したtemp.mdをhtmlに変換して表示
                convertService.command = PANDOC + "temp.md";
                System.out.println(convertService.command);
                convertService.restart();
            }
        });
        editArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                switch (e.getCode()) {
                    case ENTER:
                        break;
                    case S:
                        if (e.isControlDown()) {
                            byte[] bytes = editArea.getText().getBytes();
                            Path dest = Paths.get(mdFilePath);
                            try {
                                Files.write(dest, bytes);
                            } catch (IOException ex) {
                                Logger.getLogger(BrowserViewController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        break;
                    default:
                }
            }
        });

        // テキストフィールドの幅をボーダペインの幅にバインドする
        urlField.prefWidthProperty().bind(Bindings.max(Bindings.subtract(root.widthProperty(), 200), 200));

        webEngine = webView.getEngine();
        
        // ヒストリを取得
        webHistory = webEngine.getHistory();
        
        
    }
}
