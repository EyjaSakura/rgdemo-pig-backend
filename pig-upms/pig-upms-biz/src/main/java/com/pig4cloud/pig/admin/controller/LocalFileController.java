
// EyjaSakura：文件上传先保留本地，容器我还没研究后面再研究
// 这个不是底层框架原有的

package com.pig4cloud.pig.admin.controller;

import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.annotation.Inner;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/local-file")
public class LocalFileController {
	// 存放目录
	private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

	@PostMapping("/upload")
	public R<String> upload(@RequestParam("file") MultipartFile file) {
		if (file.isEmpty()) {
			return R.failed("文件不能为空");
		}

		// --- 文件类型校验 ---
		String contentType = file.getContentType();
		// 判断 MIME 类型是否是以 "image/" 开头
		if (contentType == null || !contentType.startsWith("image/")) {
			return R.failed(null,"非法文件类型，只能上传图片！");
		}

		try {
			// 确保本地文件夹存在，不存在就创建
			File dir = new File(UPLOAD_DIR);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			// 获取原本的文件名，并提取后缀 (比如 .jpg)
			String originalFilename = file.getOriginalFilename();
			String suffix = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";

			// 用 UUID 生成一个全新的文件名，防止重名文件互相覆盖
			String newFileName = UUID.randomUUID().toString().replace("-", "") + suffix;

			// 保存到电脑硬盘
			File dest = new File(UPLOAD_DIR + newFileName);
			file.transferTo(dest);

			// 拼装一个可以通过浏览器访问的 网络URL 给前端
			String url = "http://eyjasakura.vip.cpolar.cn/admin/local-file/view/" + newFileName;

			return R.ok(url,"获取链接成功");

		} catch (IOException e) {
			e.printStackTrace();
			return R.failed(null,"文件上传失败: " + e.getMessage());
		}
	}

	/**
	 * 用于在浏览器中查看图片的接口
	 * @Inner(value = false)注解代表这个接口完全暴露，不需要任何 Token
	 */
	@Inner(value = false)
	@GetMapping("/view/{fileName}")
	public void viewFile(@PathVariable String fileName, HttpServletResponse response) {
		try {
			File file = new File(UPLOAD_DIR + fileName);
			if (file.exists()) {
				// 告诉浏览器这返回的是一张图片
				response.setContentType("image/jpeg");
				// 将本地文件以流的形式写入到 HTTP 响应中
				FileInputStream fis = new FileInputStream(file);
				FileCopyUtils.copy(fis, response.getOutputStream());
				fis.close();
			} else {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "图片不存在");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
