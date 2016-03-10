package com.masterhilli.google.spreadsheet.api.connector;

import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

/**
 * Created by mhillbrand on 2/21/2016.
 */
public class TestSpreadsheet {
    private static SpreadsheetEntry googleSpreadSheet;

    @BeforeClass
    public static void initMocks() {
        googleSpreadSheet = Mockito.mock(SpreadsheetEntry.class);
        when(googleSpreadSheet.getKey()).thenReturn("TEST");
    }

    @Test
    public void testGetTitleForSpreadSheetEntryReturnsTest() {
        Spreadsheet spreadsheet= new Spreadsheet(googleSpreadSheet);
        Assert.assertEquals(spreadsheet.googleSpreadSheet.getKey(), "TEST");
    }
}
