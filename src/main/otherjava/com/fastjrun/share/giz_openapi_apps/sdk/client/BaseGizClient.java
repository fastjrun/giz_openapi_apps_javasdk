package com.fastjrun.share.giz_openapi_apps.sdk.client;

import java.util.Map;
import java.util.ResourceBundle;

import com.fastjrun.sdkg.client.BaseHttpClient;
import com.fastjrun.sdkg.common.CodeException;
import com.fastjrun.sdkg.helper.EncryptHelper;

import net.sf.json.JSONObject;

public abstract class BaseGizClient extends BaseHttpClient {

	protected String genericUrlPre;

	protected String appId;

	protected String appSecret;

	protected String x_gizwits_application_auth;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String getGenericUrlPre() {
		return genericUrlPre;
	}

	public void setGenericUrlPre(String genericUrlPre) {
		this.genericUrlPre = genericUrlPre;
	}

	public void initSDKConfig(String apiworld) {
		ResourceBundle rb = ResourceBundle.getBundle(apiworld + "-sdk");
		this.genericUrlPre = rb.getString(apiworld + ".genericUrlPre");
		this.appId = rb.getString(apiworld + ".appId");
		this.appSecret = rb.getString(apiworld + ".appSecret");
		try {
			this.x_gizwits_application_auth = EncryptHelper.md5Digest(this.appId + this.appSecret).toLowerCase();
		} catch (Exception e) {
			log.error("", e);
		}
	}

	@Override
	protected JSONObject process(String reqStr, String urlReq, String method, Map<String, String> requestProperties) {
		requestProperties.put("Accept", "application/json");
		requestProperties.put("X-Gizwits-Application-Id", this.appId);
		if (urlReq.endsWith("/app/request_token")) {
			requestProperties.put("X-Gizwits-Application-Auth", this.x_gizwits_application_auth);

			log.debug(this.x_gizwits_application_auth);
		}
		log.debug(reqStr);
		JSONObject responseJsonObject = this.processInternal(reqStr, urlReq, method, requestProperties);
		log.debug(responseJsonObject);
		String error_code=null;
		try {
			error_code = responseJsonObject.getString("error_code");			
		}catch(Exception e) {
			
		}
		if (error_code != null && !error_code.equals("")) {
			String error_message = responseJsonObject.getString("error_message");
			throw new CodeException(error_code, error_message);
		} 
		return responseJsonObject;
		
		

	}
}