package com.masterhilli.google.spreadsheet.api.download;

import com.masterhilli.google.spreadsheet.api.connector.Spreadsheet;
import com.google.gdata.data.spreadsheet.WorksheetEntry;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by mhillbrand on 3/10/2016.
 */
public class WorksheetEntryDownloadInfo {

    private String urlAsString;
    private final String fileName;

    public WorksheetEntryDownloadInfo (Spreadsheet spreadsheet, WorksheetEntry wsEntry) {
        urlAsString = wsEntry.getLink("http://schemas.google.com/spreadsheets/2006#exportcsv", "text/csv").getHref();
        urlAsString = urlAsString.substring(0, urlAsString.length()-3) + "pdf";
        fileName = spreadsheet.getTitle() + "-"+wsEntry.getTitle().getPlainText();
    }

    public String getUrlAsString() {
        return urlAsString;
    }

    public String getFileNameEscaped() {
        return fileName.replace("/", "_").replace("\\", "_");
    }

    public static List<WorksheetEntryDownloadInfo> createDownloadInformationFromSpreadsheets(List<Spreadsheet> spreadsheets, String workSheetname) {
        List<WorksheetEntryDownloadInfo> wsDwnldInfo = new ArrayList<>();
        for (Spreadsheet spreadsheet : spreadsheets) {
            List<WorksheetEntry> wsEntries = spreadsheet.getWorksheetEntries();
            for (WorksheetEntry wsEntry : wsEntries) {
                if (workSheetname != null) {
                    if (wsEntry.getTitle().getPlainText().compareTo(workSheetname) == 0) {
                        wsDwnldInfo.add(new WorksheetEntryDownloadInfo(spreadsheet, wsEntry));
                    }
                }  else { // add every worksheet for Download
                    wsDwnldInfo.add(new WorksheetEntryDownloadInfo(spreadsheet, wsEntry));
                }
            }
        }

        return wsDwnldInfo;
    }
}
