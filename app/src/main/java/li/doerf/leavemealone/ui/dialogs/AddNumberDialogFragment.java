package li.doerf.leavemealone.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.joda.time.DateTime;

import li.doerf.leavemealone.R;
import li.doerf.leavemealone.db.AloneSQLiteHelper;
import li.doerf.leavemealone.db.tables.PhoneNumber;
import li.doerf.leavemealone.db.tables.PhoneNumberSource;
import li.doerf.leavemealone.util.PhoneNumberHelper;

/**
 * The fragment used to add new numbers.
 *
 * Created by moo on 30/11/15.
 */
public class AddNumberDialogFragment extends DialogFragment {
    private final String LOGTAG = getClass().getSimpleName();
    private String myNumber;
    private String myName;
    private NumberAddedListener myListener;

    public interface NumberAddedListener {
        void numberAdded(PhoneNumber aNumber);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            myListener = (NumberAddedListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
//            throw new ClassCastException(activity.toString()
//                    + " must implement NumberAddedListener");
            Log.w(LOGTAG, "activity does not implement NumberAddedListener. no notification");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View view = inflater.inflate(R.layout.dialog_add_number, null);

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        myNumber = ((EditText) view.findViewById(R.id.number)).getText().toString();
                        myNumber = PhoneNumberHelper.normalize(getContext(), myNumber);
                        myName = ((EditText) view.findViewById(R.id.name)).getText().toString();
                        addPhoneNumberToDb(myNumber, myName);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        myNumber = null;
                        myName = null;
                        AddNumberDialogFragment.this.getDialog().cancel();
                    }
                });

        final AlertDialog dialog = builder.create();

        // enable number validation on key pressed
        EditText numberInput = (EditText) view.findViewById(R.id.number);
        numberInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    // validate entered number and set
                    String number = ((EditText) v).getText().toString();
                    Log.d(LOGTAG, "validating number: " + number);
                    boolean isValid = PhoneNumberHelper.isValid(getContext(), number);
                    Button buttonNo = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    buttonNo.setEnabled(isValid);
                }

                return false;
            }
        });

        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();

        // disable positive key initially (no number)
        Button buttonNo = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);
        buttonNo.setEnabled(false);

    }

    private void addPhoneNumberToDb(String aNumber, String name) {
        if (aNumber == null || aNumber.trim().equals("")) {
            throw new IllegalArgumentException("number not set");
        }

        if (name == null || name.trim().equals("")) {
            name = "<Not set>";
        }

        SQLiteDatabase db = AloneSQLiteHelper.getInstance(getContext()).getWritableDatabase();
        PhoneNumberSource source = PhoneNumberSource.update(db, "manual");
        PhoneNumber number = PhoneNumber.create(source, aNumber, name, DateTime.now());
        number.insert(db);
        myListener.numberAdded(number);
    }
}
