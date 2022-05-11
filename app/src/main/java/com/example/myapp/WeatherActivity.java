package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

//현재 위치 날씨 받아오는 부분

public class WeatherActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    //변수명 수정하기

    private SwipeRefreshLayout mysrl;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    Location location;
    public double log, lat;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather);
        mLayout = findViewById(R.id.srl);
        Log.d("location3", lat + "," + log);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(1800000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        if (!checkLocationServicesStatus()) {//위치 안켜져있음.
            showDialogForLocationServiceSetting();

        } else {//위치 켜져있고 위치 권한 허용 확인
            checkRunTimePermission();
        }

        mysrl = findViewById(R.id.srl);
        mysrl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
                mysrl.setRefreshing(false);
            }
        });
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            TextView textView = findViewById(R.id.address);
            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);
                Log.d("loccc", location.getLatitude() + "," + location.getLongitude());

                double lati = location.getLatitude();
                double lon = location.getLongitude();
                //Geocoder 객체생성
                Geocoder geocoder = new Geocoder(WeatherActivity.this);

                List<Address> address = null; //주소정보 리스트 변수
                String locality, admin; //주소받을 변수
                String str_Addr;
                //주소 가져오기
                try {
                    address = geocoder.getFromLocation(lati, lon, 1);
                    Log.d("kkk", String.valueOf(address));

                    locality = address.get(0).getLocality();
                    admin = address.get(0).getAdminArea();

                    textView.setText(admin + " " +locality);
                    getData();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (address != null) {
                    if (address.size() != 0) {
                        str_Addr = address.get(0).getAddressLine(0);
                    }
                }

            }


            if (locationResult == null) {
                return;
            } else {
                for (Location location : locationResult.getLocations()) {
                    Log.d("info", location.toString());

                    double lati = location.getLatitude();
                    double longi = location.getLongitude();
                    lat = lati;
                    log = longi;
                    Log.d("location", lati + "," + longi);
                    Log.d("location2", lat + "," + log);
                }
            }

        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // 위치 서비스 활성화를 위한 메소드
    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WeatherActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("이 기능을 사용하기 위해서\n위치 서비스를 활성화 시켜주세요.");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        super.onRequestPermissionsResult(permsRequestCode, permissions, grandResults);
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {

                // 퍼미션을 허용했다면 위치 업데이트를 시작합니다.
                startLocationUpdates();
                //getData();
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();

                } else {


                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();
                }
            }

        }
    }


    // 위치 권한 허용 확인을 위한 메소드
    void checkRunTimePermission() {

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(WeatherActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(WeatherActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // 3.  위치 값을 가져올 수 있음
            startLocationUpdates();

        } else {  //3. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(WeatherActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(WeatherActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(WeatherActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);

            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(WeatherActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }

    public void getData() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            locationChange locChange = new locationChange();
                            LatXLngY tmp = locChange.convertGRID_GPS(0, location.getLatitude(), location.getLongitude());
                            Log.d("debug_test", "x = " + tmp.x);
                            Log.d("debug_test", "y = " + tmp.y);
                            Log.e(">>", "x = " + tmp.x + ", y = " + tmp.y);

                            String pageNo = "1";
                            String numOfRows = "50";

                            //오늘 날짜
                            SimpleDateFormat real_time = new SimpleDateFormat("yyyyMMdd");//현재날짜(ex.20220313)
                            Date time = new Date();
                            String base_date = real_time.format(time);

                            //어제 날짜
                            Calendar calendar = new GregorianCalendar();
                            calendar.add(Calendar.DATE,-1);
                            Log.d("yester", String.valueOf(calendar));

                            SimpleDateFormat f2 = new SimpleDateFormat("Hmm");
                            String base_time = f2.format(time);
                            Log.d("time2",base_time);
                            int t = Integer.parseInt(base_time);
                            Log.d("t", String.valueOf(t));

                            //날씨 API는 매시간 40분에 업데이트 됨.
                            if(t>=0040 && t<140){
                                //base_date = real_time.format(calendar.getTime());
                                base_time = "0030";
                            }
                            else if(t>=140 && t<240){
                                base_time = "0130";
                            }
                            else if(t>=240 && t<340){
                                base_time = "0230";
                            }
                            else if(t>=340 && t<440){
                                base_time = "0330";
                            }
                            else if(t>=440 && t<540){
                                base_time = "0430";
                            }
                            else if(t>=540 && t<640){
                                base_time = "0530";
                            }
                            else if(t>=640 && t<740){
                                base_time = "0630";
                            }
                            else if(t>=740 && t<840){
                                base_time = "0730";
                            }
                            else if(t>=840 && t<940){
                                base_time = "0830";
                            }
                            else if(t>=940 && t<1040){
                                base_time = "0930";
                            }
                            else if(t>=1040 && t<1140){
                                base_time = "1030";
                            }
                            else if(t>=1140 && t<1240){
                                base_time = "1130";
                            }
                            else if(t>=1240 && t<1340){
                                base_time = "1230";
                            }
                            else if(t>=1340 && t<1440){
                                base_time = "1330";
                            }
                            else if(t>=1440 && t<1540){
                                base_time = "1430";
                            }
                            else if(t>=1540 && t<1640){
                                base_time = "1530";
                            }
                            else if(t>=1640 && t<1740){
                                base_time = "1630";
                            }
                            else if(t>=1740 && t<1840){
                                base_time = "1730";
                            }
                            else if(t>=1840 && t<1940){
                                base_time = "1830";
                            }
                            else if(t>=1940 && t<2040){
                                base_time = "1930";
                            }
                            else if(t>=2040 && t<2140){
                                base_time = "2030";
                            }
                            else if(t>=2140 && t<2240){
                                base_time = "2130";
                            }
                            else if(t>=2240 && t<2340){
                                base_time = "2230";
                            }
                            else if(t>=2340 && t<=2359){
                                base_time = "2330";
                            }
                            else{
                                Calendar day = Calendar.getInstance();
                                //24시~24시40분 -> 전날 11시 30분 데이터.
                                day.add(Calendar.DATE , -1);
                                String beforeDate = new java.text.SimpleDateFormat("yyyyMMdd").format(day.getTime());
                                Log.d("beforeDate",beforeDate);
                                base_date = beforeDate;
                                base_time = "2330";
                            }

                            double d = Double.parseDouble(tmp.x);
                            DecimalFormat df = new DecimalFormat("0");
                            String result = df.format(d);
                            Log.d("result",result);
                            String nx = result;

                            double d2 = Double.parseDouble(tmp.y);
                            //DecimalFormat decimalFormat = new DecimalFormat("0");
                            String result2 = df.format(d2);
                            Log.d("result2",result2);
                            String ny = result2;

                            String qurl ="http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst?serviceKey=hjnA51g4D5Jh5pMY%2BL17qEO87IpWw2ZtkiEspqyL9J57fOGtZztzdvGTWgS0dx19sDxNR4G4URiEfN1kHMuSPA%3D%3D"+"&numOfRows="+numOfRows+"pageNo="+pageNo+"&dataType=JSON"+
                                    "&base_date="+base_date+
                                    "&base_time="+base_time+
                                    "&nx="+nx+
                                    "&ny="+ny;

                            String url = "http://apis.data.go.kr/B552584/ArpltnStatsSvc/getCtprvnMesureLIst?itemCode=PM10&dataGubun=HOUR&pageNo=1&numOfRows=1&returnType=json&serviceKey=hjnA51g4D5Jh5pMY%2BL17qEO87IpWw2ZtkiEspqyL9J57fOGtZztzdvGTWgS0dx19sDxNR4G4URiEfN1kHMuSPA%3D%3D";

                            TextView ttx;
                            ttx = (TextView)findViewById(R.id.ttx);
                            ttx.setText(base_date);

                            TextView tex = (TextView) findViewById(R.id.address);
                            String te1 = tex.getText().toString();
                            Log.d("texttt",te1);

                            String[] array = te1.split(" ");
                            String local = array[0];
                            Log.d("localll2",local);

                            String finalUrl = url;
                            new Thread(){

                                public void run() {

                                    try {
                                        sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        URL url = new URL(qurl);
                                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                        connection.setRequestMethod("GET");
                                        connection.setDoInput(true);
                                        InputStream is = connection.getInputStream();
                                        StringBuilder sb = new StringBuilder();
                                        BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
                                        String result;
                                        while((result = br.readLine())!=null){
                                            sb.append(result+"\n");
                                        }
                                        result = sb.toString();
                                        Log.d("tag2",result);
                                        JsonParse(result);

                                        URL url1 = new URL(finalUrl);
                                        HttpURLConnection connection1 = (HttpURLConnection) url1.openConnection();
                                        connection1.setRequestMethod("GET");
                                        connection1.setDoInput(true);
                                        InputStream is2 = connection1.getInputStream();
                                        StringBuilder sb2 = new StringBuilder();
                                        BufferedReader br2 = new BufferedReader(new InputStreamReader(is2,"UTF-8"));
                                        String result2;
                                        while ((result2 = br2.readLine())!=null){
                                            sb2.append(result2+"\n");
                                        }
                                        result2 = sb2.toString();
                                        Log.d("tag3",result2);
                                        JsonParse4(result2);

                                        runOnUiThread(new Runnable() {
                                            @SuppressLint("ResourceAsColor")
                                            @Override
                                            public void run() {
                                                TextView air_tv = findViewById(R.id.air);
                                                String air = air_tv.getText().toString();

                                                TextView rain_tv = findViewById(R.id.tx2);
                                                String rain = rain_tv.getText().toString();

                                                ImageView imageView = findViewById(R.id.image);

                                                int a = Integer.parseInt(air);
                                                if(a<30 && rain.equals("없음")){
                                                    imageView.setImageResource(R.drawable.ic_emotion_good);
                                                }else if(a>=80 || rain.equals("비") || rain.equals("눈") || rain.equals("비/눈")){
                                                    imageView.setImageResource(R.drawable.ic_emotion_bad);
                                                }else{
                                                    imageView.setImageResource(R.drawable.ic_emotion_normal);
                                                }

                                            }
                                        });
                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }.start();


                        }
                    }
                });
    }

    //날씨api Json파싱
    public void JsonParse(String str) {

        try {
            JSONObject obj = new JSONObject(str);
            JSONObject response = obj.getJSONObject("response");
            JSONObject body = response.getJSONObject("body");
            JSONObject items = body.getJSONObject("items");

            JSONArray arr = items.getJSONArray("item");
            for(int i = 0;i<arr.length();i++){
                JSONObject job = arr.getJSONObject(i);
                String category = job.getString("category");
                String obsrValue = job.optString("obsrValue");
                Log.d("category",category);
                Log.d("obsrValue",obsrValue);

                if(category.equals("PTY")){
                    if(obsrValue.equals("0")){
                        obsrValue="없음";
                    }
                    else if(obsrValue.equals("1")){
                        obsrValue="비";
                    }
                    else if(obsrValue.equals("2")){
                        obsrValue="비/눈";
                    }
                    else if(obsrValue.equals("3")){
                        obsrValue="눈";
                    }
                    else if(obsrValue.equals("5")){
                        obsrValue="빗방울";
                    }
                    else if(obsrValue.equals("6")){
                        obsrValue="빗방울눈날림";
                    }
                    else if(obsrValue.equals("7")){
                        obsrValue="눈날림";
                    }

                    TextView t2 = findViewById(R.id.tx2);
                    String finalObsrValue = obsrValue;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            t2.setText(finalObsrValue);
                        }
                    });
                }

                if(category.equals("REH")){
                    TextView t3 = findViewById(R.id.tx3);
                    String finalObsrValue7 = obsrValue;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            t3.setText(finalObsrValue7 + "%");
                        }
                    });
                }
                else if(category.equals("RN1")){
                    TextView t4 = findViewById(R.id.tx4);
                    String finalObsrValue6 = obsrValue;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            t4.setText(finalObsrValue6 + "mm");
                        }
                    });
                }
                else if(category.equals("T1H")){
                    TextView t5 = findViewById(R.id.tx5);

                    String finalObsrValue5 = obsrValue;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            t5.setText(finalObsrValue5 + "ºC");
                        }
                    });
                }

                else if(category.equals("WSD")){
                    //category = "풍속";
                    TextView t9 = findViewById(R.id.tx9);
                    String finalObsrValue1 = obsrValue;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            t9.setText(finalObsrValue1 + "m/s");
                        }
                    });
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void JsonParse4(String str) {
        JSONObject obj;
        TextView tex = (TextView) findViewById(R.id.address);
        String te1 = tex.getText().toString();
        Log.d("text",te1);
        String[] array2 = te1.split(" ");
        String local2 = array2[0];
        Log.d("local3",local2);

        try {
            obj = new JSONObject(str);
            JSONObject response = obj.getJSONObject("response");
            JSONObject body = response.getJSONObject("body");
            JSONArray arr = body.getJSONArray("items");
            for(int i = 0;i<arr.length();i++){

                JSONObject job = arr.getJSONObject(i);
                String daegu = job.getString("daegu");
                String chungnam = job.getString("chungnam");
                String incheon = job.getString("incheon");
                String daejeon = job.getString("daejeon");
                String gyeongbuk = job.getString("gyeongbuk");
                String sejong = job.getString("sejong");
                String gwangju = job.getString("gwangju");
                String jeonbuk = job.getString("jeonbuk");
                String gangwon = job.getString("gangwon");
                String ulsan = job.getString("ulsan");
                String jeonnam = job.getString("jeonnam");
                String seoul = job.getString("seoul");
                String busan = job.getString("busan");
                String jeju = job.getString("jeju");
                String chungbuk = job.getString("chungbuk");
                String gyeongnam = job.getString("gyeongnam");
                String gyeonggi = job.getString("gyeonggi");


                Log.d("daegu",daegu);
                Log.d("chungnam",chungnam);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView textView = findViewById(R.id.air);
                        if(local2.equals("충청남도")){
                            textView.setText(chungnam);
                        }else if(local2.equals("대구광역시")){
                            textView.setText(daegu);
                        }else if(local2.equals("인천광역시")){
                            textView.setText(incheon);
                        }else if(local2.equals("대전광역시")){
                            textView.setText(daejeon);
                        }else if(local2.equals("세종특별자치시")){
                            textView.setText(sejong);
                        }else if(local2.equals("광주광역시")){
                            textView.setText(gwangju);
                        }else if(local2.equals("전라북도")){
                            textView.setText(jeonbuk);
                        }else if(local2.equals("경상북도")){
                            textView.setText(gyeongbuk);
                        }else if(local2.equals("강원도")){
                            textView.setText(gangwon);
                        }else if(local2.equals("울산광역시")){
                            textView.setText(ulsan);
                        }else if(local2.equals("전라남도")){
                            textView.setText(jeonnam);
                        }else if(local2.equals("서울특별시")){
                            textView.setText(seoul);
                        }else if(local2.equals("부산광역시")){
                            textView.setText(busan);
                        }else if(local2.equals("제주특별자치시")){
                            textView.setText(jeju);
                        }else if(local2.equals("충청북도")){
                            textView.setText(chungbuk);
                        }else if(local2.equals("경상남도")){
                            textView.setText(gyeongnam);
                        }else if(local2.equals("경기도")){
                            textView.setText(gyeonggi);
                        }
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}