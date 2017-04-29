package ua.com.vando.apk_airplan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

public class DialogNumberPicker extends DialogFragment  implements DialogInterface.OnClickListener {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        AlertDialog.Builder ADBuilder = builder.setView(inflater.inflate(R.layout.dialog_number_picker, null));
        ADBuilder.setTitle("Title");
        ADBuilder.setMessage("Body");
        ADBuilder.setPositiveButton("OK", this);
        ADBuilder.setNegativeButton("Cancel", this);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == Dialog.BUTTON_POSITIVE) {
            Log.d("x1", "BUTTON_POSITIVE");
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.d("x1", "onDismiss");
    }

}
