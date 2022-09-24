package com.app.lifedata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LifeDataActivity extends AppCompatActivity implements LifeDataProvider.BaseLifeDataOwner, LifeDataProvider.Factory {
    private LifeDataStore mvpStore;
    private LifeDataProvider provider;

    @Override
    public LifeDataStore getLifeDataStore() {
        return mvpStore == null ? mvpStore = new LifeDataStore() : mvpStore;
    }

    public LifeDataProvider provider() {
        return provider == null ? provider = LifeDataProvider.of(this, this) : provider;
    }

    public <T extends LifecycleData> T getProvider(@NonNull Class<T> modelClass) {
        return provider().get(modelClass);
    }

    public <T extends LifecycleData> T getProvider(@NonNull String key, @NonNull Class<T> modelClass) {
        return provider().get(key, modelClass);
    }

    public <T extends LifecycleData> boolean removeProvider(@NonNull Class<T> modelClass) {
        return provider().remove(modelClass);
    }

    public boolean removeProvider(@NonNull String key) {
        return provider().remove(key);
    }

    @NonNull
    @Override
    public <P extends LifecycleData> P create(@NonNull Class<P> modelClass) {
        return LifeDataProvider.createInstance(modelClass, this);
    }
}
