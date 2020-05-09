package com.cheercent.springboot.xserver.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cheercent.springboot.xserver.conf.DataKey;
import com.cheercent.springboot.xserver.util.CommonUtils;

public class XController {
    
	protected static final Logger logger = LoggerFactory.getLogger(XExceptionAdvice.class);
	
	@Autowired
	private ApplicationContext applicationContext;
	
	protected XLogic newLogic(Class<? extends XLogic> clazz) {
		try {
			XLogicConfig logicConfig = clazz.getAnnotation(XLogicConfig.class);
			if(logicConfig != null){
				XLogic logic = (XLogic)clazz.newInstance();
				logic.setConfig(logicConfig);
				return logic;
			}
		}catch(Exception e) {
			logger.error("newLogic.Exception", e);
		}
		return null;
	}
	
	protected XLogic getLogic(Class<? extends XLogic> clazz) {
        return applicationContext.getBean(clazz);
    }
	
    protected String executeLogic(Class<? extends XLogic> clazz, HttpServletRequest request) throws IOException {
        String requestIP = request.getHeader(DataKey.HEADER_IP);
        if (requestIP == null) {
        	requestIP = request.getRemoteAddr();
		}
        XLogic logic = getLogic(clazz);
        XLogicConfig logicConfig = logic.getConfig();
        
		String[] ipList = logicConfig.allow();
		boolean isForbidden = false;
		if(ipList.length > 0){
			isForbidden = true;
			for(int i=0,n=ipList.length; i<n; i++){
				if(ipList[i].equals(requestIP)){
					isForbidden = false;
					break;
				}
			}
			if(isForbidden){
				logger.error("executeLogic.IP_FORBIDDEN:"+requestIP);
				return XLogicResult.errorRequest();
			}
		}
		
		JSONObject requestParameters = cleanRequestParameters(request.getParameterMap());
		
        if(!requestParameters.containsKey(DataKey.PEERID)){
        	requestParameters.put(DataKey.PEERID, request.getHeader(DataKey.HEADER_PEERID));
        }
        if(!requestParameters.containsKey(DataKey.SESSIONID)){
        	requestParameters.put(DataKey.SESSIONID, request.getHeader(DataKey.HEADER_SESSIONID));
        }
        requestParameters.put(DataKey.CLIENT_VERSION, request.getHeader(DataKey.HEADER_VERSION));
        requestParameters.put(DataKey.CLIENT_DEVICE, request.getHeader(DataKey.HEADER_DEVICE));
        requestParameters.put(DataKey.CLIENT_IP, requestIP);
        
		if(logicConfig.requiredPeerid()){
			String peerid = requestParameters.getString(DataKey.PEERID);
			if(!checkPeerid(peerid)){
				return XLogicResult.errorValidation();
			}
		}
		
		if(logicConfig.requiredParameters().length > 0){
			String errinfo = this.checkRequiredParameters(requestParameters, logicConfig.requiredParameters());
			if(errinfo != null){
				return XLogicResult.errorParameter(errinfo);
			}
		}
		
		return logic.clone().init(requestParameters).outputResult();
    }
    
    private JSONObject cleanRequestParameters(Map<String, String[]> parameters){
    	JSONObject requestParameters = new JSONObject();
    	String[] tpv = null;
		String pv = null;
		JSONArray item = null;
		for(String pn : parameters.keySet()){
			tpv = parameters.get(pn);
			if(tpv.length > 1){
				item = new JSONArray(Arrays.asList(tpv));
				pv = item.toJSONString();
			}else if(parameters.get(pn).length == 1) {
				pv = parameters.get(pn)[0].trim();
			}else{
				pv = "";
			}
			requestParameters.put(pn, pv);
		}
		return requestParameters;
    }
    
	public String checkRequiredParameters(JSONObject parameters, final String[] requires){
		if(requires != null && requires.length > 0){
			String pn = null;
			List<String> lackedParams = new ArrayList<String>();
			for(int i=0,n=requires.length; i<n; i++){
				pn = requires[i];
				if(!parameters.containsKey(pn)){
					lackedParams.add(pn);
				}
			}
			if(lackedParams.size() > 0){
				return "PARAMETER_LACKED"+lackedParams.toString();
			}
		}
		return null;
	}
	
	private boolean checkPeerid(String peerid) {
		try {
			if(peerid!=null && peerid.length() == 20){
				int rn = Integer.parseInt(peerid.substring(4, 5));
				int mn = Integer.parseInt(peerid.substring(13, 14));
				String ts36 = peerid.substring(5, 13);
				long ts = Long.valueOf(ts36, 36);
				if(ts % rn == mn) {
					return true;
				}
			}
		}catch(Exception e){	
		}
		return false;
	}
	
	public static String createPeerid(char clientType) {
        long ts = System.currentTimeMillis();
        long rn = (long) Math.floor(Math.random() * 9) + 1;
        long mn = ts % rn;
        return clientType + CommonUtils.randomString(3, true) + rn + Long.toString(ts, 36) + mn + CommonUtils.randomString(6, true);
    }
 
}
