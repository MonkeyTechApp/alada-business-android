package com.poupock.feussom.aladabusiness.job;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.callback.VolleyRequestCallback;
import com.poupock.feussom.aladabusiness.core.CoreApp;
import com.poupock.feussom.aladabusiness.core.dashboard.DashboardActivity;
import com.poupock.feussom.aladabusiness.database.AppDataBase;
import com.poupock.feussom.aladabusiness.util.Course;
import com.poupock.feussom.aladabusiness.util.Order;
import com.poupock.feussom.aladabusiness.util.OrderItem;
import com.poupock.feussom.aladabusiness.util.User;
import com.poupock.feussom.aladabusiness.web.Fetch;
import com.poupock.feussom.aladabusiness.web.ServerUrl;
import com.poupock.feussom.aladabusiness.web.response.DataResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
                    if (dbOrderItem != null){
                        Log.i(tag, "Updating "+new Gson().toJson(dbOrderItem));
                        long up = dataBase.orderItemDao().update(orderItem);
                        if (up > 0) Log.i(tag, "Updated");
                    }else {
                        Log.i(tag, "Inserting");
                        dataBase.orderItemDao().insert(orderItem);
                    }
                }
            }else{
                dataBase.courseDao().insert(course);
                for (int k=0; k<course.getOrderItems().size(); k++){
                    OrderItem orderItem = course.getOrderItems().get(k);
                    dataBase.orderItemDao().insert(orderItem);
                }
            }
        }
    }
}
