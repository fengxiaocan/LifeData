package com.app.lifedata;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class LifeDataFragment extends Fragment implements LifeDataProvider.BaseLifeDataOwner, LifeDataProvider.Factory {
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

    @NonNull
    @Override
    public <P extends LifecycleData> P create(@NonNull Class<P> modelClass) {
        return LifeDataProvider.createInstance(modelClass, getContext());
    }
}
