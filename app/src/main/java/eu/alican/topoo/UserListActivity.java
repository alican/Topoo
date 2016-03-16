package eu.alican.topoo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import eu.alican.topoo.db.DataSources;
import eu.alican.topoo.models.User;

public class UserListActivity extends AppCompatActivity {

    ListView userlist;
    ArrayList<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userlist = (ListView) findViewById(R.id.userlist);

        getUsers();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                RegisterFragment newFragment = RegisterFragment.newInstance(true);
                newFragment.show(fragmentManager, "dialog");

            }
        });
    }

    public void getUsers(){

        final DataSources dataSources = new DataSources(this);
        users = new ArrayList<>(dataSources.getUsers());

        final ArrayAdapter<User> userListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, users);

        userlist.setAdapter(userListAdapter);


        userlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                long userid = users.get(position).getId();
                if (userid > 1){

                    new AlertDialog.Builder(UserListActivity.this)
                            .setTitle("User wirklich löschen?")
                            .setPositiveButton(R.string.deleteUser, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dataSources.deleteUserById(users.get(position).getId());
                                    users.remove(position);
                                    userListAdapter.notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(R.drawable.ic_delete_black_24dp)
                            .show();



                }
                return true;
            }
        });

    }







}
