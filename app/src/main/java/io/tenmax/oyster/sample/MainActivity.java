package io.tenmax.oyster.sample;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import io.tenmax.oyster.AdListener;
import io.tenmax.oyster.AdLoader;
import io.tenmax.oyster.NativeAd;
import io.tenmax.oyster.NativeContentAd;
import io.tenmax.oyster.NativeContentAdView;
import io.tenmax.oyster.NativeException;

public class MainActivity extends AppCompatActivity {

  private final static String OYSTER_AD_UNIT_ID = "OYSTER_AD_UNIT_ID";
  private RecyclerAdapter adapter;
  private List<Object> data = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    RecyclerView recycler = ((RecyclerView) findViewById(R.id.recycler));
    recycler.setLayoutManager(new LinearLayoutManager(this));
    adapter = new RecyclerAdapter(data);
    requestAds(10);
    requestAds(18);
    for (int i = 0; i < 30; i++) {
      data.add(String.valueOf(i));
    }
    recycler.setAdapter(adapter);
  }

  private void requestAds(final int position) {
    AdLoader.Builder builder = new AdLoader.Builder(this, OYSTER_AD_UNIT_ID);
    builder.forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
      @Override
      public void onContentAdLoaded(NativeContentAd nativeContentAd) {
        data.add(position, nativeContentAd);
        adapter.notifyItemInserted(position);
      }
    });
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

  class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final int VIEW_TYPE_NORMAL = 0;
    final int VIEW_TYPE_NATIVE_AD = 1;
    private List<Object> data;

    RecyclerAdapter(List<Object> data) {
      this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      if (viewType == VIEW_TYPE_NATIVE_AD) {
        return new NativeAdHolder((NativeContentAdView) LayoutInflater.from(parent.getContext())
            .inflate(R.layout.ad_content, parent, false));
      } else if (viewType == VIEW_TYPE_NORMAL) {
        return new ItemHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.recycler_item, parent, false));
      }
      return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
      if (holder instanceof NativeAdHolder) {
        ((NativeAdHolder) holder).bind(((NativeContentAd) data.get(position)));
      } else if (holder instanceof ItemHolder) {
        ((ItemHolder) holder).bind(((String) data.get(position)));
      }
    }

    @Override
    public int getItemViewType(int position) {
      Object o = data.get(position);
      if (o instanceof String) {
        return VIEW_TYPE_NORMAL;
      } else if (o instanceof NativeContentAd) {
        return VIEW_TYPE_NATIVE_AD;
      }
      throw new RuntimeException("Unknown view type in PublicationAdapter");
    }

    @Override
    public int getItemCount() {
      return data.size();
    }

    class NativeAdHolder extends RecyclerView.ViewHolder {

      private final NativeContentAdView adView;

      public NativeAdHolder(NativeContentAdView adView) {
        super(adView);
        this.adView = adView;
        adView.setHeadlineView(adView.findViewById(R.id.contentad_headline));
        adView.setImageView(adView.findViewById(R.id.contentad_image));
        adView.setBodyView(adView.findViewById(R.id.contentad_body));
        adView.setLogoView(adView.findViewById(R.id.contentad_logo));
        adView.setAdvertiserView(adView.findViewById(R.id.contentad_advertiser));
      }

      public void bind(NativeContentAd nativeContentAd) {
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
    }

    class ItemHolder extends RecyclerView.ViewHolder {

      private final TextView text;

      public ItemHolder(View itemView) {
        super(itemView);
        text = (TextView) itemView.findViewById(R.id.text);
      }

      public void bind(String data) {
        text.setText(data);
      }
    }
  }

}
