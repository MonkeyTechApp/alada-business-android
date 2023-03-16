package com.poupock.feussom.aladabusiness.ui.fragment.order;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.callback.DialogCallback;
import com.poupock.feussom.aladabusiness.callback.ListItemClickCallback;
import com.poupock.feussom.aladabusiness.callback.VolleyRequestCallback;
import com.poupock.feussom.aladabusiness.core.menu.CreateMenuFragment;
import com.poupock.feussom.aladabusiness.database.AppDataBase;
import com.poupock.feussom.aladabusiness.databinding.FragmentCreateOrderBinding;
import com.poupock.feussom.aladabusiness.ui.adapter.CourseAdapter;
import com.poupock.feussom.aladabusiness.ui.adapter.MenuItemAdapter;
import com.poupock.feussom.aladabusiness.ui.adapter.MenuItemCategoryVerticalAdapter;
import com.poupock.feussom.aladabusiness.ui.dialog.ListDialogFragment;
import com.poupock.feussom.aladabusiness.util.Constant;
import com.poupock.feussom.aladabusiness.util.Course;
import com.poupock.feussom.aladabusiness.util.GuestTable;
import com.poupock.feussom.aladabusiness.util.MenuItem;
import com.poupock.feussom.aladabusiness.util.MenuItemCategory;
import com.poupock.feussom.aladabusiness.util.Methods;
import com.poupock.feussom.aladabusiness.util.Order;
import com.poupock.feussom.aladabusiness.util.OrderItem;
import com.poupock.feussom.aladabusiness.util.User;
import com.poupock.feussom.aladabusiness.util.relation.CourseWithItemListRelation;
import com.poupock.feussom.aladabusiness.util.relation.OrderCourseListRelation;
import com.poupock.feussom.aladabusiness.web.PostTask;
import com.poupock.feussom.aladabusiness.web.ServerUrl;
import com.poupock.feussom.aladabusiness.web.response.DatumResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
public class CreateOrderFragment extends Fragment implements View.OnClickListener {

    private OrderViewModel orderViewModel;
    private FragmentCreateOrderBinding binding;
    Gson gson = new Gson();
    private String TAG = CreateOrderFragment.class.getSimpleName();
    private CourseAdapter courseListAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        orderViewModel =
                new ViewModelProvider(requireActivity()).get(OrderViewModel.class);

        binding = FragmentCreateOrderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        orderViewModel.getGuestTableMutableLiveData().observe(getViewLifecycleOwner(), new Observer<GuestTable>() {
            @Override
            public void onChanged(@Nullable GuestTable guestTable) {
                binding.btnTable.setText(guestTable.getTitle());
                Log.i(TAG,"The guest table has been updated!!!");
                List<Order> orders = AppDataBase.getInstance(requireContext()).orderDao().getTableOrders(guestTable.getId(), Constant.STATUS_OPEN);
                if(orders==null){
                    orders = new ArrayList<>();
                }
                if(orders.size() < 1 ){

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
        binding.listMenuItems.setLayoutManager(new GridLayoutManager(requireContext(),2));
        courseListAdapter = new CourseAdapter(requireContext(), new ArrayList<>(), new DialogCallback() {
            @Override
            public void onActionClicked(Object o, int action) {

            }
        });
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
                                                int courseIndex = Course.getActiveCourseIndex(order.getCourseList());
                                                Log.i(TAG,"The course index is "+courseIndex);
                                                Course course = order.getCourseList().get(courseIndex);
                                                Log.i(TAG,"The course index is "+course.getTitle()+" and the status is "+course.getStatus());
                                                MenuItem menuItem = gson.fromJson(gson.toJson(o), MenuItem.class);
                                                int menuItemIndex = MenuItem.getMenuIndexInList(course.getOrderItems(),menuItem);
                                                Log.i(TAG,"The menu item in the list is index : "+menuItemIndex);
                                                OrderItem orderItem;
                                                if(menuItemIndex < 0){
                                                    Log.i(TAG,"The menu item is new and item is "+ menuItem.getTitle()+ " and the id : "+menuItem.getId());
                                                    orderItem = new OrderItem();
                                                    orderItem.setCourse_id(course.getId());
                                                    orderItem.setCourse(course);
                                                    orderItem.setMenuItem(menuItem);
                                                    orderItem.setMenu_item_id(menuItem.getId());
                                                    orderItem.setQuantity(1);
                                                    orderItem.setCreated_at(Methods.getCurrentTimeStamp());
                                                    orderItem.setPrice(menuItem.getPrice());
                                                    AppDataBase.getInstance(requireContext()).orderItemDao().insert(orderItem); // add the new added order item to the DB.
                                                    orderItem =
                                                        AppDataBase.getInstance(requireContext()).orderItemDao().getOrderByCourseIdAndMenuItem(course.getId(),menuItem.getId());
                                                }
                                                else {
                                                    orderItem = course.getOrderItems().get(menuItemIndex);
                                                    orderItem.setQuantity(orderItem.getQuantity() + 1);
                                                    Log.i(TAG,"Setting the quantity upper "+orderItem.getQuantity());
                                                    AppDataBase.getInstance(requireContext()).orderItemDao().update(orderItem);

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
                                            else {
                                                Toast.makeText(requireContext(), R.string.please_select_add_a_course, Toast.LENGTH_LONG).show();
                                            }
                                        }
                                        else {
                                            Toast.makeText(requireContext(), R.string.please_select_a_table, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            }));
                    }

                }
            }));

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
                course.setGuest_table_id(guestTable.getId());
                course.setCreated_at(Methods.getCurrentTimeStamp());
                course.setStatus(Constant.STATUS_OPEN);

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
        else if(binding.btnPay==view){
            Order order =  orderViewModel.getOrderMutableLiveData().getValue();
            Objects.requireNonNull(order).setStatus(Constant.STATUS_CLOSED);
            AppDataBase.getInstance(requireContext()).orderDao().update(order);
        }
        else if(binding.btnAddOrder == view){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            GuestTable guestTable = (orderViewModel.getGuestTableMutableLiveData().getValue());
                            if (guestTable != null){
                                Order order = new Order( 0, Methods.generateCode(), User.currentUser(requireContext()).getId(), 1,  guestTable.getId(), Constant.STATUS_OPEN,
                                        Methods.getCurrentTimeStamp()
                                );

                                Log.i(TAG,"The order size is "+AppDataBase.getInstance(requireContext()).orderDao().getAllOrders().size());
                                order = AppDataBase.getInstance(requireContext()).orderDao().getSpecificOrder(
                                        AppDataBase.getInstance(requireContext()).orderDao().insert(order));

                                Log.i(TAG, "The order id is "+order.getId());
                                order.setCourseList(AppDataBase.getInstance(requireContext()).courseDao().getOrderCourses(order.getId()));


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
//            new PostTask()
            ProgressDialog dialog = new ProgressDialog(requireContext());
            dialog.setMessage(getString(R.string.sending_order_3_dots));
            new PostTask(requireContext(), ServerUrl.ORDER, orderViewModel.getOrderMutableLiveData().getValue().buildParams(),
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
                                Order datum = Order.getObjectFromObject(datumResponse.data);
                                datum.setServerId(datum.getId());

                                AppDataBase.getInstance(requireContext()).orderDao().update(datum);
                            }
                            else {
                                Toast.makeText(requireContext(), R.string.order_sent, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(VolleyError error) {

                        }

                        @Override
                        public void onJobFinished() {

                        }
                    }).execute();
        }
    }
}
