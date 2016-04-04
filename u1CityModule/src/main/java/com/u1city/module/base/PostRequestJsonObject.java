package com.u1city.module.base;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.HttpHeaderParser;
import com.u1city.module.common.HttpCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/***************************************
 * 
 * @author lwli
 * @date 2014-12-12 
 * @time 上午11:40:57
 * 类说明:用于POST请求
 * 
 **************************************/
class PostRequestJsonObject extends Request<JSONObject> {
	private Map<String, String> params;
	private Listener<JSONObject> callBack;
	
	public PostRequestJsonObject(String url,Map<String, String> params, HttpCallBack callBack) {
		super(Request.Method.POST, url, callBack);
		this.callBack = callBack;
		this.params = params;
	}
	
	@Override  
	public RetryPolicy getRetryPolicy()  
	{  
		RetryPolicy retryPolicy = new DefaultRetryPolicy(30*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		return retryPolicy;  
	}  

	/** 获取Post参数 */
	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		return params;
	}

	@Override
	protected Response<JSONObject> parseNetworkResponse(
			NetworkResponse response) {
		try {
			String jsonString = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
			return Response.success(new JSONObject(jsonString),
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JSONException je) {
			return Response.error(new ParseError(je));
		}
	}

	@Override
	protected void deliverResponse(JSONObject responseObj) {
		callBack.onResponse(responseObj);
	}
}