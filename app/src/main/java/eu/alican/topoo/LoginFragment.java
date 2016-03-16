package eu.alican.topoo;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    EditText usernameField;
    EditText passwordField;
    TopooApplication application;
    TextView message;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        application = ((TopooApplication) getActivity().getApplicationContext());

        usernameField = (EditText) view.findViewById(R.id.username);
        passwordField = (EditText) view.findViewById(R.id.password);

        message = (TextView) view.findViewById(R.id.message);
        message.setVisibility(View.INVISIBLE);

        Button sendButton = (Button) view.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()){
                    loginUser();
                }

            }
        });

        super.onViewCreated(view, savedInstanceState);
    }


    public Boolean validate(){


        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        if (username.length() < 2 || username.length() > 20){
            usernameField.setError("Dein Username muss 2 bis 20 Zeichen haben.");
            return false;
        }
        if (!isValidPassword(password)){
            passwordField.setError("Zugelassen sind nur Klein-/Großbuchstaben mit mindestens einer Zahl und eine Länge zwischen 5-20 Zeichen.");
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


    public void loginUser(){

        DataSources dataSources = new DataSources(getContext());
        User user = dataSources.getUserByName(usernameField.getText().toString(), passwordField.getText().toString());

        if(user != null){
            application.authenticateUser(user);
            startActivity(new Intent(getContext(), MainActivity.class));
            getActivity().finish();
        }else {
            message.setVisibility(View.VISIBLE);
        }
    }
}
