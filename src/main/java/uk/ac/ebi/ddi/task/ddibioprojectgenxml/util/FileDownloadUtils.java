package uk.ac.ebi.ddi.task.ddibioprojectgenxml.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.ddi.task.ddibioprojectgenxml.exceptions.FileDownloadException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class FileDownloadUtils {

    private static final int RETRY_DELAY_MS = 2000;

    private static final int RETRIES = 3;

    private static final Logger LOGGER = LoggerFactory.getLogger(FileDownloadUtils.class);

    private FileDownloadUtils() {
    }

    public static void downloadFile(String fileUrl, File dest) throws IOException {
        URL url = new URL(fileUrl);
        URLConnection connection = url.openConnection();

        try (InputStream is = connection.getInputStream();
             FileOutputStream fileOutput = new FileOutputStream(dest)) {

            byte[] buffer = new byte[2048];
            int bufferLength; //used to store a temporary size of the buffer

            while ((bufferLength = is.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);
            }
        }
    }

    public static void httpDownloadFile(String fileUrl, File dest)
            throws IOException, InterruptedException, FileDownloadException {
        URL url = new URL(fileUrl);
        HttpURLConnection connection = getConnection(url);

        try (InputStream is = connection.getInputStream();
             FileOutputStream fileOutput = new FileOutputStream(dest)) {

            byte[] buffer = new byte[2048];
            int bufferLength; //used to store a temporary size of the buffer

            while ((bufferLength = is.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);
            }
        }
    }

    private static HttpURLConnection getConnection(URL entries)
            throws InterruptedException, IOException, FileDownloadException {
        int retry = 0;
        boolean delay = false;
        do {
            if (delay) {
                Thread.sleep(RETRY_DELAY_MS);
            }
            HttpURLConnection connection = (HttpURLConnection) entries.openConnection();
            switch (connection.getResponseCode()) {
                case HttpURLConnection.HTTP_OK:
                    return connection; // **EXIT POINT** fine, go on
                case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
                    LOGGER.warn("Gateway timeout for {}", entries);
                    break; // retry
                case HttpURLConnection.HTTP_UNAVAILABLE:
                    LOGGER.warn("Http unavailable, {}", entries);
                    break; // retry, server is unstable
                default:
                    LOGGER.error("Unknown response: {}, for: {}", connection.getResponseCode(), entries);
                    break; // abort
            }
            // we did not succeed with connection (or we would have returned the connection).
            connection.disconnect();
            // retry
            retry++;
            LOGGER.warn("Failed retry {}/{}", retry, RETRIES);
            delay = true;

        } while (retry < RETRIES);

        LOGGER.error("Aborting download of {}.", entries);
        throw new FileDownloadException();
    }
}
