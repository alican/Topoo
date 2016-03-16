package eu.alican.topoo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import eu.alican.topoo.app.TopooApplication;
import eu.alican.topoo.db.DataSources;
import eu.alican.topoo.models.Todo;
import eu.alican.topoo.models.User;
import eu.alican.topoo.widgets.DatePickerFragment;
import eu.alican.topoo.widgets.TimePickerFragment;

public class TodoDetailActivity extends AppCompatActivity {

    EditText todoText;
    EditText todoName;
    CheckBox checked;
    CheckBox priority;


    TextView labelTime;
    TextView labelDate;
    Todo todo;
    TopooApplication application;
    User user;

    Date deadline;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        application = ((TopooApplication) getApplicationContext());

        if(!application.isAuthenticated()){
            Intent intent = new Intent(this, AuthenticationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        user = application.getUser();

        deadline = Calendar.getInstance().getTime();


        todoText = (EditText) findViewById(R.id.todoTitel);
        todoName = (EditText) findViewById(R.id.todoName);

        checked = (CheckBox) findViewById(R.id.checked);
        priority = (CheckBox) findViewById(R.id.priority);


        labelTime = (TextView) findViewById(R.id.time);
        labelDate = (TextView) findViewById(R.id.date);

        labelTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
            }
        });

        labelDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });


        Button saveButton = (Button) findViewById(R.id.saveButton);
        Button exitButton = (Button) findViewById(R.id.exitButton);


        long todoId = getIntent().getLongExtra("TodoId", -1);


        if (todoId >= 0) {

            DataSources dataSources = new DataSources(TodoDetailActivity.this);
            todo = dataSources.getToDoById(todoId);


            if (todo.getDeadline() != null) {
                deadline = todo.getDeadline();
            }

            todoText.setText(todo.getText());
            todoName.setText(todo.getName());
            checked.setChecked(todo.isChecked());
            priority.setChecked(todo.isPriority());

        }

        setDateTimeLabel();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    saveTodo();
                }
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }


    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }


    private boolean validate(){

        if (todoText.getText().toString().equals("")){
            todoText.setError("Dieses Feld darf nicht leer sein.");
            return false;
        }
        return true;
    }

    private void saveTodo() {

        DataSources dataSources = new DataSources(TodoDetailActivity.this);

        if (todo == null) {
            todo = new Todo(todoText.getText().toString());
            todo.setDeadline(deadline);
            todo.setName(todoName.getText().toString());
            todo.setChecked(checked.isChecked());
            todo.setPriority(priority.isChecked());
            todo.setUser(user.getId());
            dataSources.insertTodo(todo);
        }
        todo.setText(todoText.getText().toString());
        todo.setName(todoName.getText().toString());
        todo.setChecked(checked.isChecked());
        todo.setPriority(priority.isChecked());
        todo.setDeadline(deadline);
        todo.setUser(user.getId());
        dataSources.updateTodo(todo);

        finish();

    }

    public void setDateTimeLabel() {
        SimpleDateFormat time = new SimpleDateFormat("HH:mm");
        SimpleDateFormat date = new SimpleDateFormat("dd. MMM yyyy");
        labelTime.setText(time.format(deadline));
        labelDate.setText(date.format(deadline));

    }


    public void setTime(TimePicker view, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(deadline);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        deadline = calendar.getTime();
        setDateTimeLabel();
    }

    public void setDate(DatePicker view, int year, int month, int day) {
        labelDate.setText(String.format(day + "." + month + "." + year));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(deadline);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        deadline = calendar.getTime();
        setDateTimeLabel();
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onStop() {
        super.onStop();


    }
}
