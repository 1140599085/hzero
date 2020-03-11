package org.hsrm.demo.app.service;

import org.hsrm.demo.domin.entity.Task;

/**
 * @author 张洪涛
 * @create 2020-03-08 22:42
 */
public interface TaskService {
    /**
     * 创建任务
     *
     * @param task 任务
     * @return Task
     */
    Task create(Task task);

    /**
     * 更新任务
     *
     * @param task 任务
     * @return Task
     */
    Task update(Task task);

    /**
     * 根据任务编号删除
     *
     * @param taskNumber 任务编号
     */
    void deleteByTaskNumber(String taskNumber);
}
