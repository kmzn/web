
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

    public CommandFieldHandler() {
    }
    
    public void initialize(TextField commandField, BorderPane root) {
        
        CommandHistory.getInstance().fileReadStart(commandField);
        commandField.prefWidthProperty().bind(Bindings.max(Bindings.subtract(root.widthProperty(), 200), 200));
    }
    
    public String getText(TextField commandField) {
        CommandHistory.getInstance().add(commandField.getText());
        return commandField.getText();
    }
    
    public void setOnKeyPressed(final TextField commandField) {
        commandField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                
                switch (e.getCode()) {
                    case UP:
                        commandField.setText(CommandHistory.getInstance().getNext());
                        break;
                    case DOWN:
                        commandField.setText(CommandHistory.getInstance().getPrevious());
                        break;
                }
            }
        });
    }
    
}
