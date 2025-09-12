package com.eren8204.pdfviewer;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class PdfPageAdapter extends RecyclerView.Adapter<PdfPageAdapter.PdfViewHolder> {
    private final PdfRenderer pdfRenderer;
    private final ExecutorService executor = Executors.newFixedThreadPool(2);
    private final LruCache<Integer, Bitmap> bitmapCache = new LruCache<>(5);

    PdfPageAdapter(PdfRenderer pdfRenderer) {
        this.pdfRenderer = pdfRenderer;
    }

    @NonNull
    @Override
    public PdfViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pdf_page, parent, false);
        return new PdfViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PdfViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return pdfRenderer.getPageCount();
    }

    @Override
    public void onViewRecycled(@NonNull PdfViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.bitmap != null && !holder.bitmap.isRecycled()) {
            holder.bitmap.recycle();
            holder.bitmap = null;
        }
    }

    class PdfViewHolder extends RecyclerView.ViewHolder {
        private final ImageView photoView;
        private Bitmap bitmap;

        PdfViewHolder(@NonNull View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.pdfPageImage);
        }

        void bind(int position) {
            Bitmap cached = bitmapCache.get(position);
            if (cached != null && !cached.isRecycled()) {
                photoView.setImageBitmap(cached);
                return;
            }

            photoView.setImageResource(android.R.color.darker_gray);

            executor.execute(() -> {
                try (PdfRenderer.Page page = pdfRenderer.openPage(position)) {
                    int targetWidth = photoView.getWidth() > 0 ? photoView.getWidth() : page.getWidth();
                    float scale = (float) targetWidth / page.getWidth();
                    int width = (int) (page.getWidth() * scale);
                    int height = (int) (page.getHeight() * scale);

                    Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                    bitmapCache.put(position, bmp);

                    photoView.post(() -> {
                        if (photoView != null) {
                            photoView.setImageBitmap(bmp);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}