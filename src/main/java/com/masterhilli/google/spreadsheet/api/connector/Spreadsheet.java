package com.masterhilli.google.spreadsheet.api.connector;

import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;
import com.google.gdata.util.common.base.Pair;
import com.masterhilli.google.spreadsheet.api.GoogleServiceConnector;
import com.masterhilli.google.spreadsheet.api.GoogleSpreadSheetFeed;
import com.masterhilli.google.spreadsheet.api.GoogleWorksheetHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Created by mhillbrand on 2/4/2016.
 */
public class Spreadsheet {
    protected SpreadsheetEntry googleSpreadSheet = null;
    private HashMap<String, Pair<WorksheetEntry, HashMap<String, CellEntry>>> worksheetsContentByWorksheetName =
            new HashMap<>();

    public String getTitle() { return this.googleSpreadSheet.getTitle().getPlainText(); }

    public Spreadsheet(SpreadsheetEntry googleSpreadSheet) {
        this.googleSpreadSheet = googleSpreadSheet;
    }

    public Spreadsheet(String googleDriveFileId) {
        initializeSpreadsheet(googleDriveFileId, "");
    }

    public Spreadsheet(String googleDriveFileId, String worksheetName) {
        initializeSpreadsheet(googleDriveFileId, worksheetName);
    }

    public void update() {
        if (isConnectedToSpreadsheet()) {
            String updateOnlyWorksheetName = "";
            if (worksheetsContentByWorksheetName.size() == 1) {
                updateOnlyWorksheetName = worksheetsContentByWorksheetName.keySet().stream().collect(Collectors.toList()).get(0);

            }
            initializeSpreadsheet(googleSpreadSheet.getKey(), updateOnlyWorksheetName);
        }
    }

    public void update(String workSheetName) {
        if (isConnectedToSpreadsheet()) {
            initializeSpreadsheet(googleSpreadSheet.getKey(), workSheetName);
        }
    }

    private void initializeSpreadsheet(String googleDriveFileId, String worksheetName) {
        googleSpreadSheet = GoogleSpreadSheetFeed.GetSpreadsheetEntryByKey(googleDriveFileId);
        if (googleSpreadSheet != null) {
            HashMap<String, WorksheetEntry> worksheetsHashMap = GoogleWorksheetHandler.getWorksheetsForSpreadsheetEntry(googleSpreadSheet);
            for (String key : worksheetsHashMap.keySet()) {
                Pair<WorksheetEntry, HashMap<String, CellEntry>> myPair = null;
                if (worksheetName.length() == 0 || worksheetName.compareTo(key) == 0) {
                    HashMap<String, CellEntry> cellsByKey = GoogleWorksheetHandler.getCellsFromWorksheet(worksheetsHashMap.get(key));
                    myPair = new Pair<>(worksheetsHashMap.get(key), cellsByKey);
                    worksheetsContentByWorksheetName.put(key, myPair);
                }
            }
        }
    }

    public boolean isConnectedToSpreadsheet() {
        return googleSpreadSheet != null;
    }

    @Override
    public String toString() {
        String retVal = "***********************************************************************************\n";
        retVal += String.format("Title: %s (key: %s)\n", googleSpreadSheet.getTitle().getPlainText(),
                googleSpreadSheet.getKey());
        for (Pair<WorksheetEntry, HashMap<String, CellEntry>> entry : worksheetsContentByWorksheetName.values()) {
            retVal += String.format("-WS: %s (R:%d C:%d)\n", entry.first.getTitle().getPlainText(),
                    entry.first.getRowCount(), entry.first.getColCount());
        }
        retVal += "***********************************************************************************";

        return retVal;
    }

    public List<WorksheetEntry> getWorksheetEntries() {
        try {
            if (googleSpreadSheet != null) {
                return googleSpreadSheet.getWorksheets();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void addNewWorksheet(String name) {
        GoogleWorksheetHandler.createNewWorksheet(googleSpreadSheet, name);
    }

    public void deleteWorksheet(String name) {
        GoogleWorksheetHandler.deleteWorksheet(worksheetsContentByWorksheetName.get(name).first);
    }

    public void copyWorksheet(String copyFrom, String createNewWorksheet) {
        WorksheetEntry tobeCopied = worksheetsContentByWorksheetName.get(copyFrom).first;
        tobeCopied.setTitle(new PlainTextConstruct(createNewWorksheet));
        GoogleWorksheetHandler.addWorksheetEntry(googleSpreadSheet, tobeCopied);
        update();
        try {
            GoogleWorksheetHandler.copyCellsFromWorksheetToWorksheet(worksheetsContentByWorksheetName.get(copyFrom).first,
                    worksheetsContentByWorksheetName.get(createNewWorksheet).first);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    public void insertValueIntoCell(String worksheet, int column, int row, String value) {
        WorksheetEntry wsEntry = worksheetsContentByWorksheetName.get(worksheet).first;
        CellEntry cellEntry = new CellEntry(row, column, value);
        int exceptionCount = 2;
        do {
            try {
                GoogleServiceConnector.GetSpreadSheetService().insert(wsEntry.getCellFeedUrl(), cellEntry);
                exceptionCount = 0;
            } catch (Exception e) {
                e.printStackTrace();
                exceptionCount--;
            }
        } while (exceptionCount != 0);

    }

    public String receiveValueAtKey(String worksheetKey, String cellKey) {
        return worksheetsContentByWorksheetName.get(worksheetKey).second.get(cellKey).getCell().getValue();
    }

    private ListEntry createRow(Map<String, String> rowValues) {
        ListEntry row = new ListEntry();
        for (String columnName : rowValues.keySet()) {
            Object value = rowValues.get(columnName);
            row.getCustomElements().setValueLocal(columnName,
                    String.valueOf(value));
        }
        return row;
    }

    public boolean DoesWorkSheetExist(String workSheetName) {
        return worksheetsContentByWorksheetName.get(workSheetName) != null;
    }

}
