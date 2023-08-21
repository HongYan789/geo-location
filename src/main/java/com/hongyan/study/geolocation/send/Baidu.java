package com.hongyan.study.geolocation.send;

import com.alibaba.fastjson.JSONObject;
import com.baidubce.http.ApiExplorerClient;
import com.baidubce.http.AppSigner;
import com.baidubce.http.HttpMethodName;
import com.baidubce.model.ApiExplorerRequest;
import com.baidubce.model.ApiExplorerResponse;
import com.hongyan.study.geolocation.config.KeyConfig;
import com.hongyan.study.geolocation.exception.GeoLocationException;
import com.hongyan.study.geolocation.model.baidu.BaiduRsp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class Baidu {

    public static BaiduRsp send(String ip, String accessKey, String secretkey){
        try {
            if (StringUtils.isEmpty(ip) || StringUtils.isEmpty(accessKey) || StringUtils.isEmpty(secretkey)) {
                throw new GeoLocationException(ip, "request params can't be empty");
            }

            log.info("开始向第三方【百度】发起在线查询，IP：" + ip);

            ApiExplorerRequest request = new ApiExplorerRequest(HttpMethodName.GET, KeyConfig.Url);
            request.setCredentials(accessKey, secretkey);
            request.addHeaderParameter("Content-Type", "application/json;charset=UTF-8");
            request.addQueryParameter("ip", ip);

            ApiExplorerClient client = new ApiExplorerClient(new AppSigner());
            ApiExplorerResponse response = client.sendRequest(request);

            if (org.apache.commons.lang3.StringUtils.isEmpty(response.getResult())) {
                throw new Exception("响应结果为空");
            }

            BaiduRsp baiduRsp = dealWithResult(response.getResult());
            if(!baiduRsp.isSuccess()){
                throw new Exception("code:"+baiduRsp.getCode()+",message:"+baiduRsp.getMessage());
            }

            return baiduRsp;
        } catch (Exception e) {
            throw new GeoLocationException(ip, "在线库查询异常：" + e.getMessage());
        }
    }

    /**
     * 处理响应结果
     * @param str
     * @return
     */
    private static BaiduRsp dealWithResult(String str) {
        str = str.replace("request-id","requestId");
        BaiduRsp rsp = JSONObject.parseObject(str, BaiduRsp.class);
        return rsp;
    }

}
