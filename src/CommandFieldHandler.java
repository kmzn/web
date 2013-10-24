
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import static javafx.scene.input.KeyCode.BACK_SPACE;
import static javafx.scene.input.KeyCode.N;
import static javafx.scene.input.KeyCode.P;
import static javafx.scene.input.KeyCode.S;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;


/**
 *
 * @author kamizono
 */
public class CommandFieldHandler {

    private int commandIndex = 0;
    private String[] commands = CommandHistory.getInstance().get();
    public CommandFieldHandler() {
        
    }
    
    public void initialize(TextField commandField, BorderPane root) {
        commandField.setText(ConvertService.PANDOC);
        commandField.prefWidthProperty().bind(Bindings.max(Bindings.subtract(root.widthProperty(), 200), 200));
    }
    
    public String getText(TextField commandField) {
        CommandHistory.getInstance().add(commandField.getText(), commands);
        return commandField.getText();
    }
    
    public void setOnKeyPressed(final TextField commandField) {
        commandField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                
                switch (e.getCode()) {
                    case UP:
                        if (++commandIndex > CommandHistory.getInstance().commandNumber - 1) {
                            commandIndex = 0;
                        }
                        commandField.setText(commands[commandIndex]);
                        break;
                    case DOWN:
                        if (--commandIndex < 0) {
                            commandIndex = CommandHistory.getInstance().commandNumber - 1;
                        }
                        commandField.setText(commands[commandIndex]);
                        break;
                }
            }
        });
    }
    
}
