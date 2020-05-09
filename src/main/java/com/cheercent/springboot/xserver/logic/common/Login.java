package com.cheercent.springboot.xserver.logic.common;

import com.alibaba.fastjson.JSONObject;
import com.cheercent.springboot.xserver.base.XLogic;
import com.cheercent.springboot.xserver.base.XLogicConfig;
import com.cheercent.springboot.xserver.base.XLogicResult;
import com.cheercent.springboot.xserver.conf.DataKey;
import com.cheercent.springboot.xserver.util.CommonUtils;

@XLogicConfig(requiredParameters={DataKey.USERNAME, DataKey.PASSWORD})
public final class Login extends XLogic {

    @Override
    protected boolean requireSession() {
        return false;
    }
    
	private String username;
    private String password;

    @Override
    protected String prepare() {
    	username = this.getString(DataKey.USERNAME);
        if (!CommonUtils.checkPhoneNo(username)) {
            return XLogicResult.errorParameter("USERNAME_ERROR");
        }
        password = this.getString(DataKey.PASSWORD);
        if (password.length() != 32) {
            return XLogicResult.errorParameter("PASSWORD_ERROR");
        }
        return null;
    }

	@Override
	protected String execute() {
		JSONObject resultData = new JSONObject();
		resultData.put(DataKey.USERNAME, username);
		resultData.put(DataKey.PASSWORD, password);
		return XLogicResult.success(resultData);
	}

}
