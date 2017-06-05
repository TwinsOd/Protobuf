package com.example.twins.testkeepsolid.adapter;


import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.twins.testkeepsolid.R;
import com.example.twins.testkeepsolid.data.model.TaskModel;

class ValueTaskListHolder extends RecyclerView.ViewHolder implements TaskModelConsumer {
    private TextView aliasView, titleView;
    private LinearLayout taskContainerLayout;

    ValueTaskListHolder(View view) {
        super(view);
        aliasView = (TextView) view.findViewById(R.id.alias_view);
        titleView = (TextView) view.findViewById(R.id.title_view);
        taskContainerLayout = (LinearLayout) view.findViewById(R.id.task_container);
    }

    @Override
    public void accept(TaskModel item) {
        titleView.setText(item.getTitle());
        aliasView.setText(HtmlUtils.boldFirstWord(titleView.getResources().getString(R.string.alias), item.getAlias()));
        taskContainerLayout.removeAllViews();
        int i = 1;
        for (String text : item.getTasks()) {
            TextView textView = new TextView(itemView.getContext());
            textView.setText(HtmlUtils.boldFirstWord(i + ":", text));
            TextViewCompat.setTextAppearance(textView, R.style.TextAppearance_AppCompat_Body1);
            taskContainerLayout.addView(textView);
            i++;
        }
    }


}
