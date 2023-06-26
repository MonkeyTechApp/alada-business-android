package com.poupock.feussom.aladabusiness.job;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.android.volley.VolleyError;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.callback.VolleyRequestCallback;
import com.poupock.feussom.aladabusiness.core.CoreApp;
import com.poupock.feussom.aladabusiness.core.dashboard.DashboardActivity;
import com.poupock.feussom.aladabusiness.database.AppDataBase;
import com.poupock.feussom.aladabusiness.util.Course;
import com.poupock.feussom.aladabusiness.util.Methods;
import com.poupock.feussom.aladabusiness.util.Order;
import com.poupock.feussom.aladabusiness.util.OrderItem;
import com.poupock.feussom.aladabusiness.util.User;
import com.poupock.feussom.aladabusiness.web.Fetch;
import com.poupock.feussom.aladabusiness.web.PostTask;
import com.poupock.feussom.aladabusiness.web.ServerUrl;
import com.poupock.feussom.aladabusiness.web.response.DataResponse;
import com.poupock.feussom.aladabusiness.web.response.DatumResponse;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

public class OrderService extends Service {

    public static String tag = OrderService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String lastSyncTime = intent.getStringExtra(User.LAST_SYNC_TIME);
        Intent notificationIntent = new Intent(this, DashboardActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CoreApp.CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.service_runing))
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();

        startForeground(1, notification);
        new CountDownTimer(10000, 100) {
            @Override
            public void onTick(long l) {}

            @Override
            public void onFinish() {
                fetch(this);

            }
        }.start();

        new CountDownTimer(6000, 100) {
            @Override
            public void onTick(long l) {}

            @Override
            public void onFinish() {
                post(this);

            }
        }.start();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    void fetch(CountDownTimer timer){
        String url = null;
        try {
            url = ServerUrl.ORDER + "?time="+ URLEncoder.encode(User.getLastSyncTime(getApplicationContext()), "UTF-8")+"&business_id="
                    + AppDataBase.getInstance(this).businessDao().getAllBusinesses().get(0).getId();
            Log.i(tag, url);
            new Fetch(getApplicationContext(), url,
                    new VolleyRequestCallback() {
                        @Override
                        public void onStart() {
                            Log.i(tag, "Fetch started");
                        }

                        @Override
                        public void onSuccess(String response) {
                            Log.i(tag, "RESPONSE : "+response);
                            Gson gson = new Gson();
                            DataResponse dataResponse = gson.fromJson(response, DataResponse.class);
                            if (dataResponse.success){
                                List<Order> orders = Order.buildListFromObjects(dataResponse.data);
                                if (!orders.isEmpty()){
                                    AppDataBase dataBase = AppDataBase.getInstance(OrderService.this);
                                    for(int i=0; i<orders.size(); i++){
                                        Order order = orders.get(i);
                                        Order dbOrder = dataBase.orderDao().
                                                    getSpecificOrder(order.getCode());
                                        if (dbOrder != null){
                                            dataBase.orderDao().update(order);
                                            updateOrderDetails(order, dataBase);
                                        }
                                        else{
                                            dataBase.orderDao().insert(order);
                                            updateOrderDetails(order, dataBase);
                                        }
                                    }
                                }
                            }
                            Log.i(tag, "The DB Time : "+Methods.getDBCurrentTimeStamp());
                            User.storeLastSync(Methods.getDBCurrentTimeStamp(), getApplicationContext());
                        }

                        @Override
                        public void onError(VolleyError error) {}

                        @Override
                        public void onJobFinished() {timer.start();}
                    }).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    void post(CountDownTimer timer){
        Log.i(tag,  "Restarting the JOB");
        List<Order> orders = AppDataBase.getInstance(this).orderDao().getToBeUploadedOrders();
        for (int i = 0;  i<orders.size();  i ++){
            orders.get(i).setCourses(AppDataBase.getInstance(this).courseDao().getOrderCourses(orders.get(i).getId()));
            for (int j=0; j<orders.get(i).getCourses().size(); j++){
                Course course = orders.get(i).getCourses().get(j);
                orders.get(i).getCourses().get(j).setOrderItems(
                        AppDataBase.getInstance(this).orderItemDao().getAllCourseItems(course.getId())
                );
            }
        }
        int business_id = AppDataBase.getInstance(this).businessDao().getAllBusinesses().get(0).getId();
   ;
        Log.i(tag,  "the url is  : "+ ServerUrl.ORDER_LIST_POST);
        Log.i(tag,  "the params are : "+ Order.buildArrayParams(orders,  business_id));
        new PostTask(this, ServerUrl.ORDER_LIST_POST,  Order.buildArrayParams(orders,  business_id),
                new VolleyRequestCallback() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(String response) {
                        DatumResponse datumResponse = new Gson().fromJson(response, DatumResponse.class);
                        if (datumResponse.success){
                            long timeValue = (new Date().getTime());
                            for (int i = 0 ; i < orders.size() ; i ++){
                                orders.get(i).setUpdated_at(timeValue);
                                orders.get(i).setUploaded_at(timeValue);
                                AppDataBase.getInstance(OrderService.this).orderDao().update(orders.get(i));
                            }
                        }
                        else {

                        }
                    }

                    @Override
                    public void onError(VolleyError error) {

                    }

                    @Override
                    public void onJobFinished() {
                        timer.start();
                    }
                }).execute();


    }

    void updateOrderDetails(Order order, AppDataBase dataBase){
        for (int j=0; j<order.getCourseList().size(); j++){
            Course course = order.getCourseList().get(j);
            Course dbCourse = dataBase.courseDao().
                    getSpecificCourse(course.getCode());
            if (dbCourse != null){
                dataBase.courseDao().update(course);
                for (int k=0; k<course.getOrderItems().size(); k++){
                    OrderItem orderItem = course.getOrderItems().get(k);
                    OrderItem dbOrderItem = dataBase.orderItemDao()
                            .getSpecificOrderItem(orderItem.getMenu_item_id(), dbCourse.getId());
                    dbOrderItem.setQuantity(orderItem.getQuantity());
                    dbOrderItem.setPrice(orderItem.getPrice());
                    dbOrderItem.setCreated_at(orderItem.getCreated_at());
                    dbOrderItem.setUpdated_at(orderItem.getUpdated_at());
                    dbOrderItem.setUploaded_at(orderItem.getUpdated_at());
                    if (dbOrderItem != null){
                        Log.i(tag, "Updating orderItem : "+ dbOrderItem.toString() +" with "+orderItem.toString());
                        long up = dataBase.orderItemDao().update(dbOrderItem);
                        if (up > 0) Log.i(tag, "Updated");
                    }else {
                        Log.i(tag, "Inserting");
                        try {
                            dataBase.orderItemDao().insert(orderItem);
                        }catch (SQLiteConstraintException exception){
                            FirebaseCrashlytics.getInstance().recordException(exception);
                            FirebaseCrashlytics.getInstance().recordException(new Exception(new Gson().toJson(orderItem)));
                        }
                    }
                }
            }else{

                try {
                    dataBase.courseDao().insert(course);
                }catch (SQLiteConstraintException exception){
                    FirebaseCrashlytics.getInstance().recordException(exception);
                }

                for (int k=0; k<course.getOrderItems().size(); k++){
                    OrderItem orderItem = course.getOrderItems().get(k);
                    orderItem.setUploaded_at(orderItem.getUpdated_at());
                    try {
                        dataBase.orderItemDao().insert(orderItem);
                    }catch (SQLiteConstraintException exception){
                        FirebaseCrashlytics.getInstance().recordException(exception);
                        FirebaseCrashlytics.getInstance().recordException(new Exception(new Gson().toJson(orderItem)));
                    }
                }
            }
        }
    }
}
