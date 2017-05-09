package com.example.twins.testkeepsolid.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.twins.testkeepsolid.LoadingData;
import com.example.twins.testkeepsolid.R;
import com.example.twins.testkeepsolid.data.model.TaskModel;

import java.util.List;

public class ChecklistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VALUE_TASK = 1001;
    private static final int VALUE_TASK_LIST = 1002;
    private final List<TaskModel> taskList;
    private Context mContext;
    private LoadingData interfaceLoadingData;

    public ChecklistAdapter(Context context, List<TaskModel> taskList, LoadingData interfaceLoadingData) {
        this.taskList = taskList;
        this.interfaceLoadingData = interfaceLoadingData;
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
        holder.aliasView.setText(boldFirstWord(R.string.alias, model.getAlias()));
        switch (model.getType()) {
            case VALUE_TASK:
                holder.versionView.setText(boldFirstWord(R.string.version, model.getVersion()));
                holder.completedView.setText(boldFirstWord(R.string.completed, String.valueOf(model.isCompleted())));
                String description = model.getRemindOnLocation().getDescription();
                if (description == null || description.equals(""))
                    holder.descriptionView.setVisibility(View.GONE);
                else
                    holder.descriptionView.setText(boldFirstWord(R.string.description, description));
                break;
            case VALUE_TASK_LIST:
                holder.taskContainerLayout.removeAllViews();
                int i = 1;
                for (String text : model.getTasks()) {
                    TextView textView = new TextView(mContext);
                    textView.setText(boldFirstWord(i + ":", text));
                    holder.taskContainerLayout.addView(textView);
                    i++;
                }
                break;
        }
        if (getItemCount() == position + 5) interfaceLoadingData.setRequest();
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

    private Spanned boldFirstWord(int idFirst, String strSecond) {
        return fromHtml("<b>" + mContext.getString(idFirst) + "</b>" + " " + strSecond);
    }

    private Spanned boldFirstWord(String strFirst, String strSecond) {
        if (strSecond == null) return fromHtml("");
        return fromHtml("<b>" + strFirst + "</b>" + " " + strSecond);
    }

    @SuppressWarnings("deprecation")
    private Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }
}
