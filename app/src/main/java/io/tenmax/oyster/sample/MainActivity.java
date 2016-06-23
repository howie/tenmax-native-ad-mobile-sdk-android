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
import io.tenmax.oyster.OysterAd;
import io.tenmax.oyster.OysterAdListener;
import io.tenmax.oyster.OysterAdLoader;
import io.tenmax.oyster.OysterContentAd;
import io.tenmax.oyster.OysterContentAdView;
import io.tenmax.oyster.OysterException;

public class MainActivity extends AppCompatActivity {

  private final static String OYSTER_AD_UNIT_ID = "c145f1cd389e49a5";
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
    OysterAdLoader.Builder builder = new OysterAdLoader.Builder(this, OYSTER_AD_UNIT_ID);
    builder.forContentAd(new OysterContentAd.OnContentAdLoadedListener() {
      @Override
      public void onContentAdLoaded(OysterContentAd oysterContentAd) {
        data.add(position, oysterContentAd);
        adapter.notifyDataSetChanged();
      }
    });
    OysterAdLoader adLoader = builder.withAdListener(new OysterAdListener() {
      @Override
      public void onAdFailedToLoad(OysterException e) {
        Toast.makeText(MainActivity.this,
            "Failed to load oyster ad: " + e.getMessage(),
            Toast.LENGTH_SHORT).show();
      }
    }).build();
    adLoader.loadAd();
  }

  class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final int VIEW_TYPE_NORMAL = 0;
    final int VIEW_TYPE_OYSTER_AD = 1;
    private List<Object> data;

    RecyclerAdapter(List<Object> data) {
      this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      if (viewType == VIEW_TYPE_OYSTER_AD) {
        return new OysterAdHolder((OysterContentAdView) LayoutInflater.from(parent.getContext())
            .inflate(R.layout.ad_content, parent, false));
      } else if (viewType == VIEW_TYPE_NORMAL) {
        return new ItemHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.recycler_item, parent, false));
      }
      return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
      if (holder instanceof OysterAdHolder) {
        ((OysterAdHolder) holder).bind(((OysterContentAd) data.get(position)));
      } else if (holder instanceof ItemHolder) {
        ((ItemHolder) holder).bind(((String) data.get(position)));
      }
    }

    @Override
    public int getItemViewType(int position) {
      Object o = data.get(position);
      if (o instanceof String) {
        return VIEW_TYPE_NORMAL;
      } else if (o instanceof OysterContentAd) {
        return VIEW_TYPE_OYSTER_AD;
      }
      throw new IllegalArgumentException("Unknown view type in PublicationAdapter");
    }

    @Override
    public int getItemCount() {
      return data.size();
    }

    class OysterAdHolder extends RecyclerView.ViewHolder {

      private final OysterContentAdView adView;

      public OysterAdHolder(OysterContentAdView adView) {
        super(adView);
        this.adView = adView;
        adView.setHeadlineView(adView.findViewById(R.id.contentad_headline));
        adView.setImageView(adView.findViewById(R.id.contentad_image));
        adView.setBodyView(adView.findViewById(R.id.contentad_body));
        adView.setLogoView(adView.findViewById(R.id.contentad_logo));
        adView.setAdvertiserView(adView.findViewById(R.id.contentad_advertiser));
      }

      public void bind(OysterContentAd oysterContentAd) {
        ((TextView) adView.getHeadlineView()).setText(oysterContentAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(oysterContentAd.getBody());
        ((TextView) adView.getAdvertiserView()).setText(oysterContentAd.getAdvertiser());

        if (oysterContentAd.getImage().getDrawable() != null) {
          ((ImageView) adView.getImageView()).setImageDrawable(oysterContentAd.getImage()
              .getDrawable());
        }

        OysterAd.Image logoImage = oysterContentAd.getLogo();

        if (logoImage == null) {
          adView.getLogoView().setVisibility(View.INVISIBLE);
        } else {
          ((ImageView) adView.getLogoView()).setImageDrawable(logoImage.getDrawable());
          adView.getLogoView().setVisibility(View.VISIBLE);
        }

        adView.setOysterAd(oysterContentAd);
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
