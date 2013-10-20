
import java.io.File;

public class ResourceUtils {

    public static String getRelativePath(String targetPath, String basePath,
            String pathSeparator) {

        // find common path
        String[] target = targetPath.split(pathSeparator);
        String[] base = basePath.split(pathSeparator);

        String common = "";
        int commonIndex = 0;
        for (int i = 0; i < target.length && i < base.length; i++) {

            //System.out.println(target[i] + " : " + base[i]);
            if (target[i].equals(base[i])) {
                common += target[i] + pathSeparator;
                commonIndex++;
            } else {
                break;
            }
        }


        //System.out.println("" + commonIndex);
        String relative = "";
        // is the target a child directory of the base directory?
        // i.e., target = /a/b/c/d, base = /a/b/
        if (commonIndex == base.length) {
            relative = "." + pathSeparator + targetPath.substring(common.length());
        } else {
            // determine how many directories we have to backtrack
            for (int i = 1; i <= (base.length - 1) - commonIndex; i++) {
                relative += ".." + pathSeparator;
            }
            for (int i = commonIndex; i < target.length - 1; i++) {
                relative += target[i] + pathSeparator;
            }
            relative += target[target.length - 1];
            System.out.println(relative);
            //relative += targetPath.substring(common.length());
        }

        return relative;
    }

    public static String getRelativePath(String targetPath, String basePath) {
        return getRelativePath(targetPath, basePath, File.pathSeparator);
    }
}