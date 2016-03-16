package eu.alican.topoo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.alican.topoo.app.TopooApplication;
import eu.alican.topoo.db.DataSources;
import eu.alican.topoo.models.User;

public class SettingsActivity extends AppCompatActivity {

    TopooApplication application;
    User user;
    EditText oldpasswordField;
    EditText password1Field;
    EditText password2Field;
    Button saveButton;
    Button exitButton;
    TextView usernameLabel;
    DataSources dataSources;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        application = ((TopooApplication) getApplicationContext());

        dataSources = new DataSources(this);

        if(!application.isAuthenticated()){
            Intent intent = new Intent(this, AuthenticationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }




        oldpasswordField = (EditText) findViewById(R.id.password);
        password1Field = (EditText) findViewById(R.id.password1);
        password2Field = (EditText) findViewById(R.id.password2);
        saveButton = (Button) findViewById(R.id.saveButton);
        exitButton = (Button) findViewById(R.id.exitButton);
        usernameLabel = (TextView) findViewById(R.id.username);

        user = application.getUser();
        usernameLabel.setText(user.getName());

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    changePassword(password1Field.getText().toString());
                    Toast.makeText(getApplicationContext(), "Passwort wurde gespeichert", Toast.LENGTH_SHORT).show();
                    finish();

                }

            }
        });
    }

    public Boolean validate(){

        String oldpassword = oldpasswordField.getText().toString();
        String password1 = password1Field.getText().toString();
        String password2 = password2Field.getText().toString();

        User user = dataSources.getUserByName(application.getUser().getName(), oldpassword);

        if (user == null){
            oldpasswordField.setError("Ihr aktuelles Passwort ist falsch");
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


    private void changePassword(String password){

        DataSources dataSources = new DataSources(SettingsActivity.this);
        dataSources.updatePassword(user, password);
    }

}
