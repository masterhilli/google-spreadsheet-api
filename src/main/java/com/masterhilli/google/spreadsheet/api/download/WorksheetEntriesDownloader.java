package com.masterhilli.google.spreadsheet.api.download;

import com.github.axet.wget.WGet;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mhillbrand on 3/10/2016.
 */
public class WorksheetEntriesDownloader {
    public static List<File> getDownloadedFiles() {
        return downloadedFiles;
    }

    public static List<File> downloadedFiles = new ArrayList<>();
    public static void Download(String pathToFolder, List<WorksheetEntryDownloadInfo> downloadInfos) {
        if (downloadInfos == null)
            return;
        downloadedFiles.clear();
        String pathToSaveFile = Paths.get(pathToFolder).toAbsolutePath().normalize() + "\\";
        for (WorksheetEntryDownloadInfo wsEntryDwnldInfo : downloadInfos) {
            URL url = getUrl(wsEntryDwnldInfo);

            downloadUrlAsPdf(pathToSaveFile, wsEntryDwnldInfo, url);
        }


    }

    private static void downloadUrlAsPdf(String pathToSaveFile, WorksheetEntryDownloadInfo wsEntryDwnldInfo, URL url) {
        try {
            java.io.File target = new java.io.File(pathToSaveFile + wsEntryDwnldInfo.getFileNameEscaped() + ".pdf");
            System.out.println("Downloading: " + target.getName());
            // initialize wget object
            WGet w = new WGet(url, target);
            // single thread download. will return here only when file download
            // is complete (or error raised).
            w.download();
            downloadedFiles.add(target);
        } catch (Exception ex) {
            ex.printStackTrace(); // we can catch anything here, because the worst thing that happens: no download ;)
        }
    }

    private static URL getUrl(WorksheetEntryDownloadInfo wsEntryDwnldInfo) {
        URL url = null;
        try {
            url = new URL(wsEntryDwnldInfo.getUrlAsString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
        return url;
    }
}
