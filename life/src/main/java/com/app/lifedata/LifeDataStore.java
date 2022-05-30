package com.app.lifedata;

import java.util.HashMap;
import java.util.Set;

public class LifeDataStore {
    private final HashMap<String, LifecycleData> mMap = new HashMap<>();

    final void put(String key, LifecycleData p) {
        LifecycleData oldP = mMap.put(key, p);
        if (oldP != null) {
            oldP.onDetach();
        }
    }

    final LifecycleData get(String key) {
        return mMap.get(key);
    }

    Set<String> keys() {
        return mMap.keySet();
    }

    public final void clear() {
        mMap.clear();
    }

}
