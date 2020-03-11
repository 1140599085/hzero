package org.hsrm.demo.app.service;

import org.hsrm.demo.domin.entity.User;

/**
 * @author 张洪涛
 * @create 2020-03-08 22:41
 */
public interface UserService {
    /**
     * 创建用户
     *
     * @param user User
     * @return User
     */
    User create(User user);

    /**
     * 删除用户(同时删除任务)
     *
     * @param userId 用户ID
     */
    void delete(Long userId);
}
