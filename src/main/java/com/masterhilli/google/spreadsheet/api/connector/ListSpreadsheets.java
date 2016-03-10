package com.masterhilli.google.spreadsheet.api.connector;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.masterhilli.google.spreadsheet.api.auth.AuthorizeService;

import java.io.IOException;
import java.util.Arrays;

import static com.masterhilli.google.spreadsheet.api.auth.AuthorizeService.getDriveService;

/**
 * Created by felix on 23/02/2016.
 */
public class ListSpreadsheets {
    public static FileList retrieveAllFiles(String arg, String parentId) throws IOException{
        Credential authCredential = AuthorizeService.getCredential(Arrays.asList("https://spreadsheets.google.com/feeds"));
        Drive driveService = getDriveService(authCredential);

        FileList result;
        String pageToken = null;

        do {
             result = driveService.files().list()
                    .setQ("(mimeType='application/vnd.google-apps.spreadsheet') and (name contains '" + arg + "') and ('" + parentId +"' in parents)")
                    .setSpaces("drive")
                    .setFields("nextPageToken, files(id, name, parents)")
                    .setPageToken(pageToken)
                    .execute();
            for(File file: result.getFiles()) {
                System.out.printf("%s;%s ",
                        file.getName(), file.getId());
                if (file.getParents() != null) {
                    for (String parent : file.getParents()) {
                        System.out.printf("%s, ", parent);
                    }
                }
                System.out.println();
            }
            pageToken = result.getNextPageToken();
        } while (pageToken != null);

        return result;
    }
}
