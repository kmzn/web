
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class HtmlBrowser extends Application {
    /**
     *
     * @param stage
     * @throws IOException
     */
    @Override
    public void start(Stage stage) throws IOException {
        BorderPane root = FXMLLoader.load(getClass().getResource("BrowserView.fxml"));
        Scene scene = new Scene(root);
        
        stage.setTitle("HtmlBrowser");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String... args) {
        launch(args);
    }
    
    @Override
    public void stop() {
        TempFileDeleter.getInstance().deleteAll();
    }
}
