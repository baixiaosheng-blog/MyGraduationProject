package top.baixiaosheng.mygraduationserver.dao;

import top.baixiaosheng.mygraduationserver.dataobject.UserDO;

public interface UserDOMapper {
    int deleteByUId(Integer uid);
    int insert(UserDO record);

    int insertSelective(UserDO record);

    UserDO selectByUId(Integer uid);

    UserDO selectByEmail(String email);

    int updateByPrimaryKeySelective(UserDO record);

    int updateByPrimaryKey(UserDO record);
}