package com.it.controller;


import com.aliyun.oss.OSS;

import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import com.it.Result.CommonResult;
import com.it.pojo.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class OssController {

    @RequestMapping("/uploadFilesOSS")
    public CommonResult UploadFile(MultipartFile file) throws IOException {
        String endpoint = "oss-accelerate.aliyuncs.com";
        String accessKeyId = "LTAI5t6YE1EgtVRSbjrZsupX";
        String accessKeySecret = "v4aBxYBywPGEkNaMTWBQWLtDe4gYgz";
        String bucketName = "test-oys";

        String fileName=file.getOriginalFilename();
      //  System.out.println(fileName);
       //生成随机名
      //  fileName = "oys_" + new Date().getTime() + fileName.substring(fileName.lastIndexOf("."));
       String objectName = "JAVA-OSS/"+fileName;
       // System.out.println(objectName);
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

// 创建InitiateMultipartUploadRequest对象。
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, objectName);

// 如果需要在初始化分片时设置文件存储类型，请参考以下示例代码。
// ObjectMetadata metadata = new ObjectMetadata();
// metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
// request.setObjectMetadata(metadata);


// 初始化分片。
        InitiateMultipartUploadResult upresult = ossClient.initiateMultipartUpload(request);
// 返回uploadId，它是分片上传事件的唯一标识。您可以根据该uploadId发起相关的操作，例如取消分片上传、查询分片上传等。
        String uploadId = upresult.getUploadId();

// partETags是PartETag的集合。PartETag由分片的ETag和分片号组成。
        List<PartETag> partETags =  new ArrayList<PartETag>();
// 每个分片的大小，用于计算文件有多少个分片。单位为字节。
        final long partSize = 100 * 1024 * 1024L;   //1 MB。

// 填写本地文件的完整路径。如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件。

    //    final File sampleFile = new File("F:\\Download\\bbb.mp4");
        long fileLength =file.getSize();
        int partCount = (int) (fileLength / partSize);
        if (fileLength % partSize != 0) {
            partCount++;
        }
// 遍历分片上传。
        for (int i = 0; i < partCount; i++) {
            long startPos = i * partSize;
            long curPartSize = (i + 1 == partCount) ? (fileLength - startPos) : partSize;
            InputStream instream = file.getInputStream();
            // 跳过已经上传的分片。
            instream.skip(startPos);
            UploadPartRequest uploadPartRequest = new UploadPartRequest();
            uploadPartRequest.setBucketName(bucketName);
            uploadPartRequest.setKey(objectName);
            uploadPartRequest.setUploadId(uploadId);
            uploadPartRequest.setInputStream(instream);
            // 设置分片大小。除了最后一个分片没有大小限制，其他的分片最小为100 KB。
            uploadPartRequest.setPartSize(curPartSize);
            // 设置分片号。每一个上传的分片都有一个分片号，取值范围是1~10000，如果超出此范围，OSS将返回InvalidArgument错误码。
            uploadPartRequest.setPartNumber( i + 1);
            // 每个分片不需要按顺序上传，甚至可以在不同客户端上传，OSS会按照分片号排序组成完整的文件。
            UploadPartResult uploadPartResult = ossClient.uploadPart(uploadPartRequest);
            // 每次上传分片之后，OSS的返回结果包含PartETag。PartETag将被保存在partETags中。
            partETags.add(uploadPartResult.getPartETag());
        }


// 创建CompleteMultipartUploadRequest对象。
// 在执行完成分片上传操作时，需要提供所有有效的partETags。OSS收到提交的partETags后，会逐一验证每个分片的有效性。当所有的数据分片验证通过后，OSS将把这些分片组合成一个完整的文件。
        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                new CompleteMultipartUploadRequest(bucketName, objectName, uploadId, partETags);

// 如果需要在完成文件上传的同时设置文件访问权限，请参考以下示例代码。
 completeMultipartUploadRequest.setObjectACL(CannedAccessControlList.PublicRead);

// 完成上传。
        CompleteMultipartUploadResult completeMultipartUploadResult = ossClient.completeMultipartUpload(completeMultipartUploadRequest);
     System.out.println(completeMultipartUploadResult.getLocation());
// 关闭OSSClient。
        ossClient.shutdown();
        if (completeMultipartUploadResult.getLocation()!=null)
            return new CommonResult<>(200, "上传成功",completeMultipartUploadResult.getLocation());
        return  new CommonResult<>(100, "上传失败",null);
    }
}
