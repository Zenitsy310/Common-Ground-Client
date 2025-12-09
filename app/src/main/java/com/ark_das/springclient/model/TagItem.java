package com.ark_das.springclient.model;

public class TagItem {


    private int id;
    private String name;
    private boolean selected;

    public TagItem(String name) {
        this.name = name;
        this.selected = false;
    }

    public TagItem(Tag tag) {
        this.id = tag.getId();
        this.name = tag.getName();
        this.selected = false;
    }



    public String getName() {
        return name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TagItem tagToItem(Tag tag){
        return tagToItem(tag);
    }


}
