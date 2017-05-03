package ua.com.vando.apk_airplan;

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

public class DialogNumberPicker extends DialogFragment implements DialogInterface.OnClickListener {
    private View viewNumberPicker;
    private CheckBox cbLink;
    public HorizontalNumberPickerListener OnHorizontalNumberPicker = null;
    public AlertDialog.Builder alertDialog = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        viewNumberPicker = inflater.inflate(R.layout.dialog_number_picker, null);
        cbLink = (CheckBox) viewNumberPicker.findViewById(R.id.cbLink);

        HorizontalNumberPicker horizontalNumberPicker = (HorizontalNumberPicker) viewNumberPicker.findViewById(R.id.horizontal_number_picker);
        horizontalNumberPicker.setListener(horizontalNumberPickerListener);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        alertDialog = builder.setView(viewNumberPicker);
        alertDialog.setTitle("Title");
        //ADBuilder.setMessage("Body");
        alertDialog.setPositiveButton("OK", this);
        alertDialog.setNegativeButton("Cancel", this);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == Dialog.BUTTON_POSITIVE) {
            Log.d("x1", "BUTTON_POSITIVE");
        }
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
