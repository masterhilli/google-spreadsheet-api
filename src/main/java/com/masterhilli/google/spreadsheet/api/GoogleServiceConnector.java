package com.masterhilli.google.spreadsheet.api;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.masterhilli.google.spreadsheet.api.auth.AuthorizeService;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by mhillbrand on 2/18/2016.
 */
public class GoogleServiceConnector {
    private static SpreadsheetService spreadSheetAppConnection = null;
    public static SpreadsheetService GetSpreadSheetService() throws IOException {
        if (spreadSheetAppConnection == null) {
            spreadSheetAppConnection = new SpreadsheetService("MySpreadsheetIntegration-v1");
            try {
                spreadSheetAppConnection.setOAuth2Credentials(AuthorizeService.getCredential(Arrays.asList("https://spreadsheets.google.com/feeds"))); //does not find that method????
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            }
        }
        return spreadSheetAppConnection;
    }
}
