package com.cheercent.springboot.xserver.logic.user;

import com.alibaba.fastjson.JSONObject;
import com.cheercent.springboot.xserver.base.XLogic;
import com.cheercent.springboot.xserver.base.XLogicConfig;
import com.cheercent.springboot.xserver.base.XLogicResult;
import com.cheercent.springboot.xserver.conf.DataKey;
import com.cheercent.springboot.xserver.model.UserInfoModel;

@XLogicConfig()
public final class Info extends XLogic {

    @Override
    protected boolean requireSession() {
        return false;
    }
    
    private String userid;

    @Override
    protected String prepare() {
    	userid = this.getString(DataKey.USERID);
        if (userid.length() != 12) {
            return XLogicResult.errorParameter("USERID_ERROR");
        }
        
        return null;
    }

	@Override
	protected String execute() {
		UserInfoModel userModel = new UserInfoModel();
		JSONObject userInfo = userModel.getInfo(userid);
		if(userInfo == null) {
			return XLogicResult.errorInternal();
		}
		return XLogicResult.success(userInfo);
	}

}
