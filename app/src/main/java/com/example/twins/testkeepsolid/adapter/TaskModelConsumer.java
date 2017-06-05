package com.example.twins.testkeepsolid.adapter;


import com.example.twins.testkeepsolid.data.model.TaskModel;

public interface TaskModelConsumer {
    void accept(TaskModel item);
}
