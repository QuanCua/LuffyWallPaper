package com.skyfree.coolluffywallpaperhd.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.skyfree.coolluffywallpaperhd.R;
import com.skyfree.coolluffywallpaperhd.adapter.GridViewAdapter;
import com.skyfree.coolluffywallpaperhd.object.Picture;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    GridView gridView;
    GridViewAdapter gridViewAdapter;
    ArrayList<Picture> arrayList;
    ArrayList<Picture> arrayListFavourites = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = findViewById(R.id.gridViewPicture);
        arrayList = new ArrayList<>();
//
//        SharedPreferences mPrefe = getBaseContext().getSharedPreferences("MPREF", MODE_PRIVATE);
//        Boolean checkList = mPrefe.getBoolean("SAVE_ARRAY_FAVOURITES", false );

        if (isNetworkConnected() == false) {
            checkWifiConnect();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_wallpapers);

        if (navigationView.isEnabled() == true) {
            parserXML();
            gridViewAdapter = new GridViewAdapter(arrayList, getApplicationContext());
            gridView.setAdapter(gridViewAdapter);
            gridViewAdapter.notifyDataSetChanged();
        }



        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, ViewPicture.class);
                Picture url = arrayList.get(i);

                intent.putExtra("url", url.getUrl());
                startActivityForResult(intent, 5);

            }
        });
    }

    private void checkWifiConnect() {
        final AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
        alert.setMessage(getString(R.string.text_dialog));
        alert.setCancelable(false);
        alert.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alert.show();
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null; // return true =(connected),false=(not connected)
    }

    private void parserXML() {
        XmlPullParserFactory parserFactory;
        try {
            parserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserFactory.newPullParser();
            InputStream is = getAssets().open("data_pictures.xml");
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);

            processParsing(parser);

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processParsing(XmlPullParser parser) throws XmlPullParserException, IOException {
        int eventType = parser.getEventType();
        Picture currentPicture = null;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String urlName = null;
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    urlName = parser.getName();
                    if ("arrayItem".equals(urlName)) {
                        currentPicture = new Picture();
                        arrayList.add(currentPicture);

                    } else if ("url".equals(urlName)) {
                        currentPicture.url = parser.nextText();
                    }
                    break;
            }
            eventType = parser.next();
        }
    }


    public ArrayList<Picture> getList(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<Picture>>() {
        }.getType();

        if(gson.fromJson(json,type) == null){
            return new ArrayList<Picture>();
        }else{
            return gson.fromJson(json, type);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_rateApp) {
            final String appPackageName = getPackageName();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));

//            try {
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
//            } catch (android.content.ActivityNotFoundException anfe) {
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
//            }

        } else if (id == R.id.action_discover) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=Sky+Free+App")));
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_wallpapers) {
            parserXML();
            gridViewAdapter = new GridViewAdapter(arrayList, getApplicationContext());
            gridView.setAdapter(gridViewAdapter);
            gridViewAdapter.notifyDataSetChanged();

        } else if (id == R.id.nav_favourites) {
            gridViewAdapter = new GridViewAdapter(getList("LIST_OF_FAVOURITES"), getApplicationContext());
            gridView.setAdapter(gridViewAdapter);
            gridViewAdapter.notifyDataSetChanged();

        } else if (id == R.id.nav_rateApp) {
            final String appPackageName = getPackageName();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));

        } else if (id == R.id.nav_discover) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=Sky+Free+App")));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 5) {
            if (data != null) {
                ArrayList<Integer> getListIndex = new ArrayList<>();
                getListIndex = data.getIntegerArrayListExtra("PUT_INDEX_OF_LIST");
                for(int i: getListIndex){
                    Picture url = arrayList.get(i);
                    arrayListFavourites.add(new Picture(url.getUrl()));
                }


                saveList(arrayListFavourites, "LIST_OF_FAVOURITES");
            }
        } else {
//            arrayListFavourites = new ArrayList<>();
//            saveList(arrayListFavourites, "LIST_OF_FAVOURITES");



        }
    }

    private void saveList(ArrayList<Picture> list, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }
}
