package com.tawlat.customViews;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.tawlat.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mohammed on 11/4/15.
 */
public class MultiSpinner extends Spinner implements DialogInterface.OnMultiChoiceClickListener, DialogInterface.OnCancelListener {

    public interface MultiSpinnerListener {
        void onItemsSelected(boolean[] selected);

        void onItemSelectionChanged(int position, boolean isChecked);
    }

    private List<String> items = new ArrayList<>();
    //private List<Boolean> selected = new ArrayList<>();
    private boolean[] selected_primitive;
    private String defaultText;
    private MultiSpinnerListener listener;
    ArrayAdapter<String> adapter;
    private AlertDialog mDialog;

    public MultiSpinner(Context context) {
        super(context);
        init();
    }

    public MultiSpinner(Context context, int mode) {
        super(context, mode);
        init();

    }

    public MultiSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public MultiSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item_italic);
        adapter.setDropDownViewResource(R.layout.spinner_item_italic);
        setAdapter(adapter);
    }

    public ArrayList<String> getSelectedItems() {
        ArrayList<String> selectedItems = new ArrayList<>();
        for (int i = 0; i < selected_primitive.length; i++) {
            if (selected_primitive[i]) {
                selectedItems.add(items.get(i));
            }
        }
        return selectedItems;
    }

    public boolean[] getSelected() {
        return selected_primitive;
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        selected_primitive[which] = isChecked;
        if (listener != null)
            listener.onItemSelectionChanged(which, isChecked);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        StringBuilder spinnerBuffer = new StringBuilder();
        boolean someUnselected = false;
        boolean nonSelected = true;

        int len = items.size();
        int numberOfSelected = 0;
        String firstSelected = null;
        for (int i = 0; i < len; i++)
            if (selected_primitive[i]) {
                numberOfSelected++;
                if (firstSelected == null)
                    firstSelected = items.get(i);
                //spinnerBuffer.append(items.get(i)).append(", ");
                nonSelected = false;
            } else
                someUnselected = true;

        String spinnerText;
        if (someUnselected && firstSelected != null) {
            spinnerText = firstSelected + ((numberOfSelected > 1) ? " + " + (numberOfSelected - 1) + " more" : "");
        } else if (nonSelected)
            spinnerText = "No Selection";
        else
            spinnerText = defaultText;

        adapter.clear();
        adapter.add(spinnerText);
        adapter.notifyDataSetChanged();


        if (listener != null)
            listener.onItemsSelected(selected_primitive);
    }

    @Override
    public boolean performClick() {
        if (items != null) {

            mDialog.show();
        }
        return true;
    }

    public void selectAll(boolean select) {
        int len = selected_primitive.length;
        for (int i = 0; i < len; i++) {
            //selected.set(i, select);
            selected_primitive[i] = select;
            mDialog.getListView().setItemChecked(i, select);
        }

        adapter.clear();
        adapter.add(defaultText);
        adapter.notifyDataSetChanged();
    }

    public void setItems(List<String> items, String allText,
                         MultiSpinnerListener listener) {
        this.items.clear();

        this.items.addAll(items);
        this.defaultText = allText;
        this.listener = listener;
        this.selected_primitive = new boolean[this.items.size()];

        // all selected by default
        int len = items.size();
        for (int i = 0; i < len; i++) {
            //selected.add(true);
            this.selected_primitive[i] = true;
        }

        // all text on the spinner

        adapter.clear();
        adapter.add(allText);
        adapter.notifyDataSetChanged();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMultiChoiceItems(
                items.toArray(new CharSequence[items.size()]), selected_primitive, this);
        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.setOnCancelListener(this);
        mDialog = builder.create();
    }


    public void clear() {
        items.clear();
        //selected.clear();
    }

    public boolean isAllSelected() {
        for (boolean s : selected_primitive)
            if (!s)
                return false;
        return true;
    }

    public void setSelected(int position, boolean selected) {
        selected_primitive[position] = selected;
        mDialog.getListView().setItemChecked(position, selected);
    }
}
