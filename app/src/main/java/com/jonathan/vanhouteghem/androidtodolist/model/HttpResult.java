package com.jonathan.vanhouteghem.androidtodolist.model;

/**
 * Created by sca on 27/08/16.
 */
public class HttpResult {

    public int code;
    public String json;

    public HttpResult(int code, String s) {
        this.code = code;
        this.json = s;
    }
}