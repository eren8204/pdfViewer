package com.eren8204.pdfviewer;

import android.content.Context;
import android.content.Intent;

import java.io.File;

public class PdfViewer {

    /**
     * Open a PDF that is bundled in the app's assets folder.
     *
     * @param context  Activity or Application context
     * @param fileName Name of the PDF inside assets/ (e.g., "sample.pdf")
     */
    public static void openAsset(Context context, String fileName) {
        if (context == null || fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("Context and fileName must not be null or empty");
        }

        Intent intent = new Intent(context, PdfViewerActivity.class);
        intent.putExtra("fileName", fileName);
        intent.putExtra("isAsset", true);

        if (!(context instanceof android.app.Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        context.startActivity(intent);
    }

    /**
     * Open a PDF from any file path on device.
     *
     * @param context Activity or Application context
     * @param file    PDF File object
     */
    public static void openFile(Context context, File file) {
        if (context == null || file == null || !file.exists()) {
            throw new IllegalArgumentException("Context must not be null and file must exist");
        }

        Intent intent = new Intent(context, PdfViewerActivity.class);
        intent.putExtra("fileName", file.getAbsolutePath());
        intent.putExtra("isAsset", false);

        if (!(context instanceof android.app.Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        context.startActivity(intent);
    }
}