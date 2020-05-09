package com.cheercent.springboot.xserver.base;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cheercent.springboot.xserver.conf.DataKey;

/*
 * @copyright (c) xhigher 2020
 * @author xhigher    2020-5-1
 */
public abstract class XLogic implements Cloneable, InitializingBean {

	protected static final Logger logger = LoggerFactory.getLogger(XLogic.class);
	
	private XLogicConfig logicConfig = null;
	
	protected XLogicSession logicSession = null;
	protected JSONObject logicParameters = null;
	private XContext context = null;
	
	private boolean initialized = false;
	
	public void setConfig(XLogicConfig config){
		this.logicConfig = config;
	}
	
	public XLogicConfig getConfig() {
		return this.logicConfig;
	}
	
	public boolean hasParameter(String name){
		return logicParameters.containsKey(name);
	}

	public String getString(String name){
		return logicParameters.getString(name);
	}
	
	public Integer getInteger(String name){
		return logicParameters.getInteger(name);
	}
	
	public Long getLong(String name){
		return logicParameters.getLong(name);
	}
	
	public Double getDouble(String name){
		return logicParameters.getDouble(name);
	}
	
	public <T extends Enum<T>> T getEnum(Class<T> enumType, String name) {
		try {
			return Enum.valueOf(enumType, logicParameters.getString(name));
		}catch(Exception e){}
		return null;
	}
	
	public JSONArray getJSONArray(String name){
		return logicParameters.getJSONArray(name);
	}
	
	public JSONObject getJSONObject(String name){
		return logicParameters.getJSONObject(name);
	}
	
	public String getClientIP(){
		return this.getString(DataKey.CLIENT_IP);
	}
	
	public String getClientVersion(){
		return this.getString(DataKey.CLIENT_VERSION);
	}
	
	public String getClientDevice(){
		return this.getString(DataKey.CLIENT_DEVICE);
	}
	
	public void startTransaction(){
		if(context == null){
			context = new XContext();
		}
		context.startTransaction();
	}
	
	public XContext getContext(){
		if(context == null){
			context = new XContext();
		}
		return context;
	}
	
	public boolean submitTransaction(){
		return context.submitTransaction();
	}
	
	protected boolean requireSession(){
		return true;
	}
	
	protected boolean requireAccountBound(){
		return false;
	}
	
	protected abstract String prepare();
	
	protected abstract String execute();
	
	public XLogic init(JSONObject parameters) {
		this.logicParameters = parameters;
		this.logicSession = new XLogicSession(this.getString(DataKey.PEERID), this.getString(DataKey.SESSIONID));
		this.initialized = true;
		return this;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		
		
	}
	
	public String outputResult(){
		try{
			if(!this.initialized) {
				return XLogicResult.errorInternal();
			}

			String prepareResult = this.prepare();
			if(prepareResult != null){
				return prepareResult;
			}

			if(this.requireSession()){
				String sessionResult = this.logicSession.checkSession(this.requireAccountBound());
				if(sessionResult != null){
					return sessionResult;
				}
			}
			
			String executeResult = this.execute();
			if(executeResult == null) {
				executeResult = XLogicResult.success();
			}
			return executeResult;
		}catch(Exception e){
			logger.error(this.getClass().getSimpleName(), e);
			return XLogicResult.errorInternal();
		}finally{
			if(this.context != null) {
				this.context.endTransaction(false);
			}
		}
	}
	
	@Override
	public XLogic clone() {
		try{
			return (XLogic) super.clone();
		}catch(CloneNotSupportedException e){
		}
		return null;
	}
	

	


}
