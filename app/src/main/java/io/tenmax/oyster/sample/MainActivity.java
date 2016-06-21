package io.tenmax.oyster.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import io.tenmax.oyster.AdListener;
import io.tenmax.oyster.AdLoader;
import io.tenmax.oyster.NativeAd;
import io.tenmax.oyster.NativeAppInstallAd;
import io.tenmax.oyster.NativeAppInstallAdView;
import io.tenmax.oyster.NativeContentAd;
import io.tenmax.oyster.NativeContentAdView;
import io.tenmax.oyster.NativeException;

public class MainActivity extends AppCompatActivity {

  private final static String OYSTER_AD_UNIT_ID = "unit_id";
  private Button refresh;
  private CheckBox requestAppInstallAds;
  private CheckBox requestContentAds;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    refresh = (Button) findViewById(R.id.refresh);
    requestAppInstallAds = (CheckBox) findViewById(R.id.appInstall);
    requestContentAds = (CheckBox) findViewById(R.id.appContent);

    refresh.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        refreshAd(requestAppInstallAds.isChecked(), requestContentAds.isChecked());
      }
    });

    refreshAd(requestAppInstallAds.isChecked(), requestContentAds.isChecked());
  }

  private void populateContentAdView(NativeContentAd nativeContentAd, NativeContentAdView adView) {
    adView.setHeadlineView(adView.findViewById(R.id.contentad_headline));
    adView.setImageView(adView.findViewById(R.id.contentad_image));
    adView.setBodyView(adView.findViewById(R.id.contentad_body));
    adView.setLogoView(adView.findViewById(R.id.contentad_logo));
    adView.setAdvertiserView(adView.findViewById(R.id.contentad_advertiser));

    // Some assets are guaranteed to be in every NativeContentAd.
    ((TextView) adView.getHeadlineView()).setText(nativeContentAd.getHeadline());
    ((TextView) adView.getBodyView()).setText(nativeContentAd.getBody());
    ((TextView) adView.getAdvertiserView()).setText(nativeContentAd.getAdvertiser());

    if (nativeContentAd.getImage().getDrawable() != null) {
      ((ImageView) adView.getImageView()).setImageDrawable(nativeContentAd.getImage()
          .getDrawable());
    }

    // Some aren't guaranteed, however, and should be checked.
    NativeAd.Image logoImage = nativeContentAd.getLogo();

    if (logoImage == null) {
      adView.getLogoView().setVisibility(View.INVISIBLE);
    } else {
      ((ImageView) adView.getLogoView()).setImageDrawable(logoImage.getDrawable());
      adView.getLogoView().setVisibility(View.VISIBLE);
    }

    // Assign native ad object to the native view.
    adView.setNativeAd(nativeContentAd);
  }

  private void populateAppInstallAdView(NativeAppInstallAd nativeAppInstallAd,
      NativeAppInstallAdView adView) {
    adView.setHeadlineView(adView.findViewById(R.id.appinstall_headline));
    adView.setImageView(adView.findViewById(R.id.appinstall_image));
    adView.setBodyView(adView.findViewById(R.id.appinstall_body));
    adView.setLogoView(adView.findViewById(R.id.appinstall_app_icon));

    // Some assets are guaranteed to be in every NativeAppInstallAd.
    ((TextView) adView.getHeadlineView()).setText(nativeAppInstallAd.getHeadline());
    ((TextView) adView.getBodyView()).setText(nativeAppInstallAd.getBody());
    ((ImageView) adView.getLogoView()).setImageDrawable(nativeAppInstallAd.getLogo().getDrawable());

    if (nativeAppInstallAd.getImage().getDrawable() != null) {
      ((ImageView) adView.getImageView()).setImageDrawable(nativeAppInstallAd.getImage()
          .getDrawable());
    }
    // Assign native ad object to the native view.
    adView.setNativeAd(nativeAppInstallAd);
  }

  private void refreshAd(boolean requestAppInstallAds, boolean requestContentAds) {
    if (!requestAppInstallAds && !requestContentAds) {
      Toast.makeText(this,
          "At least one ad format must be checked to request an ad.",
          Toast.LENGTH_SHORT).show();
      return;
    }

    AdLoader.Builder builder = new AdLoader.Builder(this, OYSTER_AD_UNIT_ID);

    if (requestAppInstallAds) {
      builder.forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
        @Override
        public void onAppInstallAdLoaded(NativeAppInstallAd nativeAppInstallAd) {
          FrameLayout frameLayout = (FrameLayout) findViewById(R.id.adPlace);
          NativeAppInstallAdView adView = (NativeAppInstallAdView) getLayoutInflater().inflate(R.layout.ad_install,
              null);
          populateAppInstallAdView(nativeAppInstallAd, adView);
          frameLayout.removeAllViews();
          frameLayout.addView(adView);
        }
      });
    }

    if (requestContentAds) {
      builder.forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
        @Override
        public void onContentAdLoaded(NativeContentAd nativeContentAd) {
          FrameLayout frameLayout = (FrameLayout) findViewById(R.id.adPlace);
          NativeContentAdView adView = (NativeContentAdView) getLayoutInflater().inflate(R.layout.ad_content,
              null);
          populateContentAdView(nativeContentAd, adView);
          frameLayout.removeAllViews();
          frameLayout.addView(adView);
        }
      });
    }

    AdLoader adLoader = builder.withAdListener(new AdListener() {
      @Override
      public void onAdFailedToLoad(NativeException e) {
        Toast.makeText(MainActivity.this,
            "Failed to load native ad: " + e.getMessage(),
            Toast.LENGTH_SHORT).show();
      }
    }).build();

    adLoader.loadAd();
  }
}
