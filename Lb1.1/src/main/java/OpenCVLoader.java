/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author reson
 */
public class OpenCVLoader {

    public static void loadLib(String path, String name) throws Exception {
        name = "/" + name + ".dll";
        try {
            // have to use a stream
            InputStream in = OpenCVLoader.class.getResourceAsStream(name);
            // always write to different location
            File fileOut = new File(System.getProperty("java.io.tmpdir") + "/" + path + name);
            //logger.info("Writing dll to: " + fileOut.getAbsolutePath());
            OutputStream out = FileUtils.openOutputStream(fileOut);
            IOUtils.copy(in, out);
            in.close();
            out.close();
            System.load(fileOut.toString());
        } catch (Exception e) {
            throw new Exception("Failed to load required DLL", e);
        }
    }
    
}
