package com.poupock.feussom.aladabusiness.ui.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.callback.DialogCallback;
import com.poupock.feussom.aladabusiness.callback.ListItemClickCallback;
import com.poupock.feussom.aladabusiness.util.Course;

import java.util.List;

@SuppressLint("SetTextI18n")
public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder>{

    private List<Course> courses;
    private final Context context;
    private final DialogCallback dialogCallback;

    public CourseAdapter(Context context, List<Course> courses, DialogCallback callback){
        this.context = context;
        this.courses = courses;
        this.dialogCallback = callback;
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
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder{
        TextView txtName;
        RecyclerView list;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtCourseTitle);
            list = itemView.findViewById(R.id.list);
        }
    }
}
