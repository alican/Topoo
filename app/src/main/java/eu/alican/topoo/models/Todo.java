package eu.alican.topoo.models;

import java.util.Date;

/**
 * Project: Topoo
 * Created by alican on 14.03.2016.
 */
public class Todo {
    Long id;
    String name;
    String text;
    Date created;
    Date deadline;
    long user;
    boolean priority;
    boolean checked;

    public Todo(Long id, String name, String text, Date created, Date deadline, long user, boolean priority, boolean checked) {
        this.id = id;
        this.name = name;
        this.text = text;
        this.created = created;
        this.deadline = deadline;
        this.user = user;
        this.priority = priority;
        this.checked = checked;
    }

    public Todo(String text){
        this.id = null;
        this.text = text;
    }

    public Todo(long id, String text) {
        this.id = id;
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getDeadline() {
        return this.deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public boolean isPriority() {
        return priority;
    }

    public void setPriority(boolean priority) {
        this.priority = priority;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUser() {
        return user;
    }

    public void setUser(long user) {
        this.user = user;
    }
}
