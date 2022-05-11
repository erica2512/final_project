package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.skt.Tmap.TMapCircle;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

//병원 정보를 T맵을 이용하여 나타냄.

public class MapActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();

    String API_Key = "l7xxeade583821344dbf9d26df834134c8f9";

    // T Map View
    TMapView tMapView = null;

    // T Map GPS
    TMapGpsManager tMapGPS = null;

    // Initial Location
    double initialLatitude = 36.7984828;
    double initialLongitude = 127.0759283;

    TMapPoint tMapPointStart; //경로 시작 지점
    TMapPoint tMapPointEnd; //도착 지점
    TMapPolyLine polyLine;

    Button btn_tell;
    Button button2;//경로
    Button bt_bookmark;//즐겨찾기 등록

    double latitude;
    double longitude;

    //json데이터를 배열로 관리
    private ArrayList<String> store = new ArrayList<String>();
    private ArrayList<String> address = new ArrayList<String>();
    private ArrayList<String> doro_address = new ArrayList<String>();
    private ArrayList<String> pnumber = new ArrayList<String>();
    private ArrayList<Double> xx = new ArrayList<Double>();
    private ArrayList<Double> yy = new ArrayList<Double>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        btn_tell = findViewById(R.id.bt_call);
        button2 = findViewById(R.id.btn2);
        bt_bookmark = findViewById(R.id.bt_bookmark);

        //버튼 숨김.
        btn_tell.setVisibility(View.INVISIBLE);
        button2.setVisibility(View.INVISIBLE);
        bt_bookmark.setVisibility(View.INVISIBLE);

        // T Map View
        tMapView = new TMapView(this);
        initTMapView();

        // T Map GPS
        initTMapGPS();

        addMarketMarker();

        // T Map View Using Linear Layout
        LinearLayout linearLayoutTMap = (LinearLayout)findViewById(R.id.linearLayoutTmap);
        linearLayoutTMap.addView(tMapView);


    }

    public void initTMapView(){
        // API Key
        tMapView.setSKTMapApiKey(API_Key);

        // Initial Setting
        tMapView.setZoomLevel(15);
        tMapView.setIconVisibility(true);
        tMapView.setMapType(TMapView.MAPTYPE_STANDARD);
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN);

        // Initial Location Setting
        tMapView.setLocationPoint(initialLongitude, initialLatitude);
        tMapView.setCenterPoint(initialLongitude, initialLatitude);

    }

    public void initTMapGPS(){
        // Request For GPS permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        // T Map GPS
        tMapGPS = new TMapGpsManager(this);

        // Initial Setting
        tMapGPS.setMinTime(300);
        tMapGPS.setMinDistance(1);
        tMapGPS.setProvider(tMapGPS.NETWORK_PROVIDER);
        //tMapGPS.setProvider(tMapGPS.GPS_PROVIDER);

        // Using GPS
        tMapGPS.OpenGps();

    }

    @Override
    public void onLocationChange(Location location) {

        tMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
        tMapView.setCenterPoint(location.getLongitude(), location.getLatitude());
        latitude = location.getLatitude();
        longitude= location.getLongitude();

        tMapView.moveToZoomPosition(latitude,longitude);
        tMapPointStart = new TMapPoint(latitude,longitude);
        Log.d("la,lo",latitude+","+longitude);

        //tMapView.setTrackingMode(true);

        //버튼 클릭 시 시점을 현재 위치로 변경
        ImageButton btn_location = findViewById(R.id.btn_location);
        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
                tMapView.setCenterPoint(location.getLongitude(), location.getLatitude());
                tMapView.moveToZoomPosition(latitude,longitude);
                tMapView.setZoomLevel(15);
            }
        });


        //현재위치 주변에 원 그리기.
        TMapPoint mapPoint = new TMapPoint(latitude,longitude);
        TMapCircle tMapCircle = new TMapCircle();
        tMapCircle.setCenterPoint(mapPoint);
        tMapCircle.setRadius(300);//반지름
        tMapCircle.setCircleWidth(2);
        tMapCircle.setLineColor(Color.BLUE);
        tMapCircle.setAreaColor(Color.GRAY);
        tMapCircle.setAreaAlpha(100);
        tMapView.addTMapCircle("circle1", tMapCircle);
        tMapCircle.setRadiusVisible(true);

        //길찾기 버튼 클릭 시 목적지까지 polyLine 그려줌.
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                polyLine = new TMapPolyLine();
                PathAsync pathAsync = new PathAsync();
                pathAsync.execute(polyLine);

            }
        });
    }

    //polyLine그리는 부분.
    class PathAsync extends AsyncTask<TMapPolyLine, Void, TMapPolyLine> {
        //@Override
        protected TMapPolyLine doInBackground(TMapPolyLine... tMapPolyLines) {
            TMapPolyLine tMapPolyLine = tMapPolyLines[0];
            try {
                //현위치에서 목적지까지의 polyLine 그려줌.
                tMapPolyLine = new TMapData().findPathDataWithType(TMapData.TMapPathType.CAR_PATH, tMapPointStart, tMapPointEnd);
                tMapPolyLine.setLineColor(Color.BLUE);
                tMapPolyLine.setLineWidth(3);

                //거리 계산.
                double Distance = tMapPolyLine.getDistance();
                Log.d("Distance", String.valueOf(Distance));
                TextView t_distance = findViewById(R.id.distance);
                String s_distance = String.format("%.0f",Distance);
                t_distance.setText(s_distance + "m");

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("error", e.getMessage());
            }
            return tMapPolyLine;
        }

        @Override
        protected void onPostExecute(TMapPolyLine tMapPolyLine) {
            super.onPostExecute(tMapPolyLine);
            tMapView.addTMapPolyLine("Line1", tMapPolyLine);
        }
    }


    public void addMarketMarker(){

        AssetManager assetManager= getAssets();

        try {
            address.clear();
            doro_address.clear();
            store.clear();
            pnumber.clear();
            xx.clear();
            yy.clear();

            //json파싱
            InputStream is= assetManager.open("jsons/test.json");
            InputStreamReader isr= new InputStreamReader(is);
            BufferedReader reader= new BufferedReader(isr);

            StringBuffer buffer= new StringBuffer();
            String line= reader.readLine();
            while (line!=null){
                buffer.append(line+"\n");
                line=reader.readLine();
            }

            String jsonData= buffer.toString();

            //json 데이터가 []로 시작하는 배열일때..
            JSONArray jsonArray= new JSONArray(jsonData);

            String s="";

            for(int i=0; i<jsonArray.length();i++){

                JSONObject jo=jsonArray.getJSONObject(i);

                String saddress = jo.getString("소재지전체주소");
                String sdoro_address = jo.getString("도로명전체주소");
                String name= jo.getString("사업장명");
                String snumber= jo.getString("소재지전화");
                double x= jo.getDouble("Latitude");
                double y= jo.getDouble("Longitude");

                s += name+" : "+"\n";

                //파싱한 데이터 배열에 추가.
                address.add(saddress);
                doro_address.add(sdoro_address);
                store.add(name);
                pnumber.add(snumber);
                xx.add(x);
                yy.add(y);
            }

            Log.d("address",String.valueOf(address));
            Log.d("doro",String.valueOf(doro_address));
            Log.d("name2", String.valueOf(store));
            Log.d("number",String.valueOf(pnumber));
            Log.d("x", String.valueOf(xx));
            Log.d("y",String.valueOf(yy));

            //티맵에서는 비트맵 사용해야 함.
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hospital_small);
            Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.placeholder_samll);

            //마커 찍기.
            for(int i = 0; i< store.size();i++){
                String storeName = store.get(i);
                String address2 = address.get(i);
                String doro = doro_address.get(i);
                String num = pnumber.get(i);
                double lat = xx.get(i);
                double lon = yy.get(i);

                TMapPoint tMapPoint = new TMapPoint(lat,lon);


                // TMapMarkerItem
                //마커 세팅
                TMapMarkerItem tMapMarkerItem = new TMapMarkerItem();
                tMapMarkerItem.setIcon(bitmap);
                tMapMarkerItem.setPosition(0.5f, 1.0f);
                tMapMarkerItem.setTMapPoint(tMapPoint);
                tMapMarkerItem.setName(storeName);

                // 풍선뷰 세팅
                tMapMarkerItem.setCanShowCallout(true);     // Balloon View 사용
                tMapMarkerItem.setCalloutTitle(storeName);  // Main Message
                tMapMarkerItem.setCalloutSubTitle(doro+" \n" +num); // Sub Message
                tMapMarkerItem.setAutoCalloutVisible(false); // 초기 접속 시 Balloon View X
                tMapMarkerItem.setCalloutRightButtonImage(bitmap2);
//                tMapView.removeAllMarkerItem();


                //풍선뷰의 오른쪽 이미지 클릭 시..
                tMapView.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
                    @Override
                    public void onCalloutRightButton(TMapMarkerItem tMapMarkerItem) {

                        btn_tell.setVisibility(View.VISIBLE);
                        button2.setVisibility(View.VISIBLE);
                        bt_bookmark.setVisibility(View.VISIBLE);

                        String id = tMapMarkerItem.getID();
                        String name = tMapMarkerItem.getCalloutTitle();
                        String pnum = tMapMarkerItem.getCalloutSubTitle();
                        TMapPoint position = tMapMarkerItem.getTMapPoint();

                        TextView txt_name = findViewById(R.id.name);
                        txt_name.setText(name);

                        String location = String.valueOf(position);
                        Log.d("location2", location);

                        //위도, 경도 split
                        String latitude = null;
                        String longitude = null;
                        String cut2[] = location.split(" ");
                        for(int i=0; i<cut2.length;i++){
                            latitude = cut2[1];
                            longitude = cut2[3];
                        }
                        Log.d("aaa",latitude);
                        Log.d("bbb",longitude);

                        double d_latitude = Double.parseDouble(latitude);
                        double d_longitude = Double.parseDouble(longitude);

                        //목적지
                        tMapPointEnd = new TMapPoint(d_latitude,d_longitude);

                        //subtitle 파싱(주소, 전화번호)
                        String splitdata = null;
                        String splitaddr = null;
                        String cut[] = pnum.split("\n");
                        for(int i=0; i<cut.length;i++){
                            splitaddr = cut[0];
                            splitdata = cut[1];
                        }
                        TextView textView = findViewById(R.id.addr);
                        textView.setText(splitaddr);

                        TextView tnum = findViewById(R.id.number);
                        tnum.setText("번호: "+splitdata);

                        //전화버튼 클릭 시 다이얼 화면으로 이동.
                        String finalSplitdata = splitdata;
                        btn_tell.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Uri uri = Uri.parse("tel:"+ finalSplitdata);
                                Intent it = new Intent(Intent.ACTION_DIAL, uri);
                                startActivity(it);
                            }
                        });

                        String finalSplitaddr = splitaddr;
                        Log.d("splitaddr",finalSplitaddr);
                        bt_bookmark.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                addanimal(name.toString(),finalSplitdata.toString(), finalSplitaddr);
                            }
                        });

                    }
                });

                // add Marker on T Map View
                // id로 마커를 식별
                tMapView.addMarkerItem("marketLocation" + i, tMapMarkerItem);

            }

        } catch (IOException e) {e.printStackTrace();
        } catch (JSONException e) {e.printStackTrace(); }

    }

    //db에 즐겨찾기 데이터 등록하는 부분
    public void addanimal(String name, String number, String address){
        animal animal = new animal(name, number, address);
        //현재 로그인중인 사용자의 IdToken을 가져옴.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            String IdToken = user.getUid();
            Log.d("IdToken",IdToken);

            //BookMark 생성 후 IdToken에 따라 즐겨찾기 정보 저장.
            databaseReference.child("Commal").child("BookMark").child(IdToken).child(name).setValue(animal);


        } else {
            // No user is signed in
        }

    }

}
