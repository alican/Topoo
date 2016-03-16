package eu.alican.topoo;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.alican.topoo.app.TopooApplication;
import eu.alican.topoo.db.DataSources;
import eu.alican.topoo.models.User;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends DialogFragment {


    EditText usernameField;
    EditText password1Field;
    EditText password2Field;
    TopooApplication application;
    TextView message;
    TextView label;
    boolean dialog;


    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(boolean dialog) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putBoolean("dialog", dialog);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            dialog = getArguments().getBoolean("dialog");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        application = ((TopooApplication) getActivity().getApplicationContext());

        Button sendButton = (Button) view.findViewById(R.id.sendButton);
        usernameField = (EditText) view.findViewById(R.id.username);
        password1Field = (EditText) view.findViewById(R.id.password1);
        password2Field = (EditText) view.findViewById(R.id.password2);
        message = (TextView) view.findViewById(R.id.message);
        label = (TextView) view.findViewById(R.id.textView);
        message.setVisibility(View.INVISIBLE);

        if(dialog){
            label.setText("Benutzer hinzufügen");
        }
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            if (validate()){
                registerUser();
            }
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }



    public Boolean validate(){


        String username = usernameField.getText().toString();
        String password1 = password1Field.getText().toString();
        String password2 = password2Field.getText().toString();
        if (username.length() < 2 || username.length() > 20){
            usernameField.setError("Dein Username muss 2 bis 20 Zeichen haben.");
            return false;
        }
        if (!isValidPassword(password1)){
            password1Field.setError("Zugelassen sind nur Klein-/Großbuchstaben mit mindestens einer Zahl und eine Länge zwischen 5-20 Zeichen.");
            return false;

        }
        if (!password1.equals(password2)){
            password2Field.setError("Passwort stimmt nicht mit dem ersten überein.");
            return false;

        }

        return true;
    }

    public boolean isValidPassword(String password){
        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "(?!^[0-9]*$)(?!^[a-zA-Z]*$)^([a-zA-Z0-9]{5,20})$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }


    public void registerUser(){

        User user = new User();
        user.setName(usernameField.getText().toString());

        DataSources dataSources = new DataSources(getContext());
        dataSources.insertUser(user, password1Field.getText().toString());

        user = dataSources.getUserByName(user.getName(), password1Field.getText().toString());

        if(user != null){
            if(!dialog){
                application.authenticateUser(user);
                startActivity(new Intent(getContext(), MainActivity.class));
                getActivity().finish();

            }else{
                ((UserListActivity)getActivity()).getUsers();
                dismiss();
            }

        }else {
            message.setVisibility(View.VISIBLE);
        }




    }
}
