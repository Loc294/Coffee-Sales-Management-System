package org.example.event;

import java.util.ArrayList;
import java.util.List;

public class DataChangeEventBus {

    public interface DataChangeListener {
        void onDataChanged();
    }

    private static final List<DataChangeListener> listeners = new ArrayList<>();

    public static void onRegister(DataChangeListener listener) {
        listeners.add(listener);
    }

    public static void notifyChange() {
        for (DataChangeListener l : listeners) {
            l.onDataChanged();
        }
    }
}
