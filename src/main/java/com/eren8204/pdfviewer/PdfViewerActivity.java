package com.eren8204.pdfviewer;

import android.annotation.SuppressLint;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

class PdfViewerActivity extends AppCompatActivity {
    private PdfRenderer pdfRenderer;
    private ParcelFileDescriptor parcelFileDescriptor;
    private TextView pageIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewPdf);
        pageIndicator = findViewById(R.id.pageIndicator);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String fileName = getIntent().getStringExtra("fileName");

        if (fileName == null || fileName.trim().isEmpty()) {
            Toast.makeText(this, "No PDF file provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        try {
            boolean isAsset = getIntent().getBooleanExtra("isAsset", true);

            File file;
            if (isAsset) {
                file = prepareFile(fileName);
            } else {
                file = new File(fileName);
                if (!file.exists()) {
                    Toast.makeText(this, "File does not exist", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
            }

            parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            pdfRenderer = new PdfRenderer(parcelFileDescriptor);

            PdfPageAdapter adapter = new PdfPageAdapter(pdfRenderer);
            recyclerView.setAdapter(adapter);

            updatePageIndicator(1, pdfRenderer.getPageCount());

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                    super.onScrolled(rv, dx, dy);
                    LinearLayoutManager lm = (LinearLayoutManager) rv.getLayoutManager();
                    if (lm != null) {
                        int firstVisible = lm.findFirstVisibleItemPosition();
                        updatePageIndicator(firstVisible + 1, pdfRenderer.getPageCount());
                    }
                }
            });

        } catch (IOException e) {
            Toast.makeText(this, "Failed to load PDF", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            finish();
        }
    }

    private File prepareFile(String fileName) throws IOException {
        File file = new File(getCacheDir(), fileName);
        if (!file.exists()) {
            try (InputStream asset = getAssets().open(fileName);
                 FileOutputStream out = new FileOutputStream(file)) {

                byte[] buffer = new byte[4096]; // bigger buffer for efficiency
                int read;
                while ((read = asset.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
            }
        }
        return file;
    }

    @SuppressLint("SetTextI18n")
    private void updatePageIndicator(int currentPage, int totalPages) {
        if (pageIndicator != null) {
            pageIndicator.setText("Page " + currentPage + " of " + totalPages);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeQuietly(pdfRenderer);
        closeQuietly(parcelFileDescriptor);
    }

    private void closeQuietly(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException ignored) {}
        }
    }

    private void closeQuietly(AutoCloseable c) {
        if (c != null) {
            try {
                c.close();
            } catch (Exception ignored) {}
        }
    }
}