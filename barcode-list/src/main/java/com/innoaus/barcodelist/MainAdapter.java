package com.innoaus.barcodelist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    private Context context;
    BarcodeItemManager itemManager;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout layout;
        public TextView textName;
        public TextView textMemo;
        public ImageView image;

        public ViewHolder(View v) {
            super(v);
            textName = (TextView) v.findViewById(R.id.text_name);
            textMemo = (TextView) v.findViewById(R.id.text_memo);
            image = (ImageView) v.findViewById(R.id.image_icon);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MainAdapter(Context context) {
        this.context = context;
        itemManager = BarcodeItemManager.getInstance();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_main, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        BarcodeItem item = itemManager.getItems().get(position);
        holder.textName.setText(item.result);
        holder.textMemo.setText(item.timestamp);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return itemManager.getItemCount();
    }
}