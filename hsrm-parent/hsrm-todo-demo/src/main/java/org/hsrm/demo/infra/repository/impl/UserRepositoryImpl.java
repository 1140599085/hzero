package org.hsrm.demo.infra.repository.impl;

import org.hsrm.demo.domin.entity.User;
import org.hsrm.demo.domin.repository.UserRepository;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.stereotype.Component;

/**
 * @author 张洪涛
 * @create 2020-03-08 22:23
 */
@Component
public class UserRepositoryImpl extends BaseRepositoryImpl<User> implements UserRepository {

}
