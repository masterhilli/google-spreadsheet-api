package com.masterhilli.google.spreadsheet.api;

import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.util.ServiceException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by mhillbrand on 2/18/2016.
 */
public class GoogleSpreadSheetFeed {
    private static SpreadsheetFeed allSpreadSheetsFeed = null;
    private static String urlForToRetrieveWorksheetsForSpreadsheetKey = "https://spreadsheets.google.com/feeds/worksheets/%s/private/full";

    private static URL allSpreadSheetUrl = null;

    static {
        try {
            allSpreadSheetUrl = new URL(
                    "https://spreadsheets.google.com/feeds/spreadsheets/private/full");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static SpreadsheetFeed GetFeedOfAllSpreadsheetsInService() throws IOException, ServiceException {
        if (allSpreadSheetsFeed == null) {
            // Make a request to the API and get all spreadsheets.
            allSpreadSheetsFeed= GoogleServiceConnector.GetSpreadSheetService().getFeed(allSpreadSheetUrl,
                    SpreadsheetFeed.class);
        }
        return allSpreadSheetsFeed;
    }

    public static SpreadsheetEntry GetSpreadsheetEntryByKey(String key) {
        SpreadsheetEntry spreadsheetEntry = null;
        SpreadsheetFeed feed = null;
        try {
            feed = GetFeedOfAllSpreadsheetsInService();
        } catch (Exception e) {
            e.printStackTrace();
            return spreadsheetEntry;
        }

        for (SpreadsheetEntry entry : feed.getEntries()) {
            if (entry.getKey().compareTo(key) == 0) {
                return entry;
            }
        }
        return null;
    }

    public static void ResetSpreadsheetFeedForAllSpreadsheets() throws IOException, ServiceException {
        allSpreadSheetsFeed = null;
        GetFeedOfAllSpreadsheetsInService();
    }
}
