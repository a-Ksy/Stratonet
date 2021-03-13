package Stratonet.Infrastructure.Utils;

import Stratonet.Core.Helpers.StratonetLogger;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;

public class ImageToByteArrayConverter {
    public static byte[] Convert(String url) {
        try {
            URL urlToDownload = new URL(url);
            InputStream in = new BufferedInputStream(urlToDownload.openStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int n = 0;
            while (-1 != (n = in.read(buf))) {
                out.write(buf, 0, n);
            }
            out.close();
            in.close();
            byte[] response = out.toByteArray();

            return response;
        } catch (IOException ex) {
            StratonetLogger.getInstance().log(Level.SEVERE, "Exception while converting image to byte array");
        }

        return null;
    }
}
