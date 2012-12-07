/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.FileCredentialStore;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.common.base.Preconditions;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
/**
 *
 * @author loglud
 */
public class CASUALGDrive {
    
    /**
     * Global instance of the HTTP transport.
     */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    /**
     * Global Drive API client.
     */
    public static Drive drive;
    Credential credential;
    
    public CASUALGDrive() throws Exception
    {
        credential = authorize();
        drive = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(
                        "CASUAL/Beta").build();
    }
    /**
     * Authorizes the installed application to access user's protected data.
     */
    public static Credential authorize() throws Exception {
        // load client secrets
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JSON_FACTORY, CASUALGDrive.class.getResourceAsStream("./client_secrets.json"));
        if (clientSecrets.getDetails().getClientId().startsWith("Enter")
                || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
            System.out.println(
                    "Enter Client ID and Secret from https://code.google.com/apis/console/?api=drive "
                    + "into drive-cmdline-sample/src/main/resources/client_secrets.json");
            System.exit(1);
        }
        // set up file credential store
        FileCredentialStore credentialStore = new FileCredentialStore(new java.io.File(
                System.getProperty("user.home"), ".credentials/drive.json"), JSON_FACTORY);
        // set up authorization code flow
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets,
                Arrays.asList("https://www.googleapis.com/auth/drive")).setCredentialStore(credentialStore).build();
        // authorize
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    public static void main(String[] args) {
        //Preconditions.checkArgument(!UPLOAD_FILE_PATH.startsWith("Enter ")
          //      && !DIR_FOR_DOWNLOADS.startsWith("Enter "),
            //    "Please enter the upload file path and download directory in %s", CASUALGDrive.class);

        try {
            try {
                // authorization
                Credential credential = authorize();
                // set up the global Drive instance
                drive = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(
                        "CASUAL/Beta").build();

                // run commands

//        View.header1("Starting Resumable Media Upload");
//        File uploadedFile = uploadFile(false);
//
//        View.header1("Updating Uploaded File Name");
//        File updatedFile = updateFileWithTestSuffix(uploadedFile.getId());
//
//        View.header1("Starting Resumable Media Download");
//        downloadFile(false, updatedFile);
//
//        View.header1("Starting Simple Media Upload");
//        uploadedFile = uploadFile(true);
//
//        View.header1("Starting Simple Media Download");
//        downloadFile(true, uploadedFile);
//
//        View.header1("Success!");
//        return;
                System.out.println(drive.files().list().execute());
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        System.exit(1);
    }

    /**
     * Uploads a file using either resumable or direct media upload.
     */
    public static File uploadFile(boolean useDirectUpload, java.io.File uploadFile) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setTitle(uploadFile.getName());
        InputStreamContent mediaContent = new InputStreamContent("doc/pdf", new BufferedInputStream(
                new FileInputStream(uploadFile)));
        mediaContent.setLength(uploadFile.length());

        Drive.Files.Insert insert = drive.files().insert(fileMetadata, mediaContent);
        MediaHttpUploader uploader = insert.getMediaHttpUploader();
        uploader.setDirectUploadEnabled(useDirectUpload);
        //uploader.setProgressListener(new FileUploadProgressListener());
        return insert.execute();
    }
    
    public static List<File> getAllFilesFromDrive() throws IOException
    {
        List<File> result = new ArrayList<File>();
        Files.List request = drive.files().list();

                do {
                    try {
                        FileList files = request.execute();

                        result.addAll(files.getItems());
                        request.setPageToken(files.getNextPageToken());
                    } catch (IOException e) {
                        System.out.println("An error occurred: " + e);
                        request.setPageToken(null);
                    }
                } while (request.getPageToken() != null
                        && request.getPageToken().length() > 0);
                
                return result;
    }

    /**
     * Updates the name of the uploaded file to have a "drivetest-" prefix.
     */
    public static File updateFileWithTestSuffix(String id) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setTitle("drivetest-" + UPLOAD_FILE.getName());
        Drive.Files.Update update = drive.files().update(id, fileMetadata);
        return update.execute();
    }

    /**
     * Downloads a file using either resumable or direct media download.
     */
    public static void downloadFile(boolean useDirectDownload, File uploadedFile)
            throws IOException {
        // create parent directory (if necessary)
        java.io.File parentDir = new java.io.File(DIR_FOR_DOWNLOADS);
        if (!parentDir.exists() && !parentDir.mkdirs()) {
            throw new IOException("Unable to create parent directory");
        }
        OutputStream out = new FileOutputStream(new java.io.File(parentDir, uploadedFile.getTitle()));

        Drive.Files.Get get = drive.files().get(uploadedFile.getId());
        MediaHttpDownloader downloader = get.getMediaHttpDownloader();
        downloader.setDirectDownloadEnabled(useDirectDownload);
        //downloader.setProgressListener(new FileDownloadProgressListener());
        downloader.download(new GenericUrl(uploadedFile.getDownloadUrl()), out);
    }
    
    
}