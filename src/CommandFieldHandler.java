
import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
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
        // コマンド履歴ファイルがあるならそれを反映させる
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
