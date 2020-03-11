package org.hsrm.demo.app.service.impl;

import io.choerodon.core.exception.CommonException;
import org.hsrm.demo.app.service.TaskService;
import org.hsrm.demo.domin.entity.Task;
import org.hsrm.demo.domin.repository.TaskRepository;
import org.hsrm.demo.domin.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 张洪涛
 * @create 2020-03-08 22:51
 */
@Service
public class TaskServiceImpl implements TaskService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public TaskServiceImpl(UserRepository userRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Task create(Task task) {
        task.generateTaskNumber();
        taskRepository.insert(task);
        return task;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Task update(Task task) {
        Task task1 = taskRepository.selectByPrimaryKey(task);
        if(task1==null){
            throw new CommonException("htdo.warn.task.notFound");
        }
        // 更新指定字段
        taskRepository.updateOptional(task,
                Task.FIELD_STATE,
                Task.FIELD_TASK_DESCRIPTION
        );
        return task;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByTaskNumber(String taskNumber) {
        Task task = new Task();
        task.setTaskNumber(taskNumber);
        taskRepository.delete(task);
        //taskRepository.selectDetailByTaskNumber(taskNumber);
    }
}
