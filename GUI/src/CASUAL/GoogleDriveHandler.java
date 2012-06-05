package CASUAL;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * 
 * 
 * 
 * 
Client ID:	
352129366481.apps.googleusercontent.com
Client secret:	
W3PBfKyatrnqaUv-XHBEPStD
Redirect URIs:	urn:ietf:wg:oauth:2.0:oob
http://localhost
 * 
 */

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;

import java.io.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import java.nio.charset.Charset;

/**
 * @author Yaniv Inbar
 */
public class StorageServiceAccountSample {

  /** E-mail address of the service account. */
  private static final String SERVICE_ACCOUNT_EMAIL = 
    "adamoutler@gmail.com";

  /** Bucket to list. */
  private static final String BUCKET_NAME = "[[INSERT_YOUR_BUCKET_NAME_HERE]]";

  /** Global configuration of Google Cloud Storage OAuth 2.0 scope. */
  private static final String STORAGE_SCOPE = 
    "https://www.googleapis.com/auth/devstorage.read_write";
  
  /** Global instance of the HTTP transport. */
  private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = new JacksonFactory();

  public static void main(String[] args) {
    try {
      try {
        // check for valid setup
        Preconditions.checkArgument(!SERVICE_ACCOUNT_EMAIL.startsWith("[["),
          "Please enter your service account e-mail from the Google APIs " +
          "Console to the SERVICE_ACCOUNT_EMAIL constant in %s", 
          StorageServiceAccountSample.class.getName());
        Preconditions.checkArgument(!BUCKET_NAME.startsWith("[["),
          "Please enter your desired Google Cloud Storage bucket name " +
          "to the BUCKET_NAME constant in %s", 
          StorageServiceAccountSample.class.getName());
        String p12Content = Files.readFirstLine(new File("key.p12"), 
          Charset.defaultCharset());
        Preconditions.checkArgument(!p12Content.startsWith("Please"), 
          p12Content);

        // Build service account credential.
        GoogleCredential credential = 
          new GoogleCredential.Builder().setTransport(HTTP_TRANSPORT)
            .setJsonFactory(JSON_FACTORY)
            .setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
            .setServiceAccountScopes(STORAGE_SCOPE)
            .setServiceAccountPrivateKeyFromP12File(new File("key.p12"))
            .build();

        // Set up and execute Google Cloud Storage request.
        String URI = "http://commondatastorage.googleapis.com/" + BUCKET_NAME;
        HttpRequestFactory requestFactory = 
          HTTP_TRANSPORT.createRequestFactory(credential);
        GenericUrl url = new GenericUrl(URI);
        HttpRequest request = requestFactory.buildGetRequest(url);
        HttpResponse response = request.execute();
        String content = response.parseAsString();

        // Instantiate transformer input
        Source xmlInput = new StreamSource(new StringReader(content));
        StreamResult xmlOutput = new StreamResult(new StringWriter());

        // Configure transformer
        Transformer transformer = TransformerFactory.newInstance()
          .newTransformer(); // An identity transformer
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "testing.dtd");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(
          "{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.transform(xmlInput, xmlOutput);

        // Pretty print the output XML.
        System.out.println("\nBucket listing for " + BUCKET_NAME + ":\n");
        System.out.println(xmlOutput.getWriter().toString());
        System.exit(0);

      } catch (IOException e) {
        System.err.println(e.getMessage());
      }
    } catch (Throwable t) {
      t.printStackTrace();
    }
    System.exit(1);
  }

}
