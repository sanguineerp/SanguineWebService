package com.apos.dao;

import java.util.Map;

public interface inftPOSWiseItemIncentiveDao {
	
	public boolean funDeleteTruncate(String sql,String StrCode,String ind) throws Exception;
	
	public void funUpdateTable(String sql,Map<String,String> hmParameters) throws Exception;

}
