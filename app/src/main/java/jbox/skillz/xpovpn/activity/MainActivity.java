package jbox.skillz.xpovpn.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jbox.skillz.xpovpn.BuildConfig;
import jbox.skillz.xpovpn.R;
import jbox.skillz.xpovpn.SQLiteHandler;
import jbox.skillz.xpovpn.adapter.HomeListAdapter;
import jbox.skillz.xpovpn.database.SessionManager;
import jbox.skillz.xpovpn.model.Server;
import jbox.skillz.xpovpn.util.PropertiesService;


public class MainActivity extends BaseActivity{

//    DecoView arcView, arcView2;
    public static final String EXTRA_COUNTRY = "country";
    private PopupWindow popupWindow;
    private RelativeLayout homeContextRL;

//    TextView centree;
    private List<Server> countryList;
    private ListView listView;
    private HomeListAdapter serverListAdapter;
    CardView mCardViewShare;
    Intent i;
    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, String.valueOf(R.string.admob_app_id));
        AdView mAdMobAdView = (AdView) findViewById(R.id.admob_adview);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdMobAdView.loadAd(adRequest);

        homeContextRL = (RelativeLayout) findViewById(R.id.homeContextRL);
        countryList = dbHelper.getUniqueCountries();

        if (BaseActivity.connectedServer == null) {
            Button hello = (Button) findViewById(R.id.elapse2);
            hello.setText("No VPN Connected");
            hello.setBackgroundResource(R.drawable.button2);
        }
        else {
            Button hello = (Button) findViewById(R.id.elapse2);
            hello.setText("Connected");
            hello.setBackgroundResource(R.drawable.button3);
        }

        Random ran2 = new Random();

        listView = (ListView) findViewById(R.id.homeCountryList);
        String country = getIntent().getStringExtra(MainActivity.EXTRA_COUNTRY);
        final List<Server> serverList = dbHelper.getUniqueCountries();
        serverListAdapter = new HomeListAdapter(this, serverList);

        listView.setAdapter(serverListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onSelectCountry(countryList.get(position));
            }
        });


        Button button1 = (Button) findViewById(R.id.homeBtnRandomConnection);
        button1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                sendTouchButton("homeBtnRandomConnection");
                Server randomServer = getRandomServer();
                if (randomServer != null) {
                    newConnecting(randomServer, true, true);
                } else {
                    String randomError = String.format(getResources().getString(R.string.error_random_country), PropertiesService.getSelectedCountry());
                    Toast.makeText(MainActivity.this, randomError, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (BaseActivity.connectedServer == null) {
            Button hello = (Button) findViewById(R.id.elapse2);
            hello.setText("No VPN Connected");


            Button button1 = (Button) findViewById(R.id.homeBtnRandomConnection);
            hello.setText("Quick Connect");
            button1.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    sendTouchButton("homeBtnRandomConnection");
                    Server randomServer = getRandomServer();
                    if (randomServer != null) {
                        newConnecting(randomServer, true, true);
                    } else {
                        String randomError = String.format(getResources().getString(R.string.error_random_country), PropertiesService.getSelectedCountry());
                        Toast.makeText(MainActivity.this, randomError, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        else {
            Button hello = (Button) findViewById(R.id.elapse2);
            hello.setText("Connected");
            hello.setBackgroundResource(R.drawable.button3);


            Button button1 = (Button) findViewById(R.id.homeBtnRandomConnection);
            button1.setText("Connected!");
            button1.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    if (connectedServer != null)
                        startActivity(new Intent(MainActivity.this, VPNInfoActivity.class));


                }
            });
        }

        invalidateOptionsMenu();


    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    protected boolean useHomeButton() {
        return true;
    }

    public void homeOnClick(View view) {
        switch (view.getId()) {
            case R.id.homeBtnChooseCountry:
                sendTouchButton("homeBtnChooseCountry");
                chooseCountry();
                break;
            case R.id.homeBtnRandomConnection:
                sendTouchButton("homeBtnRandomConnection");
                Server randomServer = getRandomServer();
                if (randomServer != null) {
                    newConnecting(randomServer, true, true);
                } else {
                    String randomError = String.format(getResources().getString(R.string.error_random_country), PropertiesService.getSelectedCountry());
                    Toast.makeText(this, randomError, Toast.LENGTH_LONG).show();
                }
                break;
        }

    }

    private void chooseCountry() {
        View view = initPopUp(R.layout.choose_country, 0.6f, 0.8f, 0.8f, 0.7f);

        final List<String> countryListName = new ArrayList<String>();
        for (Server server : countryList) {
            String localeCountryName = localeCountries.get(server.getCountryShort()) != null ?
                    localeCountries.get(server.getCountryShort()) : server.getCountryLong();
            countryListName.add(localeCountryName);
        }

        ListView lvCountry = (ListView) view.findViewById(R.id.homeCountryList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, countryListName);

        lvCountry.setAdapter(adapter);
        lvCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                popupWindow.dismiss();
                onSelectCountry(countryList.get(position));
            }
        });

        popupWindow.showAtLocation(homeContextRL, Gravity.CENTER,0, 0);
    }

    private View initPopUp(int resourse,
                            float landPercentW,
                            float landPercentH,
                            float portraitPercentW,
                            float portraitPercentH) {

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(resourse, null);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            popupWindow = new PopupWindow(
                    view,
                    (int)(widthWindow * landPercentW),
                    (int)(heightWindow * landPercentH)
            );
        } else {
            popupWindow = new PopupWindow(
                    view,
                    (int)(widthWindow * portraitPercentW),
                    (int)(heightWindow * portraitPercentH)
            );
        }


        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        return view;
    }

    private void onSelectCountry(Server server) {
        Intent intent = new Intent(getApplicationContext(), VPNListActivity.class);
        intent.putExtra(EXTRA_COUNTRY, server.getCountryShort());
        startActivity(intent);
    }


    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase += c;
        }
        return phrase;
    }

    private void aboutMyApp() {

        MaterialDialog.Builder bulder = new MaterialDialog.Builder(this)
                .title(R.string.app_name)
                .customView(R.layout.about, true)
                .backgroundColor(getResources().getColor(R.color.colorPrimaryDark))
                .titleColorRes(android.R.color.white)
                .positiveText("MORE APPS")
                .positiveColor(getResources().getColor(android.R.color.white))
                .icon(getResources().getDrawable(R.mipmap.ic_launcher))
                .limitIconToDefaultSize()
                .onPositive((dialog, which) -> {
                    Uri uri = Uri.parse("market://search?q=pub:" + "PA Production");
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    try {
                        startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/search?q=pub:" + "PA Production")));
                    }
                });

        MaterialDialog materialDialog = bulder.build();

        TextView versionCode = (TextView) materialDialog.findViewById(R.id.version_code);
        TextView versionName = (TextView) materialDialog.findViewById(R.id.version_name);
        versionCode.setText(String.valueOf("Version Code : " + BuildConfig.VERSION_CODE));
        versionName.setText(String.valueOf("Version Name : " + BuildConfig.VERSION_NAME));

        materialDialog.show();
    }
}



