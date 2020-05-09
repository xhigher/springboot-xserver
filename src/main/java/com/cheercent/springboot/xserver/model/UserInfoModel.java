package com.cheercent.springboot.xserver.model;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.cheercent.springboot.xserver.util.CommonUtils;


public class UserInfoModel extends BusinessDatabase {

	@Override
	protected String tableName() {
		return "user_info";
	}

	public JSONObject getInfoByUsername(String username){
		return this.prepare().addWhere("username",username).find();
	}

	public JSONObject getInfo(String userid){
		return this.prepare().addWhere("userid",userid).find();
	}
	
	public boolean updateInfo(String userid, String nickname, String avatar){
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("nickname", nickname);
		values.put("avatar", avatar);
		values.put("updatetime", CommonUtils.getCurrentYMDHMS());
		return this.prepare().set(values).addWhere("userid", userid).update();
	}


}
