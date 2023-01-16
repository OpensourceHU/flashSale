package com.jesper.seckill.infra.db.mapper;

import com.jesper.seckill.infra.db.pojos.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * Created by OpensourceHU on 2021/5/21.
 */
@Mapper
public interface UserMapper {

    @Select("select * from sk_user where id = #{id}")
    User getById(@Param("id") long id);

    @Update("update sk_user set password = #{password} where id = #{id}")
    void update(User toBeUpdate);
}
