package com.smileqi.common.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class MenuItem {
    private String path;
    private String name;
    private String component;
    private Meta meta;
    private List<MenuItem> children;


        /*= new ArrayList<>();
    public List<MenuItem> getChildren() {
        return children;
    }
    public void setChildren(MenuItem children) {
        this.children.add(children);
    }
    public MenuItem(String path, String name, Meta meta,List<MenuItem> children) {
        this.path = path;
        this.name = name;
        this.meta = meta;
        this.children = children;
    }*/
}