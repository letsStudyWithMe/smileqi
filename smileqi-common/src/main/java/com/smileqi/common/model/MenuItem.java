package com.smileqi.common.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class MenuItem {
    private String path;
    private String name;
    private Meta meta;
    private List<MenuItem> children;

    public MenuItem(String path, String name, Meta meta) {
        this.path = path;
        this.name = name;
        this.meta = meta;
        this.children = new ArrayList<>();
    }

    public void addChild(MenuItem child) {
        this.children.add(child);
    }
}

@Data
class Meta {
    private String locale;
    private boolean requiresAuth;
    private String icon;
    private int order;

    public Meta(String locale, boolean requiresAuth, String icon, int order) {
        this.locale = locale;
        this.requiresAuth = requiresAuth;
        this.icon = icon;
        this.order = order;
    }
}