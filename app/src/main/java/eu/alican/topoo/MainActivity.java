package eu.alican.topoo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.SortedList;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appdatasearch.GetRecentContextCall;

import java.util.List;

import eu.alican.topoo.adapter.TodoAdapter;
import eu.alican.topoo.app.TopooApplication;
import eu.alican.topoo.db.DataSources;
import eu.alican.topoo.models.Todo;
import eu.alican.topoo.models.User;

public class MainActivity extends AppCompatActivity  {

    ListView listView;
    DataSources dataSources;
    User user;
    TopooApplication application;
    boolean showChecked;
    boolean orderByPriority;
    TodoAdapter todoAdapter;

    final int MENU_BENUTZERVERWALTUNG = 110;
    final int MENU_CHECKEDONOFF = 111;

    SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        application = ((TopooApplication) getApplicationContext());

        prefs = this.getSharedPreferences("eu.alican.Toppp", Context.MODE_PRIVATE);
        showChecked = prefs.getBoolean("checked", false);
        orderByPriority = prefs.getBoolean("priority", false);

        if(!application.isAuthenticated()){
            Intent intent = new Intent(this, AuthenticationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        user = application.getUser();

        listView = (ListView) findViewById(R.id.listView);

        dataSources = new DataSources(MainActivity.this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, TodoDetailActivity.class));
            }
        });


    }



    public void fetchData(){

        List<Todo> todoList = dataSources.getTodos(user.getId(), showChecked, orderByPriority);

        todoAdapter = new TodoAdapter(MainActivity.this, todoList);

        listView.setAdapter(todoAdapter);
        todoAdapter.setOnCheckedListener(new TodoAdapter.OnCheckedListener() {
            @Override
            public void onCheckClicked() {
                fetchData();
            }
        });

    }

    @Override
    protected void onResume() {

        fetchData();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (application.isAdmin()){
            menu.add(0, MENU_BENUTZERVERWALTUNG, Menu.NONE, R.string.benutzerverwaltung);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.removeItem(MENU_CHECKEDONOFF);
        if(showChecked){
            menu.add(0, MENU_CHECKEDONOFF, Menu.NONE, "Erledigte Todos einblenden");
        }else{
            menu.add(0, MENU_CHECKEDONOFF, Menu.NONE, "Erledigte Todos ausblenden");
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        switch (id){
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.logout_settings:
                application.logoutUser();
                Intent intent = new Intent(this, AuthenticationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;
            case MENU_BENUTZERVERWALTUNG:
                startActivity(new Intent(this, UserListActivity.class));
                break;
            case MENU_CHECKEDONOFF:
                showChecked = !showChecked;
                prefs.edit().putBoolean("checked", showChecked).apply();
                fetchData();
                break;
            case R.id.action_deleteAll:


                new AlertDialog.Builder(this)
                        .setTitle("Alle Eintäge entfernen?")
                        .setPositiveButton(R.string.deleteAll, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dataSources.deleteAllTodos(user.getId());
                                fetchData();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(R.drawable.ic_delete_black_24dp)
                        .show();
                break;
            case R.id.action_sortPriorityTime:
                orderByPriority = true;
                prefs.edit().putBoolean("priority", orderByPriority).apply();
                fetchData();
                break;
            case R.id.action_sortTime:
                orderByPriority = false;
                prefs.edit().putBoolean("priority", orderByPriority).apply();
                fetchData();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


}
