package com.cheercent.springboot.xserver.logic.user;

import com.cheercent.springboot.xserver.base.XLogic;
import com.cheercent.springboot.xserver.base.XLogicConfig;
import com.cheercent.springboot.xserver.base.XLogicResult;
import com.cheercent.springboot.xserver.conf.DataKey;
import com.cheercent.springboot.xserver.model.UserInfoModel;

@XLogicConfig(requiredParameters={DataKey.USERID, DataKey.NICKNAME, DataKey.AVATAR})
public final class Update extends XLogic {

    @Override
    protected boolean requireSession() {
        return false;
    }

    private String userid;
	private String nickname;
    private String avatar;

    @Override
    protected String prepare() {
    	userid = this.getString(DataKey.USERID);
        if (userid.length() != 12) {
            return XLogicResult.errorParameter("USERID_ERROR");
        }
    	nickname = this.getString(DataKey.NICKNAME);
        if (nickname.length() < 2 || nickname.length()>100) {
            return XLogicResult.errorParameter("NICKNAME_ERROR");
        }
        avatar = this.getString(DataKey.AVATAR);
        if (avatar.isEmpty()) {
            return XLogicResult.errorParameter("AVATAR_ERROR");
        }
        return null;
    }

	@Override
	protected String execute() {
		UserInfoModel userModel = new UserInfoModel();
		if(!userModel.updateInfo(userid, nickname, avatar)) {
			return XLogicResult.error();
		}
		return XLogicResult.success();
	}

}
