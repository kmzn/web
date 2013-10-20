
import java.io.File;
import java.util.HashSet;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author hide
 */
public class TempFileDeleter {

    private static TempFileDeleter instance = new TempFileDeleter();
    private HashSet<String> filePaths = new HashSet<>();

    private TempFileDeleter() {
    }

    // インスタンス取得メソッド
    public static TempFileDeleter getInstance() {
        return instance;
    }
    
    public void add(String path) {
        filePaths.add(path);
    }
    
    public void deleteAll() {
        for (String path : filePaths) {
            File file = new File(path);
            if (file.exists()) {
                //System.out.println(path);
                file.delete();
            }
        }
    }
    
}
