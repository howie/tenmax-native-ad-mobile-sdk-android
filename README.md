# 步驟 1.下載 Oyster-sdk.aar

1. 放至路徑 your_project/app/libs/ 底下
2. 在build.gradle裡加上
```gradle
dependencies {
    ...
  compile(name: 'oyster-sdk', ext: 'aar')
    ...
}
```

# 步驟 2.下載 goole play service

在build.gradle加上
```gradle
dependencies {
    ...
  compile 'com.google.android.gms:play-services-basement:9.0.2'
    ...
}
```
>如果已經有compile 'com.google.android.gms:play-services-ads'，可以略過此步驟

# 步驟 3.取得AdUnitID

從tenmax取得一個唯一廣告識別碼

# 步驟 4.加入以下程式碼

OYSTER_AD_UNIT_ID為步驟3拿到的唯一廣告識別碼，記得填進去喔。

```java

OysterAdLoader builder = new OysterAdLoader.Builder(context, OYSTER_AD_UNIT_ID) 
    .forContentAd(new OysterContentAd.OnContentAdLoadedListener() {
      @Override
      public void onContentAdLoaded(OysterContentAd oysterContentAd) {
        // Show the content ad.
      }
    }) 
    .withAdListener(new OysterAdListener() {
      @Override
      public void onAdFailedToLoad(OysterException e) {
        // Handle the failure by logging, altering the UI, etc.
      }
    }) 
    .withAdOysterAdOption(new OysterAdOption.Builder()
        // Methods in the OysterAdOption.Builder class can be
        // used here to specify individual options settings.
        .build())
    .build()
    .loadAd();
        
```

# 步驟 5.用OysterContentAdView製作layout

OysterContentAdView是一個ViewGroup，可視為一容器, 將想要呈現的廣告內容放入此容器內

```xml
<io.tenmax.oyster.OysterContentAdView
        xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/my_content_ad"
>
    ... 想呈現的廣告內容 ...
</io.tenmax.oyster.OysterContentAdView>
```

> 注意, 如果沒有使用OysterContentAdView則廣告不起作用

# 步驟 6.coding放入廣告內容

```java
private void displayContentAd(OysterContentAd contentAd) {
      NativeContentAdView adView = (NativeContentAdView) findViewById(R.id.my_content_ad);
      TextView headlineView = (TextView) adView.findViewById(R.id.contentad_headline);
      headlineView.setText(contentAd.getHeadline());
      adView.setHeadlineView(headlineView);

      ...
      // 重複以上的程式碼來新增廣告 (Buttons, ImageViews, etc).
      ...

      // 註冊OysterAd objcet
      adView.setOysterAd(contentAd);
 }
```
# 加入一個Banner Ad

# 步驟1. 加入一個OysterAdView
```xml
<io.tenmax.oyster.OysterAdView android:layout_width="match_parent"
                                   android:layout_height="wrap_content"
                                   android:id="@+id/oyster_ad_view"
                                   app:oysterAdUnitId="YOUR_AD_UNIT_ID"
/>

```
# 步驟2. 加入以下程式碼
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    OysterAdView oysterAdView = (OysterAdView) findViewById(R.id.oyster_ad_view);
    oysterAdView.loadAd();
}

```

