package io.tenmax.oyster.sample;

import java.util.List;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import io.tenmax.oyster.AdLoader;
import io.tenmax.oyster.common.internal.AdListener;
import io.tenmax.oyster.format.NativeAd;
import io.tenmax.oyster.format.NativeAppInstallAd;
import io.tenmax.oyster.format.NativeContentAd;
import io.tenmax.oyster.view.NativeAppInstallAdView;
import io.tenmax.oyster.view.NativeContentAdView;

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
        refreshAd(requestAppInstallAds.isChecked(),
            requestContentAds.isChecked());
      }
    });

    refreshAd(requestAppInstallAds.isChecked(),
        requestContentAds.isChecked());
  }

  private void populateContentAdView(NativeContentAd nativeContentAd,
      NativeContentAdView adView) {
    adView.setHeadlineView(adView.findViewById(R.id.contentad_headline));
    adView.setImageView(adView.findViewById(R.id.contentad_image));
    adView.setBodyView(adView.findViewById(R.id.contentad_body));
    adView.setCallToActionView(adView.findViewById(R.id.contentad_call_to_action));
    adView.setLogoView(adView.findViewById(R.id.contentad_logo));
    adView.setAdvertiserView(adView.findViewById(R.id.contentad_advertiser));

    // Some assets are guaranteed to be in every NativeContentAd.
    ((TextView) adView.getHeadlineView()).setText(nativeContentAd.getHeadline());
    ((TextView) adView.getBodyView()).setText(nativeContentAd.getBody());
    ((TextView) adView.getCallToActionView()).setText(nativeContentAd.getCallToAction());
    ((TextView) adView.getAdvertiserView()).setText(nativeContentAd.getAdvertiser());

    List<NativeAd.Image> images = nativeContentAd.getImages();

    if (images != null && images.size() > 0) {
      ((ImageView) adView.getImageView()).setImageDrawable(images.get(0).getDrawable());
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
    adView.setCallToActionView(adView.findViewById(R.id.appinstall_call_to_action));
    adView.setLogoView(adView.findViewById(R.id.appinstall_app_icon));
    adView.setPriceView(adView.findViewById(R.id.appinstall_price));
    adView.setStarRatingView(adView.findViewById(R.id.appinstall_stars));
    adView.setStoreView(adView.findViewById(R.id.appinstall_store));

    // Some assets are guaranteed to be in every NativeAppInstallAd.
    ((TextView) adView.getHeadlineView()).setText(nativeAppInstallAd.getHeadline());
    ((TextView) adView.getBodyView()).setText(nativeAppInstallAd.getBody());
    ((Button) adView.getCallToActionView()).setText(nativeAppInstallAd.getCallToAction());
    ((ImageView) adView.getLogoView()).setImageDrawable(nativeAppInstallAd.getLogo()
        .getDrawable());

    List<NativeAd.Image> images = nativeAppInstallAd.getImages();

    if (images.size() > 0) {
      ((ImageView) adView.getImageView()).setImageDrawable(images.get(0).getDrawable());
    }

    // Some aren't guaranteed, however, and should be checked.
    if (nativeAppInstallAd.getPrice() == null) {
      adView.getPriceView().setVisibility(View.INVISIBLE);
    } else {
      adView.getPriceView().setVisibility(View.VISIBLE);
      ((TextView) adView.getPriceView()).setText(nativeAppInstallAd.getPrice());
    }

    if (nativeAppInstallAd.getStore() == null) {
      adView.getStoreView().setVisibility(View.INVISIBLE);
    } else {
      adView.getStoreView().setVisibility(View.VISIBLE);
      ((TextView) adView.getStoreView()).setText(nativeAppInstallAd.getStore());
    }

    if (nativeAppInstallAd.getStarRating() == null) {
      adView.getStarRatingView().setVisibility(View.INVISIBLE);
    } else {
      ((RatingBar) adView.getStarRatingView())
          .setRating(nativeAppInstallAd.getStarRating().floatValue());
      adView.getStarRatingView().setVisibility(View.VISIBLE);
    }

    // Assign native ad object to the native view.
    adView.setNativeAd(nativeAppInstallAd);
  }

  private void refreshAd(boolean requestAppInstallAds, boolean requestContentAds) {
    if (!requestAppInstallAds && !requestContentAds) {
      Toast.makeText(this, "At least one ad format must be checked to request an ad.",
          Toast.LENGTH_SHORT).show();
      return;
    }

    AdLoader.Builder builder = new AdLoader.Builder(this, OYSTER_AD_UNIT_ID);

    if (requestAppInstallAds) {
      builder.forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
        @Override
        public void onAppInstallAdLoaded(NativeAppInstallAd nativeAppInstallAd) {
          FrameLayout frameLayout =
              (FrameLayout) findViewById(R.id.adPlace);
          NativeAppInstallAdView adView = (NativeAppInstallAdView) getLayoutInflater()
              .inflate(R.layout.ad_install, null);
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
          FrameLayout frameLayout =
              (FrameLayout) findViewById(R.id.adPlace);
          NativeContentAdView adView = (NativeContentAdView) getLayoutInflater()
              .inflate(R.layout.ad_content, null);
          populateContentAdView(nativeContentAd, adView);
          frameLayout.removeAllViews();
          frameLayout.addView(adView);
        }
      });
    }

    AdLoader adLoader = builder.withAdListener(new AdListener() {
      @Override
      public void onAdFailedToLoad(int errorCode) {
        Toast.makeText(MainActivity.this, "Failed to load native ad: "
            + errorCode, Toast.LENGTH_SHORT).show();
      }
    }).build();

    adLoader.loadAd();
  }
}
