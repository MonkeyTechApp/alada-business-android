package com.poupock.feussom.aladabusiness.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.poupock.feussom.aladabusiness.database.dao.BusinessDao;
import com.poupock.feussom.aladabusiness.database.dao.CourseDao;
import com.poupock.feussom.aladabusiness.database.dao.GuestTableDao;
import com.poupock.feussom.aladabusiness.database.dao.InternalPointDao;
import com.poupock.feussom.aladabusiness.database.dao.MenuItemCategoryDao;
import com.poupock.feussom.aladabusiness.database.dao.MenuItemDao;
import com.poupock.feussom.aladabusiness.database.dao.OrderDao;
import com.poupock.feussom.aladabusiness.database.dao.OrderItemDao;
import com.poupock.feussom.aladabusiness.database.dao.RoleDao;
import com.poupock.feussom.aladabusiness.database.dao.UserDao;
import com.poupock.feussom.aladabusiness.util.Business;
import com.poupock.feussom.aladabusiness.util.Constant;
import com.poupock.feussom.aladabusiness.util.Course;
import com.poupock.feussom.aladabusiness.util.GuestTable;
import com.poupock.feussom.aladabusiness.util.InternalPoint;
import com.poupock.feussom.aladabusiness.util.MenuItem;
import com.poupock.feussom.aladabusiness.util.MenuItemCategory;
import com.poupock.feussom.aladabusiness.util.Order;
import com.poupock.feussom.aladabusiness.util.OrderItem;
import com.poupock.feussom.aladabusiness.util.Role;
import com.poupock.feussom.aladabusiness.util.User;


@Database(
    version = 7,
    entities = {User.class, Role.class, InternalPoint.class, MenuItemCategory.class, MenuItem.class, GuestTable.class,
    Course.class, OrderItem.class, Order.class, Business.class},
    autoMigrations = {
        @AutoMigration(
            from = 6,
            to = 7
        )
    }
)

public abstract class AppDataBase extends RoomDatabase {

    private static AppDataBase instance;
    private static final String tag = AppDataBase.class.getSimpleName();

    public abstract UserDao userDao();
    public abstract BusinessDao businessDao();
    public abstract RoleDao roleDao();
    public abstract OrderItemDao orderItemDao();
    public abstract OrderDao orderDao();
    public abstract MenuItemDao menuItemDao();
    public abstract MenuItemCategoryDao menuItemCategoryDao();
    public abstract InternalPointDao internalPointDao();
    public abstract GuestTableDao guestTableDao();
    public abstract CourseDao courseDao();

    public static synchronized AppDataBase getInstance(Context context)
    {
        if(instance==null)
        {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDataBase.class, Constant.DB_NAME)
                    .addCallback(roomCallback)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration().build();
        }

        return instance;

    }

    private static final Callback roomCallback = new Callback()
    {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db)
        {
            Log.i(tag,"The database is created");
            super.onCreate(db);
            new PopulateDbAsyncTasks(instance).execute();
        }
    };

    private static class PopulateDbAsyncTasks extends AsyncTask<Void,Void,Void> {

        private PopulateDbAsyncTasks(AppDataBase db) {

        }

        @Override
        protected Void doInBackground(Void...voids) {

            return null;
        }
    }


}
