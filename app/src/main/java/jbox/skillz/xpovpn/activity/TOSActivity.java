package jbox.skillz.xpovpn.activity;

import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import jbox.skillz.xpovpn.Constant;
import jbox.skillz.xpovpn.R;

public class TOSActivity extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacypolicy);

        MobileAds.initialize(this, String.valueOf(R.string.admob_app_id));
        AdView mAdMobAdView = (AdView) findViewById(R.id.admob_adview);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdMobAdView.loadAd(adRequest);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp);
        upArrow.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(upArrow);
//        procheck();


        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(Constant.PrivacyPolicyUrl);
        webView.setWebViewClient(new MyWebViewClient());
        webView.setHorizontalScrollBarEnabled(false);
        webView.requestFocus();



    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


//    public void procheck()
//    {
//        boolean strPref = false;
//        SharedPreferences shf = this.getSharedPreferences("config", MODE_PRIVATE);
//        strPref = shf.getBoolean(Constant.UpgradePro, false);
//
//        if(strPref)
//        {
//            AdView mAdMobAdView = (AdView) findViewById(R.id.admob_adview);
//            mAdMobAdView.setVisibility(View.GONE);
//
//
//        }
//        else {
//            AdView mAdMobAdView = (AdView) findViewById(R.id.admob_adview);
//            AdRequest adRequest = new AdRequest.Builder()
//                    .build();
//            mAdMobAdView.loadAd(adRequest);
//
//            final InterstitialAd mInterstitial = new InterstitialAd(this);
//            mInterstitial.setAdUnitId(getString(R.string.interstitial_ad_unit));
//            mInterstitial.loadAd(new AdRequest.Builder().build());
//            mInterstitial.setAdListener(new AdListener() {
//                @Override
//                public void onAdLoaded() {
//                    // TODO Auto-generated method stub
//                    super.onAdLoaded();
//                    if (mInterstitial.isLoaded()) {
//                        mInterstitial.show();
//                    }
//                }
//            });
//
//
//
//        }
//
//    }

}
