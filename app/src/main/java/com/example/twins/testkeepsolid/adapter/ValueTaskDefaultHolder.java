package com.example.twins.testkeepsolid.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.twins.testkeepsolid.R;
import com.example.twins.testkeepsolid.data.model.TaskModel;

class ValueTaskDefaultHolder extends RecyclerView.ViewHolder implements TaskModelConsumer {
    private TextView aliasView, titleView;

    ValueTaskDefaultHolder(final View view) {
        super(view);
        aliasView = (TextView) view.findViewById(R.id.alias_view);
        titleView = (TextView) view.findViewById(R.id.title_view);
    }

    @Override
    public void accept(TaskModel model) {
        titleView.setText(model.getTitle());
        aliasView.setText(HtmlUtils.boldFirstWord(aliasView.getResources().getString(R.string.alias), model.getAlias()));
    }
}
