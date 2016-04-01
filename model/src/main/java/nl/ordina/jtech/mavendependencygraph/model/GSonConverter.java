package nl.ordina.jtech.mavendependencygraph.model;

import com.google.gson.Gson;

import java.io.Serializable;

public interface GSonConverter extends Serializable {
    Gson gson = new Gson();

    static <T> T fromJson(final String input, Class<T> classOfT) {
        return gson.fromJson(input, classOfT);
    }

    default String toJson() {
        return gson.toJson(this);
    }
}
