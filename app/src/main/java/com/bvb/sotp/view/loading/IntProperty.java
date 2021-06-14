package com.bvb.sotp.view.loading;

import android.util.Property;

public abstract class IntProperty<T> extends Property<T, Integer> {

    IntProperty(String name) {
        super(Integer.class, name);
    }

    public abstract void setValue(T object, int value);

    @Override
    final public void set(T object, Integer value) {
        setValue(object, value);
    }

}