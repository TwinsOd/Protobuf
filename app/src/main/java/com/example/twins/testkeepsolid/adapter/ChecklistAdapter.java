package com.example.twins.testkeepsolid.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.twins.testkeepsolid.R;
import com.example.twins.testkeepsolid.data.model.TaskModel;

import java.util.List;


public class ChecklistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VALUE_TASK = 1001;
    private static final int VALUE_TASK_LIST = 1002;
    private final List<TaskModel> taskList;
    private Context mContext;

    public ChecklistAdapter(Context context, List<TaskModel> taskList) {
        this.taskList = taskList;
        mContext = context;
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
                break;
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
        final ImageViewHolder holder = (ImageViewHolder) viewHolder;
        final TaskModel model = taskList.get(position);

        holder.titleView.setText(model.getTitle());
        holder.aliasView.setText(model.getAlias());
        switch (model.getType()) {
            case VALUE_TASK:
                holder.versionView.setText(String.format("%s %s", mContext.getString(R.string.version), model.getVersion()));
                holder.completedView.setText(String.format("%s %s", mContext.getString(R.string.completed), String.valueOf(model.isCompleted())));
                holder.descriptionView.setText(String.format("%s %s", mContext.getString(R.string.description), String.valueOf(model.getRemindOnLocation().getDescription())));
                break;
            case VALUE_TASK_LIST:
                holder.taskContainerLayout.removeAllViews();
                int i = 1;
                for (String text : model.getTasks()) {
                    TextView textView = new TextView(mContext);
                    textView.setText(String.format("%d: %s", i, text));
                    holder.taskContainerLayout.addView(textView);
                    i++;
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (taskList != null) {
            return taskList.size();
        } else {
            return 0;
        }
    }

    private static class ImageViewHolder extends RecyclerView.ViewHolder {
        private TextView aliasView, titleView;
        //VALUE_TASK
        private TextView versionView, completedView, descriptionView;
        //VALUE_TASK_LIST
        private LinearLayout taskContainerLayout;

        ImageViewHolder(final View view, int type) {
            super(view);
            aliasView = (TextView) view.findViewById(R.id.alias_view);
            titleView = (TextView) view.findViewById(R.id.title_view);

            switch (type) {
                case VALUE_TASK:
                    versionView = (TextView) view.findViewById(R.id.version_view);
                    completedView = (TextView) view.findViewById(R.id.completed_view);
                    descriptionView = (TextView) view.findViewById(R.id.description_view);
                    break;
                case VALUE_TASK_LIST:
                    taskContainerLayout = (LinearLayout) view.findViewById(R.id.task_container);
                    break;
            }
        }
    }
}
