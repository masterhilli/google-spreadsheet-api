package com.masterhilli.google.spreadsheet.api;

import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mhillbrand on 2/20/2016.
 */
public class GoogleWorksheetHandler {
    public static HashMap<String, WorksheetEntry> getWorksheetsForSpreadsheetEntry(SpreadsheetEntry spreadsheet) {
        HashMap<String, WorksheetEntry> worksheetsByName = new HashMap<>();
        List<WorksheetEntry> worksheetEntries = null;
        try {
            worksheetEntries=  spreadsheet.getWorksheets();
            for (WorksheetEntry entry : worksheetEntries) {
                worksheetsByName.put(entry.getTitle().getPlainText(), entry);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        return worksheetsByName;
    }

    public static void createNewWorksheet(SpreadsheetEntry spreadsheet, String worksheetName) {
        WorksheetEntry worksheet = new WorksheetEntry();
        worksheet.setTitle(new PlainTextConstruct(worksheetName));
        worksheet.setColCount(50);
        worksheet.setRowCount(1000);
        addWorksheetEntry(spreadsheet, worksheet);
    }

    public static void addWorksheetEntry(SpreadsheetEntry spreadsheet, WorksheetEntry worksheet) {
        // Send the local representation of the worksheet to the API for
        // creation.  The URL to use here is the worksheet feed URL of our
        // spreadsheet.
        URL worksheetFeedUrl = spreadsheet.getWorksheetFeedUrl();
        try {
            GoogleServiceConnector.GetSpreadSheetService().insert(worksheetFeedUrl, worksheet);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    public static void deleteWorksheet(WorksheetEntry worksheetEntry) {
        try {
            worksheetEntry.delete();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    public static void copyCellsFromWorksheetToWorksheet(WorksheetEntry copyCellsFrom, WorksheetEntry copyCellsTo) throws IOException, ServiceException {
        CellFeed cellsToCopyFrom = GoogleServiceConnector.GetSpreadSheetService().getFeed(copyCellsFrom.getCellFeedUrl(), CellFeed.class);
        CellFeed cellsToCopyTo = GoogleServiceConnector.GetSpreadSheetService().getFeed(copyCellsTo.getCellFeedUrl(),
                CellFeed.class);

        for (CellEntry entry : cellsToCopyFrom.getEntries()) {
            cellsToCopyTo.insert(new CellEntry(entry));
        }
    }

    public static HashMap<String, CellEntry> getCellsFromWorksheet(WorksheetEntry wsEntry) {
        HashMap<String, CellEntry> cellsPerName = new HashMap<>();
        CellFeed cellFeed = null;
        try {
            cellFeed = GoogleServiceConnector.GetSpreadSheetService().getFeed(wsEntry.getCellFeedUrl(), CellFeed.class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        for (CellEntry entry : cellFeed.getEntries()) {
            cellsPerName.put(entry.getTitle().getPlainText(), entry);
        }
        return cellsPerName;
    }

    public static void updateCellValueInWorksheet(WorksheetEntry wsEntry, String key, String value) {
        HashMap<String, CellEntry> cellsPerKey = getCellsFromWorksheet(wsEntry);
        if (cellsPerKey.get(key) == null) {
            addCellWithValueInWorksheet(wsEntry, key, value);
        } else {
            cellsPerKey.get(key).changeInputValueLocal(value);
            try {
                cellsPerKey.get(key).update();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ServiceException e) {
                e.printStackTrace();
            }
        }

    }

    private static void addCellWithValueInWorksheet(WorksheetEntry wsEntry, String key, String value) {
        CellFeed cellFeed = null;
        try {
            cellFeed = GoogleServiceConnector.GetSpreadSheetService().getFeed(wsEntry.getCellFeedUrl(), CellFeed.class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        CellEntry newEntry = new CellEntry(5, 10, value);
        // TODO: find a new / better way to set values to the spreadsheet!
        //newEntry.setTitle(new PlainTextConstruct(key));
        //newEntry.changeInputValueLocal(value);
        try {
            cellFeed.insert(newEntry);
        } catch (ServiceException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
