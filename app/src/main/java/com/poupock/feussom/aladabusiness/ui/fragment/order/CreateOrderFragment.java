package com.poupock.feussom.aladabusiness.ui.fragment.order;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.callback.DialogCallback;
import com.poupock.feussom.aladabusiness.callback.ListItemClickCallback;
import com.poupock.feussom.aladabusiness.callback.ProcessCallback;
import com.poupock.feussom.aladabusiness.callback.VolleyRequestCallback;
import com.poupock.feussom.aladabusiness.database.AppDataBase;
import com.poupock.feussom.aladabusiness.databinding.FragmentCreateOrderBinding;
import com.poupock.feussom.aladabusiness.ui.adapter.CourseAdapter;
import com.poupock.feussom.aladabusiness.ui.adapter.MenuItemAdapter;
import com.poupock.feussom.aladabusiness.ui.adapter.MenuItemCategoryVerticalAdapter;
import com.poupock.feussom.aladabusiness.ui.dialog.ListDialogFragment;
import com.poupock.feussom.aladabusiness.ui.dialog.OrderDetailDialogFragment;
import com.poupock.feussom.aladabusiness.ui.dialog.OrderItemUpdateQuantityDialogFragment;
import com.poupock.feussom.aladabusiness.util.Constant;
import com.poupock.feussom.aladabusiness.util.Course;
import com.poupock.feussom.aladabusiness.util.GuestTable;
import com.poupock.feussom.aladabusiness.util.MenuItem;
import com.poupock.feussom.aladabusiness.util.MenuItemCategory;
import com.poupock.feussom.aladabusiness.util.Methods;
import com.poupock.feussom.aladabusiness.util.Order;
import com.poupock.feussom.aladabusiness.util.OrderItem;
import com.poupock.feussom.aladabusiness.util.User;
import com.poupock.feussom.aladabusiness.util.Variation;
import com.poupock.feussom.aladabusiness.util.relation.CourseWithItemListRelation;
import com.poupock.feussom.aladabusiness.util.relation.OrderCourseListRelation;
import com.poupock.feussom.aladabusiness.web.PostTask;
import com.poupock.feussom.aladabusiness.web.ServerUrl;
import com.poupock.feussom.aladabusiness.web.response.DatumResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
public class CreateOrderFragment extends Fragment implements View.OnClickListener {

    private OrderViewModel orderViewModel;
    private FragmentCreateOrderBinding binding;
    Gson gson = new Gson();
    private String TAG = CreateOrderFragment.class.getSimpleName();
    private CourseAdapter courseListAdapter;

    Bitmap bitmap;
    public static int REQUEST_PERMISSIONS = 1;
    private boolean boolean_permission;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        orderViewModel =
                new ViewModelProvider(requireActivity()).get(OrderViewModel.class);

        binding = FragmentCreateOrderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        courseListAdapter = buildCourseAdapter();

        orderViewModel.getGuestTableMutableLiveData().observe(getViewLifecycleOwner(), new Observer<GuestTable>() {
            @Override
            public void onChanged(@Nullable GuestTable guestTable) {
                binding.btnTable.setText(guestTable.getTitle());
                Log.i(TAG,"The guest table has been updated!!!");
                List<Order> orders = AppDataBase.getInstance(requireContext()).orderDao().getTableOrders(guestTable.getId(), Constant.STATUS_OPEN);
                if(orders==null){
                    orders = new ArrayList<>();
                }
                if(orders.isEmpty() ){
                    binding.txtOrderTotal.setText("0"+getString(R.string.currency_cfa));
                    if (courseListAdapter != null)
                        courseListAdapter.setCourses(new ArrayList<>());
                    else {
                        courseListAdapter = buildCourseAdapter();
                    }
                    courseListAdapter.notifyDataSetChanged();
                }
                else {
                    if(orders.size() == 1){
                        OrderCourseListRelation orderCourseRelation = AppDataBase.getInstance(requireContext()).orderDao().getOrderWithCourseList(orders.get(0).getId());
                        if(orderCourseRelation != null){
                            actualiseOrderView(orderCourseRelation.buildOrderObject());
                        }
                        else {
                            Log.e(TAG, "The orderCourseRelation object is null");
                        }
                    }
                    else {
                        ListDialogFragment fragment = ListDialogFragment.newInstance(Order.class.getSimpleName(),
                            guestTable.getId() + "",
                            new ListItemClickCallback() {
                                @Override
                                public void onItemClickListener(Object o, boolean isLong) {
                                    actualiseOrderView(AppDataBase.getInstance(requireContext()).
                                        orderDao().getOrderWithCourseList(gson.fromJson(gson.toJson(o), Order.class).getId()).buildOrderObject());
                                }
                            });
                        fragment.show(requireActivity().getSupportFragmentManager(), ListDialogFragment.class.getSimpleName());
                    }
                }

            }
        });

        orderViewModel.getOrderMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Order>() {
            @Override
            public void onChanged(Order order) {
                Log.i(TAG,"The order has been updated!!!");
            }
        });

        binding.listMenuCategory.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.courseList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.listMenuItems.setLayoutManager(new GridLayoutManager(requireContext(),getResources().getInteger(R.integer.menu_grid_column_count)));

        binding.courseList.setAdapter(courseListAdapter);

        binding.listMenuCategory.setAdapter(new MenuItemCategoryVerticalAdapter(requireContext(),
            AppDataBase.getInstance(requireContext()).menuItemCategoryDao().getAllMenuItemCategories(),
            new ListItemClickCallback() {
                @Override
                public void onItemClickListener(Object o , boolean isLong) {
                    if(!isLong){
                        MenuItemCategory menuItemCategory = gson.fromJson(gson.toJson(o), MenuItemCategory.class);
                        binding.txtCategory.setText(menuItemCategory.getName());
                        binding.listMenuItems.setAdapter(new MenuItemAdapter(requireContext(), AppDataBase.getInstance(requireContext()).
                            menuItemDao().getSpecificCategoryItems(menuItemCategory.getId()), true,
                            new ListItemClickCallback() {
                                @Override
                                public void onItemClickListener(Object o , boolean isLong)
                                {
                                    if(!isLong)
                                    {

                                        Order order = orderViewModel.getOrderMutableLiveData().getValue();
                                        if(order != null){
                                            if(order.getCourseList() != null){
                                                if (!order.getCourseList().isEmpty()){
                                                    int courseIndex = Course.getActiveCourseIndex(order.getCourseList());
                                                    Log.i(TAG,"The course index is "+courseIndex);
                                                    Course course = order.getCourseList().get(courseIndex);
                                                    Log.i(TAG,"The course index is "+course.getTitle()+" and the status is "+course.getStatus());
                                                    MenuItem menuItem = gson.fromJson(gson.toJson(o), MenuItem.class);
                                                    final Variation[] variation = {null};
                                                    if (menuItem.getVariations().size()>0){
                                                        ListDialogFragment fragment = ListDialogFragment.newInstance(Variation.class.getSimpleName(),
                                                                new Gson().toJson(menuItem.getVariations()),
                                                                new ListItemClickCallback() {
                                                                    @Override
                                                                    public void onItemClickListener(Object o, boolean isLong) {
                                                                        //TODO
                                                                        variation[0] = gson.fromJson(gson.toJson(o), Variation.class);
                                                                        handleActualise(course, menuItem, variation[0], order, courseIndex);
                                                                    }
                                                                });
                                                        fragment.show(requireActivity().getSupportFragmentManager(), ListDialogFragment.class.getSimpleName());

                                                    }
                                                    else {
                                                        handleActualise(course, menuItem, null, order, courseIndex);
                                                    }
                                                }
                                                else {
                                                    Toast.makeText(requireContext(), R.string.please_select_add_a_course, Toast.LENGTH_LONG).show();
                                                }
                                            }
                                            else {
                                                Toast.makeText(requireContext(), R.string.please_select_add_a_course, Toast.LENGTH_LONG).show();
                                            }
                                        }
                                        else {
                                            Toast.makeText(requireContext(), R.string.please_add_an_order, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            }));
                    }

                }
            }));

        // Hiding and disabling the button if sales mode is set to private.
        if (User.getSaleMode(requireContext()).equalsIgnoreCase(User.SALE_DIRECT_MODE)) {
            binding.btnAddCourse.setVisibility(View.GONE);
            binding.btnAddCourse.setEnabled(false);
        }

        binding.btnTable.setOnClickListener(this);
        binding.btnAddCourse.setOnClickListener(this);
        binding.btnSend.setOnClickListener(this);
        binding.btnPay.setOnClickListener(this);
        binding.btnAddOrder.setOnClickListener(this);

        return root;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Log.i(TAG,"The config is landscape");
            binding.listMenuCategory.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false));
        }
        else{
            Log.i(TAG,"The config is portrait");
            binding.listMenuCategory.setLayoutManager(new LinearLayoutManager(requireContext()));
        }
    }

//    private void actualiseOrderView(GuestTable guestTable) {
//        OrderCourseListRelation orderCourseListRelation = AppDataBase.getInstance(requireContext()).orderDao().getOrderWithCourseList(guestTable.getId(),
//            Constant.STATUS_OPEN);
//
//        Order order = null;
//        if(orderCourseListRelation!=null) order = orderCourseListRelation.buildOrderObject();
//        Log.i(TAG, "The table id is "+guestTable.getId());
//        if(order != null){
//            Log.i(TAG,"The table has an order opened on it.");
//            if(order.getCourseList() != null) Log.i(TAG,"The table has a course list of size "+order.getCourseList().size());
//            else Log.i(TAG,"The table has a no course list");
//            order.setCourseList(CourseWithItemListRelation.getListCourseFromRelation(
//                AppDataBase.getInstance(requireContext()).courseDao().getOrderCoursesWithOrderItem(order.getId())));
//        }
//        else {
//            order = new Order( 0, Methods.generateCode(), 0, 0,  guestTable.getId(), Constant.STATUS_OPEN,
//                Methods.getCurrentTimeStamp()
//            );
//            AppDataBase.getInstance(requireContext()).orderDao().insert(order);
//            Log.i(TAG,"The order size is "+AppDataBase.getInstance(requireContext()).orderDao().getAllOrders().size());
//            order = AppDataBase.getInstance(requireContext()).orderDao().getSpecificOrderFromTableId(guestTable.getId(),
//                Constant.STATUS_OPEN);
//            Log.i(TAG, "The order id is "+order.getId());
//            order.setCourseList(AppDataBase.getInstance(requireContext()).courseDao().getOrderCourses(order.getId()));
//
//        }
//        orderViewModel.setOrderMutableLiveData(order);
//        if(order.getCourseList() != null){
//            Log.i(TAG, "Setting the course list");
////            binding.courseList.setAdapter(new CourseAdapter(requireContext(), order.getCourseList(), new DialogCallback() {
////                @Override
////                public void onActionClicked(Object o, int action) {
////
////                }
////            }));
//            courseListAdapter.setCourses(order.getCourseList());
//            binding.txtOrderTotal.setText(order.getTotal()+" "+getString(R.string.currency_cfa));
//            courseListAdapter.notifyDataSetChanged();
//            binding.courseList.smoothScrollToPosition(order.getCourseList().size());
//        }
//        else Log.i(TAG, "The course list is null");
//
//    }
    private void actualiseOrderView(Order updatedOrder) {
//        OrderCourseListRelation orderCourseListRelation = AppDataBase.getInstance(requireContext()).orderDao().getOrderWithCourseList(updatedOrder.getId());
//
//        Order order = null;
//        if(orderCourseListRelation!=null) order = orderCourseListRelation.buildOrderObject();
//        Log.i(TAG, "The updated_order id id is "+updatedOrder.getId());
//        if(order != null){
//            Log.i(TAG,"The table has an order opened on it.");
//            if(order.getCourseList() != null) Log.i(TAG,"The table has a course list of size "+order.getCourseList().size());
//            else Log.i(TAG,"The table has a no course list");
//            order.setCourseList(CourseWithItemListRelation.getListCourseFromRelation(
//                AppDataBase.getInstance(requireContext()).courseDao().getOrderCoursesWithOrderItem(order.getId())));
//        }
//        else {
//            order = new Order( 0, Methods.generateCode(), 0, 0,  guestTable.getId(), Constant.STATUS_OPEN,
//                Methods.getCurrentTimeStamp()
//            );
//            AppDataBase.getInstance(requireContext()).orderDao().insert(order);
//            Log.i(TAG,"The order size is "+AppDataBase.getInstance(requireContext()).orderDao().getAllOrders().size());
//            order = AppDataBase.getInstance(requireContext()).orderDao().getSpecificOrderFromTableId(guestTable.getId(),
//                Constant.STATUS_OPEN);
//            Log.i(TAG, "The order id is "+order.getId());
//            order.setCourseList(AppDataBase.getInstance(requireContext()).courseDao().getOrderCourses(order.getId()));
//
//        }
        orderViewModel.setOrderMutableLiveData(updatedOrder);
        if(updatedOrder.getCourseList() != null){
            Log.i(TAG, "Setting the course list ");
//            binding.courseList.setAdapter(new CourseAdapter(requireContext(), order.getCourseList(), new DialogCallback() {
//                @Override
//                public void onActionClicked(Object o, int action) {
//
//                }
//            }));
            updatedOrder.setCourseList(CourseWithItemListRelation.getListCourseFromRelation(
                AppDataBase.getInstance(requireContext()).courseDao().getOrderCoursesWithOrderItem(updatedOrder.getId())));
            Log.i(TAG, "The number of courses is : "+updatedOrder.getCourseList().size());
            Log.i(TAG, "The oder id is : "+updatedOrder.getId());
            Log.i(TAG, "The courses are : "+gson.toJson(AppDataBase.getInstance(requireContext()).courseDao().getAllCourses()));
            courseListAdapter.setCourses(updatedOrder.getCourseList());
            binding.txtOrderTotal.setText(updatedOrder.getTotal()+" "+getString(R.string.currency_cfa));
            courseListAdapter.notifyDataSetChanged();
            binding.courseList.smoothScrollToPosition(updatedOrder.getCourseList().size());
        }
        else Log.i(TAG, "The course list is null");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View view) {
        if(binding.btnTable == view){
            Log.i(TAG, "Btn table showed");
            DialogFragment listDialogFragment = ListDialogFragment.newInstance(
                GuestTable.class.getSimpleName(), "",null);
            listDialogFragment.show(getParentFragmentManager(),ListDialogFragment.class.getSimpleName());
        }
        else if(binding.btnAddCourse == view){
            Log.i(TAG,"Button add course clicked");
            if (orderViewModel.getOrderMutableLiveData().getValue()!=null){
                Order order = Objects.requireNonNull(orderViewModel.getOrderMutableLiveData().getValue());
                GuestTable guestTable = Objects.requireNonNull(orderViewModel.getGuestTableMutableLiveData().getValue());
                Course course = new Course();
                course.setTitle("TITLE");
                course.setOrder_id(order.getId());
                course.setCode(User.currentUser(requireContext()).getId()+"-"+UUID.randomUUID().toString());
                course.setGuest_table_id(guestTable.getId());
                course.setCreated_at(Methods.getCurrentTimeStamp());
                course.setStatus(Constant.STATUS_OPEN);
                course.setUpdated_at((new Date().getTime()));

                if(order.getCourseList() != null) {
                    Log.i(TAG,"Course list not null");
                    for (int i=0; i< order.getCourseList().size(); i++){
                        order.getCourseList().get(i).setStatus(Constant.STATUS_CLOSED);
                    }
                    order.getCourseList().add(course);
                }
                else {
                    Log.i(TAG,"Course list null");
                    List<Course> courses = new ArrayList<>();
                    courses.add(course);
                    order.setCourseList(courses);
                    if(order.getCourseList() != null) Log.i(TAG,"Course list init");
                }


                AppDataBase.getInstance(requireContext()).courseDao().insert(course);
                orderViewModel.setOrderMutableLiveData(order);

                actualiseOrderView(order);
            }else {
                Snackbar.make(requireView(), R.string.select_a_table, Snackbar.LENGTH_LONG).show();
            }
        }
        else if(binding.btnAddOrder == view){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            GuestTable guestTable = (orderViewModel.getGuestTableMutableLiveData().getValue());
                            if (guestTable != null){
                                Order order = new Order( 0, Methods.generateCode(requireContext()), User.currentUser(requireContext()).getId(), 1,  guestTable.getId(), Constant.STATUS_OPEN,
                                    1, Methods.getCurrentTimeStamp(), (new Date().getTime()),  0);

                                Log.i(TAG,"The order size is "+AppDataBase.getInstance(requireContext()).orderDao().getAllOrders().size());
                                order = AppDataBase.getInstance(requireContext()).orderDao().getSpecificOrder(
                                        AppDataBase.getInstance(requireContext()).orderDao().insert(order));

                                Log.i(TAG, "The order id is "+order.getId());
                                order.setCourseList(AppDataBase.getInstance(requireContext()).courseDao().getOrderCourses(order.getId()));

                                if (User.getSaleMode(requireContext()).equalsIgnoreCase(User.SALE_DIRECT_MODE)){
                                    Course course = new Course();
                                    course.setTitle("TITLE");
                                    course.setOrder_id(order.getId());
                                    course.setCode(User.currentUser(requireContext()).getId()+"-"+UUID.randomUUID().toString());

                                    course.setGuest_table_id(guestTable.getId());
                                    course.setCreated_at(Methods.getCurrentTimeStamp());
                                    course.setStatus(Constant.STATUS_OPEN);
                                    course.setUpdated_at((new Date().getTime()));

                                    AppDataBase.getInstance(requireContext()).courseDao().insert(course);
                                    if (order.getCourseList() == null){
                                        order.setCourseList(new ArrayList<>());
                                    }
                                    order.getCourseList().add(course);
                                }

                                orderViewModel.setOrderMutableLiveData(order);

                                if(order.getCourseList() != null) {
                                    Log.i(TAG, "Setting the course list");
                                    courseListAdapter.setCourses(order.getCourseList());
                                    binding.txtOrderTotal.setText(order.getTotal() + " " + getString(R.string.currency_cfa));
                                    courseListAdapter.notifyDataSetChanged();
                                    binding.courseList.smoothScrollToPosition(order.getCourseList().size());
                                }
                                actualiseOrderView(order);
                                dialog.dismiss();
                            }
                            else {
                                Snackbar.make(requireView(), R.string.please_select_a_table, Snackbar.LENGTH_LONG).show();
                            }
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.dismiss();
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setMessage(getString(R.string.confirm_new_order_for_table)).setPositiveButton(getString(R.string.yes), dialogClickListener)
                .setNegativeButton(getString(R.string.no), dialogClickListener).show();
        }
        else if (binding.btnSend == view){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int position) {
                    switch (position){
                        case DialogInterface.BUTTON_POSITIVE:

                            Order order = orderViewModel.getOrderMutableLiveData().getValue();
                            Log.i(TAG, "The time is : "+new Date().getTime());
                            Log.i(TAG, "The time is : "+(int)(new Date().getTime()));

                            long timeValue = (new Date().getTime());
                            order.setUpdated_at(timeValue);
//                            order.setUploaded_at(timeValue);
                            for (int i=0; i<order.getCourses().size(); i++){
                                order.getCourses().get(i).setUploaded_at(timeValue);
                                order.getCourses().get(i).setUploaded_at(timeValue);
                                for (int j=0; j<order.getCourses().get(i).getItems().size(); j++){
//                                    order.getCourses().get(i).getOrderItems().get(j).setUploaded_at(timeValue);
                                    order.getCourses().get(i).getOrderItems().get(j).setUpdated_at(timeValue);
                                    AppDataBase.getInstance(requireContext()).orderItemDao().update(order.getCourses().get(i).getOrderItems().get(j));
                                }
                                AppDataBase.getInstance(requireContext()).courseDao().update(order.getCourses().get(i));
                            }
                            AppDataBase.getInstance(requireContext()).orderDao().update(order);
                            Snackbar.make(requireView(), R.string.order_updated, Snackbar.LENGTH_LONG).show();
                            requireActivity().onBackPressed();
//                            sendOrder(orderViewModel.getOrderMutableLiveData().getValue(), new ProcessCallback() {
//                                @Override
//                                public void done() {
//
//                                }
//
//                                @Override
//                                public void failed() {
//                                }
//                            });
//                            dialogInterface.dismiss();
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            dialogInterface.dismiss();
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setMessage(getString(R.string.confirm_sending_order)).setPositiveButton(getString(R.string.yes), dialogClickListener)
                    .setNegativeButton(getString(R.string.no), dialogClickListener).show();

        }
        else if(binding.btnPay==view){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            // update the orders
                            Order order =  orderViewModel.getOrderMutableLiveData().getValue();
                            Objects.requireNonNull(order).setStatus(Constant.STATUS_CLOSED);
                            long timeValue = (new Date().getTime());
                            order.setUpdated_at(timeValue);
                            for (int i=0; i<order.getCourses().size(); i++){
                                order.getCourses().get(i).setUploaded_at(timeValue);
                                order.getCourses().get(i).setUploaded_at(timeValue);
                                for (int j=0; j<order.getCourses().get(i).getItems().size(); j++){
                                    order.getCourses().get(i).getOrderItems().get(j).setUpdated_at(timeValue);
                                    AppDataBase.getInstance(requireContext()).orderItemDao().update(order.getCourses().get(i).getOrderItems().get(j));
                                }
                                AppDataBase.getInstance(requireContext()).courseDao().update(order.getCourses().get(i));
                            }
                            AppDataBase.getInstance(requireContext()).orderDao().update(order);
                            requireActivity().onBackPressed();
                            Snackbar.make(requireView(), R.string.order_updated, Snackbar.LENGTH_LONG).show();
//                            sendOrder(order, new ProcessCallback() {
//                                @Override
//                                public void done() {
//
//                                }
//
//                                @Override
//                                public void failed() { }
//                            });

                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.dismiss();
                            DialogFragment detailDialogFragment = OrderDetailDialogFragment.newInstance("", "");
                            detailDialogFragment.show(getChildFragmentManager(), OrderDetailDialogFragment.class.getSimpleName());
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setMessage(getString(R.string.confirm_payment_received)).setPositiveButton(getString(R.string.yes), dialogClickListener)
                    .setNegativeButton(getString(R.string.print), dialogClickListener).show();
        }
    }

    CourseAdapter buildCourseAdapter(){
        return new CourseAdapter(requireContext(), new ArrayList<>(), new DialogCallback() {
            @Override
            public void onActionClicked(Object o, int action) {
                if (DialogCallback.LONG_CLICK == action){
                    OrderItem orderItem = OrderItem.getObjectFromObject(o);

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:

                                    HashMap<String, String> map = new HashMap<>();
                                    Course course = AppDataBase.getInstance(requireContext()).courseDao().getSpecificCourse(orderItem.getCourse_id());
                                    if (course != null){
                                        map.put("course_id", course.getCode());
                                        map.put("menu_item_id", orderItem.getMenu_item_id()+"");

                                        Log.i(TAG, "The map is "+map.toString());
                                        ProgressDialog progressDialog = new ProgressDialog(requireContext());
                                        progressDialog.setMessage(getString(R.string.deleting_order_items_3_dots));
                                        progressDialog.setCancelable(false);
                                        new PostTask(requireContext(), ServerUrl.ORDER_ITEM_DEL, map,
                                                new VolleyRequestCallback() {
                                                    @Override
                                                    public void onStart() {
                                                        progressDialog.show();
                                                    }

                                                    @Override
                                                    public void onSuccess(String response) {
                                                        DatumResponse datumResponse = new Gson().fromJson(response, DatumResponse.class);
                                                        if (datumResponse.success){
                                                            AppDataBase.getInstance(requireContext()).orderItemDao().delete(orderItem);
                                                            actualiseOrderView(orderViewModel.getOrderMutableLiveData().getValue());
                                                            Toast.makeText(requireContext(), R.string.order_item_deleted_successs, Toast.LENGTH_LONG).show();
                                                        }
                                                        else {
                                                            if (datumResponse.data == null){
                                                                AppDataBase.getInstance(requireContext()).orderItemDao().delete(orderItem);
                                                                actualiseOrderView(orderViewModel.getOrderMutableLiveData().getValue());
                                                                Toast.makeText(requireContext(), R.string.order_item_deleted_successs, Toast.LENGTH_LONG).show();
                                                            }
                                                            Toast.makeText(requireContext(), R.string.order_item_not_deleted, Toast.LENGTH_LONG).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onError(VolleyError error) {
                                                        Toast.makeText(requireContext(), R.string.server_error, Toast.LENGTH_LONG).show();
                                                    }

                                                    @Override
                                                    public void onJobFinished() {
                                                        progressDialog.dismiss();
                                                    }
                                                }).execute();
                                    }

                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    };

                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
                    builder.setTitle(R.string.order_item_actions)
                            .setCancelable(true)
                            .setItems(R.array.edit_del, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    if(which == 0){
                                        DialogFragment updateDialog = OrderItemUpdateQuantityDialogFragment.newInstance(orderItem.getId(),
                                                new DialogCallback() {
                                                    @Override
                                                    public void onActionClicked(Object o, int action) {
                                                        actualiseOrderView(orderViewModel.getOrderMutableLiveData().getValue());
                                                    }
                                                });
                                        updateDialog.show(getChildFragmentManager(), OrderItemUpdateQuantityDialogFragment.class.getSimpleName());
                                    }
                                    else if(which == 1){
                                        AlertDialog.Builder deletionBuilder = new AlertDialog.Builder(requireContext());
                                        deletionBuilder.setMessage(getString(R.string.confirm_order_item_deletion)+" : "
                                                        + AppDataBase.getInstance(requireContext()).menuItemDao().getSpecificMenuItem(orderItem.getMenu_item_id()).getName()
                                                        +" "+getString(R.string.with_quantity)+" "+ orderItem.getQuantity())
                                                .setPositiveButton(getString(R.string.yes), dialogClickListener)
                                                .setNegativeButton(getString(R.string.no), dialogClickListener).show();
                                    }
                                }
                            }).create().show();


                }
            }
        }, requireActivity().getSupportFragmentManager(), new DialogCallback() {
            @Override
            public void onActionClicked(Object o, int action) {
                actualiseOrderView(orderViewModel.getOrderMutableLiveData().getValue());
            }
        });
    }

    public void sendOrder(Order order, ProcessCallback processCallback){
        ProgressDialog dialog = new ProgressDialog(requireContext());
        dialog.setMessage(getString(R.string.sending_order_3_dots));
        Log.i(TAG, "The order is "+ order.buildParams(AppDataBase.getInstance(requireContext()).businessDao().getAllBusinesses().get(0).getId()).toString());
        new PostTask(requireContext(), ServerUrl.ORDER_POST,
                order.buildParams(AppDataBase.getInstance(requireContext()).businessDao().getAllBusinesses().get(0).getId()),
                new VolleyRequestCallback() {
                    @Override
                    public void onStart() {
                        dialog.setCancelable(false);
                        dialog.show();
                    }

                    @Override
                    public void onSuccess(String response) {
                        DatumResponse datumResponse = new Gson().fromJson(response, DatumResponse.class);
                        if (datumResponse.success){
//                            Order datum = Order.getObjectFromObject(datumResponse.data);
//                            AppDataBase.getInstance(requireContext()).orderDao().update(datum);
                            processCallback.done();
                            Toast.makeText(requireContext(), R.string.order_sent, Toast.LENGTH_LONG).show();
                        }
                        else {
                            processCallback.failed();
                            Toast.makeText(requireContext(), R.string.order_not_sent, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        processCallback.failed();
                        Toast.makeText(requireContext(), R.string.server_error, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onJobFinished() {
                        dialog.dismiss();
                    }
                }).execute();
    }

    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)||
                (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);

            }

            if ((ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);

            }
        } else {
            boolean_permission = true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                boolean_permission = true;
            } else {
                Toast.makeText(requireContext(), "Please allow the permission", Toast.LENGTH_LONG).show();

            }
        }
    }

    void handleActualise(Course course, MenuItem menuItem, Variation variation, Order order, int courseIndex){

        int menuItemIndex = MenuItem.getMenuIndexInList(course.getOrderItems(),menuItem, variation);
        Log.i(TAG,"The menu item in the list is index : "+menuItemIndex);
        OrderItem orderItem;
        if(menuItemIndex < 0){
            Log.i(TAG,"The menu item is new and item is "+ menuItem.getName()+ " and the id : "+menuItem.getId());
            orderItem = new OrderItem();
            orderItem.setCourse_id(course.getId());
            orderItem.setCourse(course);
            orderItem.setMenuItem(menuItem);
            orderItem.setMenu_item_id(menuItem.getId());
            if(variation != null) orderItem.setVariation_id(variation.getId()+"");
            orderItem.setQuantity(1);
            orderItem.setCreated_at(Methods.getCurrentTimeStamp());
            double  price = (variation == null) ? (menuItem.getPrice()) : (variation.getPrice_adjustment()+menuItem.getPrice());
            orderItem.setPrice(price);
            orderItem.setUpdated_at((new Date().getTime()));
            AppDataBase.getInstance(requireContext()).orderItemDao().insert(orderItem); // add the new added order item to the DB.
            orderItem =
                    AppDataBase.getInstance(requireContext()).orderItemDao().getOrderByCourseIdAndMenuItem(course.getId(),menuItem.getId());
        }
        else {
            orderItem = course.getOrderItems().get(menuItemIndex);
            orderItem.setQuantity(orderItem.getQuantity() + 1);
            Log.i(TAG,"Setting the quantity upper "+orderItem.getQuantity());
            orderItem.setUpdated_at((new Date().getTime()));
            AppDataBase.getInstance(requireContext()).orderItemDao()    .update(orderItem);

        }
        if(course.getOrderItems() != null){
            Log.i(TAG, "The course has a list of items");
            if(menuItemIndex < 0) course.getOrderItems().add(orderItem);
            else course.getOrderItems().set(menuItemIndex, orderItem);
        }
        else {
            Log.i(TAG, "The course has no items");
            List<OrderItem> orderItems = new ArrayList<>();
            orderItems.add(orderItem);
            course.setOrderItems(orderItems);
        }
        order.getCourseList().set(courseIndex, course); // set the course to its new value
        Log.i(TAG,"The order course has been updated");
        orderViewModel.setOrderMutableLiveData(order);
        actualiseOrderView(orderViewModel.getOrderMutableLiveData().getValue());
//                                                if(orderViewModel.getGuestTableMutableLiveData().getValue()!=null)
//                                                    actualiseOrderView((orderViewModel.getGuestTableMutableLiveData().getValue()));
    }

}
