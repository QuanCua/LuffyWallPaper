package com.skyfree.coolluffywallpaperhd.activity;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.skyfree.coolluffywallpaperhd.R;
import com.skyfree.coolluffywallpaperhd.adapter.ViewPagerAdapter;
import com.skyfree.coolluffywallpaperhd.object.Picture;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Asus on 3/22/2018.
 */

public class ViewPicture extends AppCompatActivity {
    ArrayList<Picture> arrayListWallPaper;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    ArrayList<Integer> listIndex = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_picture);

        arrayListWallPaper = new ArrayList<>();

        parserXML();

        viewPager = findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(this, arrayListWallPaper);
        viewPager.setAdapter(viewPagerAdapter);


        extraData();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        final BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_favourite:
                        listFavourites();
                        break;
                    case R.id.action_share:
                        shareImage();
                        break;
                    case R.id.action_download:
                        downloadImage();
                        break;
                    case R.id.action_setAsWallpaper:

                        if (item.getItemId() == R.id.action_setAsWallpaper) {
                            bottomNavigationView.setVisibility(View.INVISIBLE);
                        }
                        changeStatusBarColor("#FF000000");

                        setWallpaper();

                        bottomNavigationView.setVisibility(View.VISIBLE);
                        changeStatusBarColor("#cc500d");
                        break;

                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void changeStatusBarColor(String color) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }

    private void tedPermissonal() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
//                Toast.makeText(SettingActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
//                Toast.makeText(SettingActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }


    public void extraData() {
        Intent i = getIntent();

        if (i != null) {
            String url = i.getStringExtra("url");

            Picture picture = new Picture(url);
            for (Picture picture1 : arrayListWallPaper) {
                if (picture.getUrl().equals(picture1.getUrl())) {
                    int index = arrayListWallPaper.indexOf(picture1);
                    viewPager.setCurrentItem(index);

                }
            }

        }
    }

    public static Bitmap viewBitmap(View view, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private void shareImage() {
        tedPermissonal();
        Bitmap bitmap = viewBitmap(viewPager.getRootView(), viewPager.getRootView().getWidth(), viewPager.getRootView().getHeight());
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/jpeg");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "LuffyWallpaper.jpg");
        try {
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/LuffyWallpaper.jpg"));
        startActivity(Intent.createChooser(intent, "Share image"));
//        file.delete();
    }

    public void setWallpaper() {
        tedPermissonal();
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        try {
            wallpaperManager.setBitmap(viewBitmap(viewPager.getRootView(), viewPager.getRootView().getWidth(), viewPager.getRootView().getHeight()));
            Toast.makeText(ViewPicture.this, getText(R.string.text_set_as_wallpaper), Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void downloadImage() {
        tedPermissonal();
        FileOutputStream outputStream = null;
        File path = getDisc();
        if (!path.exists() && !path.mkdirs()) {
            Toast.makeText(ViewPicture.this, getText(R.string.text_download_fail), Toast.LENGTH_LONG).show();
            return;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String date = simpleDateFormat.format(new Date());
        String name = "Img " + date + ".jpg";
        String file_name = path.getAbsolutePath() + "/" + name;
        File new_file = new File(file_name);

        try {
            outputStream = new FileOutputStream(new_file);
            Bitmap bitmap = viewBitmap(viewPager.getRootView(), viewPager.getRootView().getWidth(), viewPager.getRootView().getHeight());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            Toast.makeText(ViewPicture.this, getText(R.string.text_download), Toast.LENGTH_LONG).show();

            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        refreshGallery(new_file);
    }

    public File getDisc() {
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        return new File(file, "LuffyWallpaper");
    }

    private void refreshGallery(File file) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        sendBroadcast(intent);
    }

    public void listFavourites() {
        Intent intent = new Intent();
        listIndex.add(viewPager.getCurrentItem());
        intent.putExtra("PUT_INDEX_OF_LIST", listIndex);
        setResult(5, intent);
        Toast.makeText(ViewPicture.this, getText(R.string.text_favourites), Toast.LENGTH_LONG).show();
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
                        arrayListWallPaper.add(currentPicture);

                    } else if ("url".equals(urlName)) {
                        currentPicture.url = parser.nextText();
                    }
                    break;
            }
            eventType = parser.next();
        }
    }
}