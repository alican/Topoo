package eu.alican.topoo.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import eu.alican.topoo.R;
import eu.alican.topoo.TodoDetailActivity;
import eu.alican.topoo.db.DataSources;
import eu.alican.topoo.models.Todo;

/**
 * Project: Topoo
 * Created by alican on 14.03.2016.
 */
public class TodoAdapter extends ArrayAdapter<Todo> {
    List<Todo> todos;
    private OnCheckedListener listener;
    DataSources dataSources = new DataSources(getContext());


    public TodoAdapter(Context context, List<Todo> todos) {
        super(context, 0, todos);
        this.todos = todos;
        this.listener = null;
    }


    public void setOnCheckedListener(OnCheckedListener onCheckedListener){
        this.listener = onCheckedListener;
    }

    public interface OnCheckedListener {
        void onCheckClicked();

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_todo, parent, false);
        TextView todoText = (TextView) convertView.findViewById(R.id.todoText);
        CheckBox todoChecked = (CheckBox) convertView.findViewById(R.id.checkBox);
        final ImageView priorityIcon = (ImageView) convertView.findViewById(R.id.star);

        if (!getItem(position).isPriority()){
            priorityIcon.setVisibility(View.INVISIBLE);
        }


        todoText.setText(getItem(position).getText());
        todoChecked.setChecked(getItem(position).isChecked());

        todoChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getItem(position).setChecked(isChecked);
                dataSources.updateTodo(getItem(position));
                if (listener != null){
                    listener.onCheckClicked();
                }
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TodoDetailActivity.class);
                intent.putExtra("TodoId", getItem(position).getId());
                getContext().startActivity(intent);

            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popup = new PopupMenu(getContext(), v);
                popup.getMenuInflater()
                        .inflate(R.menu.todo_menu_popup, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.menu_delete:
                                dataSources.deleteTodoById(getItem(position).getId());
                                todos.remove(position);
                                notifyDataSetChanged();
                                break;
                            case R.id.menu_edit:
                                Intent intent = new Intent(getContext(), TodoDetailActivity.class);
                                intent.putExtra("TodoId", getItem(position).getId());
                                getContext().startActivity(intent);
                                break;
                            case R.id.menu_priority:
                                getItem(position).setPriority((!getItem(position).isPriority()));
                                dataSources.updateTodo(getItem(position));
                                if (!getItem(position).isPriority()){
                                    priorityIcon.setVisibility(View.INVISIBLE);
                                }else{
                                    priorityIcon.setVisibility(View.VISIBLE);
                                }
                                if (listener != null){
                                    listener.onCheckClicked();
                                }
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
                return true;
            }
        });

        return convertView;
    }
}