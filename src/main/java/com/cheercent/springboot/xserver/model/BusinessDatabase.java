package com.cheercent.springboot.xserver.model;

import com.cheercent.springboot.xserver.base.XModel;


public abstract class BusinessDatabase extends XModel {

	public static final String dataSourceName = "business";
	
	@Override
	protected String getDataSourceName() {
		return dataSourceName;
	}
	
}
