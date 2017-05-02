package ua.com.vando.apk_airplan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

public class DialogNumberPicker extends DialogFragment implements DialogInterface.OnClickListener {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View viewNP = inflater.inflate(R.layout.dialog_number_picker, null);
        //viewNP.findViewById(R.layout.horizontal_number_picker);
        //viewNP.findViewById(R.layout.dialog_number_picker);
        //viewNP.findViewById(1);

        AlertDialog.Builder ADBuilder = builder.setView(viewNP);
        ADBuilder.setTitle("Title");
        //ADBuilder.setMessage("Body");
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
}
