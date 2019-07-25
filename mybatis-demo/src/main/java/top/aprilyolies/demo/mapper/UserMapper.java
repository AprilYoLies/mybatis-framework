package top.aprilyolies.demo.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.aprilyolies.demo.pojo.User;

/**
 * @Author EvaJohnson
 * @Date 2019-07-25
 * @Email g863821569@gmail.com
 */
public interface UserMapper {
  @Select("select * from user where id = #{id}")
  User getUserById(@Param("id") int id);
}
