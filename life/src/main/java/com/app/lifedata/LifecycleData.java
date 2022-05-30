package com.app.lifedata;

import androidx.lifecycle.LifecycleEventObserver;

public interface LifecycleData extends LifecycleEventObserver {
    void onDetach();
}