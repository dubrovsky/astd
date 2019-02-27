package com.isc.astd.web.commons;

import java.util.Collection;

/**
 * @author p.dzeviarylin
 */
public class Response<T> {

    private boolean success = true;
    private Collection<T> items;
    private T item;
    private String message;
    private long total = 1;

    public Response(Collection<T> items) {
        this.items = items;
    }

    public Response(Collection<T> items, long total) {
        this.items = items;
        this.total = total;
    }

    public Response(T item) {
        this.item = item;
    }

    public Response(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public Response() {
    }

    public Collection<T> getItems() {
        return items;
    }

    public boolean isSuccess() {
        return success;
    }

    public T getItem() {
        return item;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Response{" +
          "success=" + success +
          ", message='" + message + '\'' +
          '}';
    }

    public long getTotal() {
        return total;
    }
}
