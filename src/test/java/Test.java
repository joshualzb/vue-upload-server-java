import eu.medsea.mimeutil.MimeUtil;
import net.sf.jmimemagic.*;

import java.io.File;
import java.util.Collection;

public class Test {

    public static void main(String[] args) {
        String filePath = "C:\\Users\\joshu\\Videos\\4.MOV";
        MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
        File f = new File(filePath);
        Collection<?> mimeTypes = MimeUtil.getMimeTypes(f);
        String type = mimeTypes.toString();
        System.out.println(type);
    }
}
