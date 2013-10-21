
import java.io.File;
import javafx.stage.FileChooser;

/**
 *
 * @author kamizono
 */
public class FileChooserManager {

    private static FileChooserManager instance = new FileChooserManager();
    private FileChooser fileChooser = new FileChooser();

    public enum Mode {

        MD, PICTURE, SAVE;
    }
    public Mode mode = Mode.MD;

    private FileChooserManager() {
    }

    // インスタンス取得メソッド
    public static FileChooserManager getInstance() {
        return instance;
    }

    public void changeMode(Mode m) {
        mode = m;
        fileChooser.getExtensionFilters().clear();
        switch (mode) {
            case MD:
                fileChooser.setTitle("select markdown file");
                fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("markdown", "*.md", "*.MD"));
                break;
            case PICTURE:
                fileChooser.setTitle("select img file");
                fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("img", "*.jpg", "*.png"));
                break;
            case SAVE:
                fileChooser.setTitle("save md file");
                fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("markdown", "*.md", "*.MD"));
                break;
            default:
                throw new AssertionError(mode.name());
        }
    }

    public File showDialog() {
        switch (mode) {
            case MD:
                return fileChooser.showOpenDialog(null);
            case PICTURE:
                return fileChooser.showOpenDialog(null);
            case SAVE:
                return fileChooser.showSaveDialog(null);
            default:
                throw new AssertionError(mode.name());
        }
    }
}
