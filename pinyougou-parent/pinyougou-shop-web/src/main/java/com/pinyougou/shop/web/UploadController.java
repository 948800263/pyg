package com.pinyougou.shop.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import entity.Result;
import util.FastDFSClient;

@RestController
@RequestMapping("/")
public class UploadController {
	@Value("${FILE_SERVER_URL}")
	private String FILE_SERVER_URL;
	
	
	@RequestMapping("/upload")
	public Result upload(MultipartFile file){
		try{
			//获取文件的名字或者后缀
			String oldName = file.getOriginalFilename();
			String houzhui = oldName.substring(oldName.lastIndexOf("."));
			
			//创建文件上传对象
			FastDFSClient  fdfsc = new FastDFSClient("classpath:fdfs_client.conf");
			
			
			String res = fdfsc.uploadFile(file.getBytes(), houzhui);
			
			String realPath = FILE_SERVER_URL+res;
			
			return new Result(true, realPath);
		}catch(Exception e){
			return new Result(false,"上传图片失败");
		}
		
		
		
	}
	
}
