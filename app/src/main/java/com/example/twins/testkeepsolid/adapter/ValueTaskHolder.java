package com.example.twins.testkeepsolid.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import com.example.twins.testkeepsolid.R;
import com.example.twins.testkeepsolid.data.model.TaskModel;

/**
 * Created by User on 05-Jun-17.
 */
class ValueTaskHolder extends RecyclerView.ViewHolder implements Consumer<TaskModel> {
    private TextView aliasView, titleView;
    //VALUE_TASK
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
        titleView.setText(model.getTitle());
        aliasView.setText(boldFirstWord(R.string.alias, model.getAlias()));
        versionView.setText(boldFirstWord(R.string.version, model.getVersion()));
        completedView.setText(boldFirstWord(R.string.completed, String.valueOf(model.isCompleted())));
        String description = model.getRemindOnLocation().getDescription();
        if (description == null || description.equals(""))
            descriptionView.setVisibility(View.GONE);
        else
            descriptionView.setText(boldFirstWord(R.string.description, description));
    }

    private Spanned boldFirstWord(int idFirst, String strSecond) {
        return fromHtml("<b>" + itemView.getResources().getString(idFirst) + "</b>" + " " + strSecond);
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
