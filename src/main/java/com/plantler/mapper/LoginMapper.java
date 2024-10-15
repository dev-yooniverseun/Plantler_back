package com.plantler.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import com.plantler.dto.UserDTO;


@Mapper
public interface LoginMapper {
	
	@Select("select * from pl_user where user_id = #{user_id} and user_pwd = #{user_pwd} and user_delYN = true")
	public UserDTO login(UserDTO userDTO);

	@Select("SELECT * FROM pl_user WHERE user_no = #{user_no} and user_delYN = true")
	public UserDTO findOne(int no);
	
	@Select("SELECT * FROM pl_user WHERE user_id = #{id} and user_delYN = true")
	public UserDTO findById(String id);
	
	@Select("SELECT * FROM pl_user where user_email = #{user_email} and user_delYN = true")
	public UserDTO findUserByEmail(UserDTO userDTO);

	@Update("UPDATE pl_user SET user_nick = #{user_nick}, user_id = #{user_id}, user_pwd = #{user_pwd}, user_email = #{user_email}, user_moddate= NOW() WHERE user_no = #{user_no}")
	public int edit(Map map);
	
	@Update("UPDATE pl_user SET user_pwd = #{user_pwd} WHERE user_id = #{user_id} AND user_email = #{user_email} and user_delYN = true")
	public int updatePwd(UserDTO userDTO);
	
	 @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "user_no", before = false, resultType = Integer.class)
	 @Insert("INSERT INTO pl_user (user_nick, user_id, user_pwd, user_email, user_provider, user_delYN) VALUES (#{user_nick}, #{user_id}, #{user_pwd}, #{user_email}, #{user_provider}, true)")
	 public int save(UserDTO userDTO);
	 
	 @Select("SELECT COUNT(*) > 0 FROM pl_user WHERE user_id = #{user_id} and user_delYN = true")
	 public boolean isIdExists(Map map);

	 @Select("SELECT COUNT(*) > 0 FROM pl_user WHERE user_nick = #{user_nick} and user_delYN = true")
	 public boolean isNickExists(Map map);
	 
	 @Update("UPDATE pl_user SET user_delYN = false WHERE user_nick = #{nick}")
	 public int deleteUserByNick(String nick);

}
