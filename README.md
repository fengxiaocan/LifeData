# LifeData

#使用方法:

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
    
        @NonNull
        @Override
        public <P extends LifecycleData> P create(@NonNull Class<P> modelClass) {
            return LifeDataProvider.createInstance(modelClass, this);
        }
    }
  
  在继承的Activity中如下使用,可以避免重复创建
  
      public class MainActivity extends LifeDataActivity{
            ...
            public void xxx(){
                getProvider(xLoadDialog.class).show();
            }
            ...
      }
      
另外的用法功能请看[BaseMvp](https://github.com/fengxiaocan/BaseMvp)
