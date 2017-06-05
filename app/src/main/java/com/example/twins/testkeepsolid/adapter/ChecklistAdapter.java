package com.example.twins.testkeepsolid.adapter;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.twins.testkeepsolid.LoadingData;
import com.example.twins.testkeepsolid.R;
import com.example.twins.testkeepsolid.data.model.TaskModel;

import java.util.List;

public class ChecklistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VALUE_TASK = 1001;
    public static final int VALUE_TASK_LIST = 1002;
    @NonNull
    private final List<TaskModel> taskList;
    private LoadingData interfaceLoadingData;

    public ChecklistAdapter(@NonNull List<TaskModel> taskList, LoadingData interfaceLoadingData) {
        this.taskList = taskList;
        this.interfaceLoadingData = interfaceLoadingData;
    }

    @Override
    public int getItemViewType(int position) {

        return taskList.get(position).getType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view;
        switch (i) {
            case VALUE_TASK:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_task, viewGroup, false);
                return new ValueTaskHolder(view);
            case VALUE_TASK_LIST:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_task_list, viewGroup, false);
                break;
            default:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_default, viewGroup, false);
        }

        return new ImageViewHolder(view, i);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        Consumer<TaskModel> holder = (Consumer<TaskModel>) viewHolder;
        TaskModel model = taskList.get(position);
        holder.accept(model);

        if (getItemCount() == position + 5) {
            interfaceLoadingData.setRequest();
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

}
