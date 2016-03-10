package com.masterhilli.google.spreadsheet.api.auth;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by mhillbrand on 1/30/2016.
 */
public class AuthorizeService {

    /** Application name. */
    private static final String APPLICATION_NAME =
            "Drive API Java Quickstart";

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credentials/drive-java-quickstart");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart. */
    private static Collection<String> SCOPES = null; //DriveScopes.all(); // Arrays.asList(DriveScopes.DRIVE_READONLY);
    //DriveScopes.all();

    private static final String SPREADSHEET_SCOPE = "https://spreadsheets.google.com/feeds";

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);

        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    private static String pathToAuthorizationFile = "/googleapi/client_secret.json";

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    private static Credential authorize(Collection<String> driveScopes) throws IOException {
        // Load client secrets.
        System.out.printf("Path to google: %s\n", pathToAuthorizationFile);
        InputStream in =
                AuthorizeService.class.getResourceAsStream(pathToAuthorizationFile);
        if (in == null) {
            throw new NullPointerException("The path to the authorization file is wrong, recheck: " + pathToAuthorizationFile);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, AuthorizeService.SCOPES)
                        .setDataStoreFactory(DATA_STORE_FACTORY)
                        .setAccessType("offline")
                        .build();
        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    private static Credential credential = null;
    public static Credential getCredential(Collection<String> driveScopes) throws IOException{
        if (AuthorizeService.credential == null) {
            driveScopes = new ArrayList<>();
            driveScopes.addAll(DriveScopes.all());
            driveScopes.add(SPREADSHEET_SCOPE);
            AuthorizeService.SCOPES = driveScopes;
            AuthorizeService.credential = AuthorizeService.authorize(driveScopes);
        }
        return AuthorizeService.credential;
    }
    /**
     * Build and return an authorized Drive client service.
     * @return an authorized Drive client service
     * @throws IOException
     */
    public static Drive getDriveService(Credential credential) throws IOException {
        if (credential == null )
            credential = AuthorizeService.getCredential(DriveScopes.all());
        return new Drive.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
