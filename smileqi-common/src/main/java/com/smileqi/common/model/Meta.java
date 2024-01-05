package com.smileqi.common.model;

import lombok.Data;

@Data
public class Meta {
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