package com.silk.mapper;

import java.util.List;
import java.util.Map;

import com.silk.entity.User;
import org.apache.ibatis.annotations.Param;


public interface UserMapper {

	public int create(User user);

	public int delete(Integer id);

	public int update(User user);

	public int updateSelective(User user);

	public List<User> query(User user);

	public User detail(Integer id);

	public int count(User user);

	public User login(@Param("userName") String userName, @Param("userPwd") String userPwd);

	// 修改密码
	public int updatePwd(@Param("id") Integer id, @Param("userPwd") String userPwd);


	public int queryLiverAmount(Integer roomId);

	public int buildingActualStudentAmount(Integer buildingId);

	// 按角色查询所有用户ID（用于站内通知群发）
	public List<Integer> queryIdsByType(@Param("userType") Integer userType);
}