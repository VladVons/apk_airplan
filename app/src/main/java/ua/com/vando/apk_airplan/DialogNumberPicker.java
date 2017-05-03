package ua.com.vando.apk_airplan;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;

import com.hrules.horizontalnumberpicker.HorizontalNumberPicker;
import com.hrules.horizontalnumberpicker.HorizontalNumberPickerListener;

// http://stackoverflow.com/questions/10313382/how-to-get-elementsfindviewbyid-for-a-layout-which-is-dynamically-loadedsetvi

public class DialogNumberPicker extends DialogFragment{
    private View viewNumberPicker;
    private CheckBox cbLink;
    private AlertDialog.Builder alertDialog;

    public HorizontalNumberPickerListener OnHorizontalNumberPicker;
    public DialogInterface.OnClickListener OnDialogClick;

    public DialogNumberPicker() {
        super();
        OnHorizontalNumberPicker = null;
        OnDialogClick = null;
        alertDialog = null;

        // exception
        //LayoutInflater inflater = getActivity().getLayoutInflater();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = getActivity();

        LayoutInflater inflater = activity.getLayoutInflater();
        viewNumberPicker = inflater.inflate(R.layout.dialog_number_picker, null);
        cbLink = (CheckBox) viewNumberPicker.findViewById(R.id.cbLink);

        HorizontalNumberPicker horizontalNumberPicker = (HorizontalNumberPicker) viewNumberPicker.findViewById(R.id.horizontal_number_picker);
        horizontalNumberPicker.setListener(horizontalNumberPickerListener);
        horizontalNumberPicker.SetEdit(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        alertDialog = builder.setView(viewNumberPicker);
        alertDialog.setTitle("Title");
        //ADBuilder.setMessage("Body");
        alertDialog.setPositiveButton("OK", OnDialogClick);
        alertDialog.setNegativeButton("Cancel", OnDialogClick);

        return builder.create();
    }

    private HorizontalNumberPickerListener horizontalNumberPickerListener = new HorizontalNumberPickerListener() {
        @Override
        public void onHorizontalNumberPickerChanged(HorizontalNumberPicker horizontalNumberPicker, int value) {
            if (OnHorizontalNumberPicker != null && cbLink.isChecked()) {
                OnHorizontalNumberPicker.onHorizontalNumberPickerChanged(horizontalNumberPicker, value);
            }
        }
    };
}
