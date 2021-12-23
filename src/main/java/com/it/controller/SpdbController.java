package com.it.controller;

import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.vod.model.v20170321.GetPlayInfoRequest;
import com.aliyuncs.vod.model.v20170321.GetPlayInfoResponse;

import com.aliyun.vod.upload.req.UploadVideoRequest;

import com.aliyun.vod.upload.resp.UploadVideoResponse;
import com.aliyuncs.DefaultAcsClient;
import com.it.Result.CommonResult;
import com.it.Util.FileUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.transform.Result;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
@RestController
public class SpdbController {

        //账号AK信息请填写(必选)
        private static final String accessKeyId = "LTAI5t6YE1EgtVRSbjrZsupX";
        //账号AK信息请填写(必选)
        private static final String accessKeySecret =  "v4aBxYBywPGEkNaMTWBQWLtDe4gYgz";

 /*       public static void main(String[] args) {
            // 一、视频文件上传
            // 视频标题(必选)
            String title = "测试标题";
            // 1.本地文件上传和文件流上传时，文件名称为上传文件绝对路径，如:/User/sample/文件名称.mp4 (必选)
            // 2.网络流上传时，文件名称为源文件名，如文件名称.mp4(必选)。
            // 任何上传方式文件名必须包含扩展名
            String fileName = "F:/Download/aaa.mp4";
            // 本地文件上传
            testUploadVideo(accessKeyId, accessKeySecret, title, fileName);

            // 待上传视频的网络流地址
            String url = "http://xxxx.xxxx.com/xxxx.mp4";

            // 2.网络流上传
            // 文件扩展名，当url中不包含扩展名时，需要设置该参数
            String fileExtension = "mp4";
            //testUploadURLStream(accessKeyId, accessKeySecret, title, url, fileExtension);

            // 二、图片上传
            //testUploadImageLocalFile(accessKeyId, accessKeySecret);

        }


        */

    public static DefaultAcsClient initVodClient() {
        String REGION_ID = "LTAI5t6YE1EgtVRSbjrZsupX";
        /** 阿里云API的密钥Access Key ID */
        String accessKeyId = "v4aBxYBywPGEkNaMTWBQWLtDe4gYgz";
        /** 阿里云API的密钥Access Key Secret */
         String accessKeySecret = "cn-shanghai";
        //点播服务所在的Region，国内请填cn-shanghai，不要填写别的区域
        DefaultProfile profile = DefaultProfile.getProfile(REGION_ID, accessKeyId, accessKeySecret);
        DefaultAcsClient client = new DefaultAcsClient(profile);
        return client;
    }



        /**
         * 本地文件上传接口
         */
        @RequestMapping("/uploadFileSpdb")
        private CommonResult  testUploadVideo(MultipartFile file) throws IOException, ClientException {
            String accessKeyId = "LTAI5t6YE1EgtVRSbjrZsupX";
            String accessKeySecret =  "v4aBxYBywPGEkNaMTWBQWLtDe4gYgz";
            String REGION_ID = "cn-shanghai";
            String title="测试";

            // 创建SubmitMediaInfoJob实例并初始化
            DefaultProfile profile = DefaultProfile.getProfile(REGION_ID, accessKeyId, accessKeySecret);
            IAcsClient client = new DefaultAcsClient(profile);
            GetPlayInfoRequest request1 = new GetPlayInfoRequest();

           //获取文件名
          String fileName=file.getOriginalFilename();

            // MultipartFile转 File
            File resultFile = FileUtil.MultipartFileToFile(file);
        //    System.out.println(resultFile.getPath());

            UploadVideoRequest request = new UploadVideoRequest(accessKeyId, accessKeySecret, title, fileName);
            /* 可指定分片上传时每个分片的大小，默认为2M字节 */
            request.setPartSize(2 * 1024 * 1024L);
            /* 可指定分片上传时的并发线程数，默认为1，(注：该配置会占用服务器CPU资源，需根据服务器情况指定）*/
            request.setTaskNum(1);

            UploadVideoImpl uploader = new UploadVideoImpl();
            UploadVideoResponse response = uploader.uploadVideo(request);

            resultFile.delete();
            if (response.isSuccess())
            {
                System.out.print("VideoId=" + response.getVideoId() + "\n");
            request1.setVideoId(response.getVideoId());
                GetPlayInfoResponse response1 = client.getAcsResponse(request1);
                for (GetPlayInfoResponse.PlayInfo playInfo : response1.getPlayInfoList()) {
                    // 播放地址
                    String playURL = playInfo.getPlayURL();
                    System.out.println("PlayInfo.PlayURL = " + playURL);
                    return new CommonResult<>(200, "上传成功",playURL);
                }
                return new CommonResult<>(200, "上传成功",response.getVideoId());
            }

            return  new CommonResult<>(100, "上传失败",response.getMessage());
        }

}
