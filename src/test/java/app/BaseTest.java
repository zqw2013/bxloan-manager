package app;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import com.coamctech.bxloan.manager.utils.Encodes;
import com.coamctech.bxloan.manager.utils.TokenUtils;
import com.coamctech.bxloan.manager.utils.encrypt.Cryptos;
import com.coamctech.bxloan.manager.utils.encrypt.MD5Util;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseTest {
	private static Logger logger = LoggerFactory.getLogger(BaseTest.class);
	protected static final String base = "http://localhost:8080/";

    protected static String token = "477b9bb3a60beb023698abcb62a336c0v101000000_f8749b2ce88fc56125d6b6a22a37506d1178f793";
    protected static String sign = "415F24D4C5C09169A706ACE283602DE7";
    public static void main(String[] args) {
        login();
        topColumns();
        banner();
        customColumn();
        haveCustomDocColumns();
        noCustomDocColumns();
        cancelCustomColumn();
        worlds();
    }
    public static void haveCustomDocColumns(){
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new NameValuePair("topLevelColumnId","2"));
        addTokenAndSign(nvps);
        String res = post(nvps,"api/app/home/haveCustomDocColumns");
    }
    public static void cancelCustomColumn(){
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new NameValuePair("columnId","7"));
        addTokenAndSign(nvps);
        String res = post(nvps,"api/app/home/cancelCustomColumn");
    }
    public static void customColumn(){
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new NameValuePair("columnId","6"));
        addTokenAndSign(nvps);
        String res = post(nvps,"api/app/home/customColumn");
    }
    public static void noCustomDocColumns(){
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new NameValuePair("topLevelColumnId","2"));
        addTokenAndSign(nvps);
        String res = post(nvps,"api/app/home/noCustomDocColumns");
    }
    public static void worlds(){
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new NameValuePair("pageIndex","0"));
        addTokenAndSign(nvps);
        String res = post(nvps,"api/app/home/worlds");
    }
    public static void banner(){
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        addTokenAndSign(nvps);
        String res = post(nvps,"api/app/home/banner");
    }
    public static void topColumns(){
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        addTokenAndSign(nvps);
        String res = post(nvps,"api/app/home/topColumns");
    }
    protected static void login(){
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new NameValuePair("userName", "admin"));
		nvps.add(new NameValuePair("password", MD5Util.md5Hex("12345678")));
		nvps.add(new NameValuePair("deviceCode", "123456789abcdef"));
		String res = post(nvps,"api/app/user/anon/login");
	}
    public static void addTokenAndSign(List<NameValuePair> nvps){
        Map<String,String> params = new HashMap<String,String>();
        nvps.forEach(nvp -> {
            params.put(nvp.getName(), nvp.getValue());
        });
        nvps.add(new NameValuePair("token", token));
        nvps.add(new NameValuePair("sign", createSign(params, sign)));
    }
	public static String post(List<NameValuePair> nvps,String uri){
		uri = base+uri;
		logger.info("uri={}",uri);
		logger.info("params={}",nvps.toString());
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod(uri);
		method.addParameters(nvps.toArray(new NameValuePair[nvps.size()]));
		method.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
		String result = "";
		try {
			client.executeMethod(method);
			result = method.getResponseBodyAsString();
			logger.info("response status code = {} ",method.getStatusCode());
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		};
		logger.info(result);
		logger.info("===============================================================================================================");
		logger.info(formatJson(result));

		return result;
	}
	 /**
     * 格式化
     * @param jsonStr
     * @return
     */
    private static String formatJson(String jsonStr) {
        if (null == jsonStr || "".equals(jsonStr)) return "";
        StringBuilder sb = new StringBuilder();
        char last = '\0';
        char current = '\0';
        int indent = 0;
        for (int i = 0; i < jsonStr.length(); i++) {
            last = current;
            current = jsonStr.charAt(i);

            //遇到{ [换行，且下一行缩进
            switch (current) {
                case '{':
                case '[':
                    sb.append(current);
                    sb.append('\n');
                    indent++;
                    addIndentBlank(sb, indent);
                    break;

              //遇到} ]换行，当前行缩进
                case '}':
                case ']':
                    sb.append('\n');
                    indent--;
                    addIndentBlank(sb, indent);
                    sb.append(current);
                    break;

               //遇到,换行
                case ',':
                    sb.append(current);
                    if (last != '\\') {
                        sb.append('\n');
                        addIndentBlank(sb, indent);
                    }
                    break;
                default:
                    sb.append(current);
            }
        }
 
        return sb.toString();
    }

	/**
	 * 生成签名
	 * 规则:
	 * 		1.请求参数名按字符排序
	 * 		2.按如下格式拼接成字符串str:
	 * 			param1=value1&param2=value2&param3=value3
	 * 		3.使用HMAC-SHA1算法根据签名秘钥对拼接后的字符串进行加密,生成一个字节数组
	 * 		4.对加密后返回的字节数组转换成小写的十六进制字符串
	 *
	 * @params 请求参数 包括token
	 *	signKey 签名秘钥 登陆后返回
	 */

	public static String createSign(Map<String,String> params,String signKey){
		if(params==null || params.size()==0){
			return "";
		}
		TreeMap treemap = new TreeMap<String, String>();
		params.entrySet().forEach(entry -> {
			treemap.put(entry.getKey(), entry.getValue());
		});
		StringBuilder sb = new StringBuilder();
		for(Map.Entry<String,String> entry : params.entrySet()){
			sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}
		sb.deleteCharAt(sb.length() - 1);
		try {
			Encodes.encodeHex(Cryptos.hmacSha1(sb.toString().getBytes("UTF-8"),signKey.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}
    /**
     * 添加space
     * @param sb
     * @param indent
     */
    private static void addIndentBlank(StringBuilder sb, int indent) {
        for (int i = 0; i < indent; i++) {
            sb.append('\t');
        }
    }
}
