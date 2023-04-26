package com.poupock.feussom.aladabusiness.ui.adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.callback.DialogCallback;
import com.poupock.feussom.aladabusiness.callback.ListItemClickCallback;
import com.poupock.feussom.aladabusiness.callback.ProcessCallback;
import com.poupock.feussom.aladabusiness.callback.VolleyRequestCallback;
import com.poupock.feussom.aladabusiness.database.AppDataBase;
import com.poupock.feussom.aladabusiness.ui.dialog.ConfirmationCodeDialogFragment;
import com.poupock.feussom.aladabusiness.util.Course;
import com.poupock.feussom.aladabusiness.util.OrderItem;
import com.poupock.feussom.aladabusiness.util.User;
import com.poupock.feussom.aladabusiness.web.PostTask;
import com.poupock.feussom.aladabusiness.web.ServerUrl;
import com.poupock.feussom.aladabusiness.web.response.DatumResponse;

import java.util.HashMap;
import java.util.List;

@SuppressLint("SetTextI18n")
public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder>{

    private final FragmentManager manager;
    private List<Course> courses;
    private final Context context;
    private final DialogCallback dialogCallback;
    private final DialogCallback courseCallBack;

    public CourseAdapter(Context context, List<Course> courses, DialogCallback callback, FragmentManager manager,
                         DialogCallback courseCallBack){
        this.context = context;
        this.courses = courses;
        this.dialogCallback = callback;
        this.courseCallBack = courseCallBack;
        this.manager = manager;
    }

    public void setCourses(List<Course> courses){
        this.courses = courses;
    }

    @NonNull
    @Override
    public CourseAdapter.CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_course, parent, false);
        return new CourseAdapter.CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseAdapter.CourseViewHolder holder, int position) {

        Course course = this.courses.get(position);

        holder.txtName.setText(context.getString(R.string.course)+"-"+course.getId());
        holder.list.setLayoutManager(new LinearLayoutManager(context));
        if(course.getOrderItems()!=null)
            holder.list.setAdapter(new OrderItemAdapter(context, course.getOrderItems(), dialogCallback));

        // Handle direct sale hide delete button.
        if (User.getSaleMode(context).equalsIgnoreCase(User.SALE_DIRECT_MODE)){
            holder.imgDelete.setVisibility(View.GONE);
            holder.imgDelete.setEnabled(false);
        }
        else {
            holder.imgDelete.setVisibility(View.VISIBLE);
            holder.imgDelete.setEnabled(true);
        }

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFragment = ConfirmationCodeDialogFragment.newInstance(1,
                        new DialogCallback() {
                            @Override
                            public void onActionClicked(Object o, int action) {
                                if (action == DialogCallback.DONE){
                                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            switch (i){
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    delete(o.toString(), course);
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    dialogInterface.dismiss();
                                                    break;
                                            }
                                        }
                                    };
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setMessage(context.getString(R.string.confirm_course_deletion)).setPositiveButton(context.getString(R.string.yes),
                                                    dialogClickListener)
                                            .setNegativeButton(context.getString(R.string.no), dialogClickListener).show();
                                }
                            }
                        });
                dialogFragment.show(manager, ConfirmationCodeDialogFragment.class.getSimpleName());

            }
        });


    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder{
        TextView txtName;
        RecyclerView list;
        ImageView imgDelete;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            imgDelete = itemView.findViewById(R.id.imgDelete);
            txtName = itemView.findViewById(R.id.txtCourseTitle);
            list = itemView.findViewById(R.id.list);
        }
    }

    void delete(String code, Course course){
        HashMap<String, String> map = new HashMap<>();
        map.put("course", course.getCode()+"");
        map.put("code", code+"");


        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.deleting_course));
        progressDialog.setCancelable(false);

        Log.i(PostTask.class.getSimpleName(), "The params are : "+ map.toString());
        new PostTask(context, ServerUrl.COURSE_DEL, map,
                new VolleyRequestCallback() {
                    @Override
                    public void onStart() {
                        progressDialog.show();
                    }

                    @Override
                    public void onSuccess(String response) {
                        DatumResponse datumResponse = new Gson().fromJson(response, DatumResponse.class);
                        if (datumResponse.success){
                            AppDataBase.getInstance(context).courseDao().delete(course);
                            deleteItems(course.getId());
                            courseCallBack.onActionClicked(course, 1);
                            Toast.makeText(context, R.string.course_deleted_successs, Toast.LENGTH_LONG).show();
                        }
                        else {
                            if (datumResponse.data == null){
                                AppDataBase.getInstance(context).courseDao().delete(course);
                                deleteItems(course.getId());
                                courseCallBack.onActionClicked(course, 1);
                                Toast.makeText(context, R.string.course_deleted_successs, Toast.LENGTH_LONG).show();
                            }
                            Toast.makeText(context, R.string.course_not_deleted, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        Toast.makeText(context, R.string.server_error, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onJobFinished() {
                        progressDialog.dismiss();
                    }
                }).execute();
    }

    void deleteItems(int id){
        List<OrderItem> orderItems = AppDataBase.getInstance(context).orderItemDao().getAllCourseItems(id);
        for (int i=0; i<orderItems.size(); i++)
            AppDataBase.getInstance(context).orderItemDao().delete(orderItems.get(i));
    }
}
