package com.example.twins.testkeepsolid.adapter;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.twins.testkeepsolid.R;
import com.example.twins.testkeepsolid.data.model.TaskModel;

class ValueTaskHolder extends RecyclerView.ViewHolder implements TaskModelConsumer {
    private TextView aliasView, titleView;
    private TextView versionView, completedView, descriptionView;

    ValueTaskHolder(final View view) {
        super(view);
        aliasView = (TextView) view.findViewById(R.id.alias_view);
        titleView = (TextView) view.findViewById(R.id.title_view);
        versionView = (TextView) view.findViewById(R.id.version_view);
        completedView = (TextView) view.findViewById(R.id.completed_view);
        descriptionView = (TextView) view.findViewById(R.id.description_view);
    }

    @Override
    public void accept(TaskModel model) {
        Resources resources = titleView.getResources();
        titleView.setText(model.getTitle());
        aliasView.setText(HtmlUtils.boldFirstWord(resources.getString(R.string.alias), model.getAlias()));
        versionView.setText(HtmlUtils.boldFirstWord(resources.getString(R.string.version), model.getVersion()));
        completedView.setText(HtmlUtils.boldFirstWord(resources.getString(R.string.completed), String.valueOf(model.isCompleted())));
        String description = model.getRemindOnLocation().getDescription();
        if (description == null || description.equals(""))
            descriptionView.setVisibility(View.GONE);
        else
            descriptionView.setText(HtmlUtils.boldFirstWord(resources.getString(R.string.description), description));
    }
}
