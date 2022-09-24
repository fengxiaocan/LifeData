package com.app.lifedata;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import java.lang.reflect.Constructor;

public class LifeDataProvider implements LifecycleEventObserver {

    private final Factory providerFactory;
    private final LifeDataStore lifeDataStore;
    private Lifecycle mLifecycle;

    public LifeDataProvider(@NonNull LifeDataStore store, Factory factory, @NonNull Lifecycle lifecycle) {
        providerFactory = factory;
        lifeDataStore = store;
        this.mLifecycle = lifecycle;
        lifecycle.addObserver(this);
    }

    public static LifeDataProvider of(BaseLifeDataOwner owner) {
        return of(owner, null);
    }

    public static LifeDataProvider of(BaseLifeDataOwner owner, Factory factory) {
        return of(owner, factory, owner.getLifecycle());
    }

    public static LifeDataProvider of(LifeDataStoreOwner owner, Factory factory, Lifecycle lifecycle) {
        return new LifeDataProvider(owner.getLifeDataStore(), factory, lifecycle);
    }

    public static <P extends LifecycleData> P get(@NonNull Lifecycle lifecycle, @NonNull Class<P> modelClass) {
        P p = createInstance(modelClass);
        lifecycle.addObserver(p);
        return p;
    }

    public static <P extends LifecycleData> P createInstance(@NonNull Class<P> modelClass) {
        try {
            return modelClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        }
    }

    public static <P extends LifecycleData> P createInstance(@NonNull Class<P> modelClass, Context context) {
        try {
            if (context != null) {
                Constructor<P> constructor = modelClass.getDeclaredConstructor(Context.class);
                if (constructor != null) {
                    constructor.setAccessible(true);
                    return constructor.newInstance(context);
                }
            }
            return modelClass.newInstance();
        } catch (Exception e) {
            try {
                return modelClass.newInstance();
            } catch (Exception e1) {
                e1.printStackTrace();
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            }
        }
    }

    public static Factory factory(Context context) {
        return new InstanceFactory(context);
    }

    @NonNull
    public <P extends LifecycleData> P get(@NonNull Class<P> modelClass) {
        String canonicalName = modelClass.getCanonicalName();
        if (canonicalName == null) {
            throw new IllegalArgumentException("Local and anonymous classes can not be LifecycleData");
        }
        return get(canonicalName, modelClass);
    }

    @NonNull
    public <P extends LifecycleData> P get(@NonNull String key, @NonNull Class<P> modelClass) {
        LifecycleData lifecycleData = lifeDataStore.get(key);
        if (modelClass.isInstance(lifecycleData)) {
            return (P) lifecycleData;
        } else {
            if (lifecycleData != null) {
                //Log.d(TAG,"is different");
            }
        }
        if (providerFactory == null) {
            lifecycleData = createInstance(modelClass);
        } else {
            lifecycleData = providerFactory.create(modelClass);
        }
        lifeDataStore.put(key, lifecycleData);
        return (P) lifecycleData;
    }


    @NonNull
    public <P extends LifecycleData> boolean remove(@NonNull Class<P> modelClass) {
        String canonicalName = modelClass.getCanonicalName();
        return lifeDataStore.remove(canonicalName);
    }

    @NonNull
    public boolean remove(@NonNull String key) {
        return lifeDataStore.remove(key);
    }

    public void put(@NonNull LifecycleData presenter) {
        put(presenter.getClass().getCanonicalName(), presenter);
    }

    public void put(@NonNull String key, @NonNull LifecycleData presenter) {
        lifeDataStore.put(key, presenter);
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        for (String key : lifeDataStore.keys()) {
            lifeDataStore.get(key).onStateChanged(source, event);
        }
        if (event == Lifecycle.Event.ON_DESTROY) {
            lifeDataStore.clear();
            if (mLifecycle != null) {
                mLifecycle.removeObserver(this);
                mLifecycle = null;
            }
        }
    }

    public interface BaseLifeDataOwner extends LifecycleOwner, LifeDataStoreOwner {
    }

    public interface LifeDataStoreOwner {
        LifeDataStore getLifeDataStore();
    }

    public interface Factory {
        @NonNull
        <P extends LifecycleData> P create(@NonNull Class<P> modelClass);
    }

    public static class InstanceFactory implements Factory {
        Context context;

        public InstanceFactory(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public <P extends LifecycleData> P create(@NonNull Class<P> modelClass) {
            return createInstance(modelClass, context);
        }
    }
}