
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class BrowserViewController implements Initializable {
    @FXML
    private BorderPane root;
    @FXML
    private TextField urlField;
    @FXML
    private WebView webView;
    @FXML
    private TextArea editArea;
    @FXML
    private Button button;
    private FileChooser fileChooser = new FileChooser();
    private WebEngine webEngine;
    private WebHistory webHistory;
    
    private ConvertService convertService = new ConvertService();
    private FileReaderService fileReaderService = new FileReaderService();
    
    static String PANDOC = "pandoc -s -f markdown -t html5 --highlight-style=tango ";
    
    @FXML
    public void chooseFile(ActionEvent event) {
        File importFile = fileChooser.showOpenDialog(null);
        if (importFile != null) {
            
            convertService.command = PANDOC + importFile.getAbsolutePath();
            System.out.println(convertService.command);
            convertService.restart();
            
            fileReaderService.file = importFile;
            fileReaderService.restart();
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
    
    private void showDialog(String message) {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Alert");
        dialog.setResizable(false);      

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        Label messageLabel = new Label(message);
        messageLabel.setFont(Font.font(null, 16.0));
        root.setCenter(messageLabel);
        BorderPane.setAlignment(messageLabel, Pos.CENTER);
        
        Button closeButton = new Button("OK");
        closeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                dialog.close();
            }
        });
        
        root.setBottom(closeButton);
        BorderPane.setAlignment(closeButton, Pos.CENTER);
        
        Scene scene = new Scene(root);
        dialog.setScene(scene);
        
        dialog.show();
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
                fileReaderService.load(editArea);
            }
            
        });
        
        // テキストフィールドの幅をボーダペインの幅にバインドする
        urlField.prefWidthProperty().bind(Bindings.max(Bindings.subtract(root.widthProperty(), 200), 200));

        webEngine = webView.getEngine();
        
        // ヒストリを取得
        webHistory = webEngine.getHistory();
        
        // JavaScriptのalertがコールされた場合のイベント処理
        webEngine.setOnAlert(new EventHandler<WebEvent<String>>() {
            @Override
            public void handle(WebEvent<String> event) {
               showDialog(event.getData());
            }
        });
        
        // ページのロードの状態に応じた処理
        Worker<Void> worker = webEngine.getLoadWorker();
        worker.stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> value, 
                                 Worker.State oldState, Worker.State newState) {
                if (newState == Worker.State.SUCCEEDED) {
                    // ページのロードに成功したらURLをテキストフィールドに表示する
                    urlField.setText(webEngine.getLocation());
                } else if (newState == Worker.State.FAILED) {
                    
                }
            }
        });
    }
}
