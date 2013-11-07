
import java.awt.Desktop;
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
import static javafx.scene.input.KeyCode.S;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;

public class BrowserViewController implements Initializable {

    @FXML
    private BorderPane root;
    @FXML
    private WebView webView;
    @FXML
    private TextArea editArea;
    @FXML
    private TextField commandField;
    private CommandFieldHandler commandFieldHandler = new CommandFieldHandler();
    private WebEngine webEngine;
    private WebHistory webHistory;
    private ConvertService convertService = new ConvertService();
    private FileReaderService fileReaderService = new FileReaderService();
    private Boolean isFileLoading = false;
    private String mdFilePath = null;
    
    private Boolean isConvertCancel = false;

    @FXML
    public void chooseFile(ActionEvent event) {

        FileChooserManager.getInstance().changeMode(FileChooserManager.Mode.MD);
        File importFile = FileChooserManager.getInstance().showDialog();
        if (importFile != null) {

            isFileLoading = true;
            mdFilePath = importFile.getAbsolutePath();

            convertService.filePath = mdFilePath;
            convertService.restart();

            fileReaderService.filePath = mdFilePath;
            fileReaderService.restart();

            final String tempPath = mdFilePath.replaceAll("\\.md", "_temp.md");
            TempFileDeleter.getInstance().add(tempPath);
            TempFileDeleter.getInstance().add(tempPath.replaceAll("\\.md", ".html"));
        } else {
            mdFilePath = null;
        }
    }

    @FXML
    public void load(ActionEvent event) {
        convertService.userCommand = commandFieldHandler.getText(commandField);
        if (mdFilePath != null) {
            convertService.filePath = mdFilePath;
            convertService.restart();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

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

                // ファイルローディング中のテキスト変更は無視
                if (isFileLoading) {
                    return;
                }
                if (isConvertCancel) {
                    return;
                }
                if (mdFilePath == null) {
                    return;
                }

                byte[] bytes = editArea.getText().getBytes();
                final String tempPath = mdFilePath.replaceAll("\\.md", "_temp.md");
                Path dest = Paths.get(tempPath);
                try {
                    Files.write(dest, bytes);
                } catch (IOException ex) {
                    Logger.getLogger(BrowserViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
                // 保存したtemp.mdをhtmlに変換して表示
                convertService.filePath = tempPath;
                convertService.restart();


            }
        });
        editArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                isConvertCancel = false;
                switch (e.getCode()) {
                    // 削除するときはHTML変換しなくていい
                    case BACK_SPACE:
                        isConvertCancel = true;
                        break;
                    case SPACE:
                        isConvertCancel = true;
                        break;
                    case ENTER:
                        isConvertCancel = true;
                        break;
                    case B:
                        if (mdFilePath == null) {
                            return;
                        }
                        if (e.isControlDown()) {
                            Desktop desktop = Desktop.getDesktop();
                            try {
//System.out.println(" " + new URI(mdFilePath).toString());
                                desktop.browse(new File(mdFilePath.replaceAll("\\.md", ".html")).toURI());
                            } catch (IOException ex) {
                                Logger.getLogger(BrowserViewController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                        break;

                    case O:
                        if (e.isControlDown()) {
                            FileChooserManager.getInstance().changeMode(FileChooserManager.Mode.MD);
                            File importFile = FileChooserManager.getInstance().showDialog();
                            if (importFile != null) {

                                isFileLoading = true;
                                mdFilePath = importFile.getAbsolutePath();

                                convertService.filePath = mdFilePath;
                                convertService.restart();

                                fileReaderService.filePath = mdFilePath;
                                fileReaderService.restart();

                                final String tempPath = mdFilePath.replaceAll("\\.md", "_temp.md");
                                TempFileDeleter.getInstance().add(tempPath);
                                TempFileDeleter.getInstance().add(tempPath.replaceAll("\\.md", ".html"));
                            } else {
                                mdFilePath = null;
                            }
                        }
                        break;
                    case N:
                        // create new file
                        if (e.isControlDown()) {
                            mdFilePath = null;
                            editArea.setText("");
                            webEngine.load("");
                        }
                        break;
                    case S:

                        if (e.isControlDown()) {
                            if (mdFilePath == null) {
                                
                                FileChooserManager.getInstance().changeMode(FileChooserManager.Mode.SAVE);
                                File file = FileChooserManager.getInstance().showDialog();

                                if (file == null) {
                                    return;
                                } else {
                                    mdFilePath = file.getAbsolutePath();
                                    //System.out.println("" + mdFilePath);
                                }

                            }
                            // md保存
                            byte[] bytes = editArea.getText().getBytes();
                            Path dest = Paths.get(mdFilePath);
                            try {
                                Files.write(dest, bytes);
                            } catch (IOException ex) {
                                Logger.getLogger(BrowserViewController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            // 保存したmdで変換したhtml保存
                            convertService.filePath = mdFilePath;
                            convertService.restart();
                        }
                        break;
                    case P:
                        if (e.isControlDown()) {
                            // saveできないのでPICTUREは使えないように
                            if (mdFilePath == null) {
                                return;
                            }
                            FileChooserManager.getInstance().changeMode(FileChooserManager.Mode.PICTURE);
                            File importFile = FileChooserManager.getInstance().showDialog();
                            if (importFile != null) {
                                try {
                                    Path p1 = Paths.get(importFile.getCanonicalPath());
                                    Path p2 = Paths.get(mdFilePath);
                                    String relPath = ResourceUtils.getRelativePath(p1.toString(), p2.toString(), "\\\\");
                                    editArea.insertText(editArea.getCaretPosition(),
                                            "\n![](file:" + relPath + ")\n");

                                } catch (IOException ex) {
                                    Logger.getLogger(BrowserViewController.class.getName()).log(Level.SEVERE, null, ex);
                                }

                            }
                        }
                        break;
                    default:
                }
            }
        });
        
        editArea.setStyle("-fx-text-fill: #44dd44;"+
                "-fx-background-color: #2d2d2d;"+
                "-fx-font: Consolas;"+
                "-fx-font-family: Consolas;"+
                "-fx-font-weight: bold;"+
                "-fx-font-size: 30;");
        
        root.setStyle("-fx-text-fill: #111111;"+
                "-fx-background-color: #212121;"+
                "-fx-font: Consolas;"+
                "-fx-font-family: Consolas;"+
                "-fx-font-weight: bold;"+
                "-fx-font-size: 20;");


        commandFieldHandler.initialize(commandField, root);
        commandFieldHandler.setOnKeyPressed(commandField);
        
        webView.prefWidthProperty().bind(Bindings.max(Bindings.subtract(root.widthProperty(), 640), 640));
        webEngine = webView.getEngine();

        // ヒストリを取得
        webHistory = webEngine.getHistory();

        // for HTML's debug
        // webEngine.executeScript("(function(F,i,r,e,b,u,g,L,I,T,E){if(F.getElementById(b))return;E=F[i+'NS']&&F.documentElement.namespaceURI;E=E?F[i+'NS'](E,'script'):F[i]('script');E[r]('id',b);E[r]('src',I+g+T);E[r](b,u);(F[e]('head')[0]||F[e]('body')[0]).appendChild(E);E=new Image;E[r]('src',I+L);})(document,'createElement','setAttribute','getElementsByTagName','FirebugLite','4','firebug-lite.js','releases/lite/latest/skin/xp/sprite.png','https://getfirebug.com/','#startOpened');");

    }
}
