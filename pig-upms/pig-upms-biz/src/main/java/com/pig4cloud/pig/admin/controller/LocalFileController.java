
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
	private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/file/";

	/**
	 * 通用文件查看/下载接口
	 * 访问路径示例：
	 * 头像图片：/local-file/file/avatar/xxx.jpg
	 * 作业附件：/local-file/file/homework_attachment/yyy.pdf
	 */
	@Inner(value = false)
	@GetMapping("/file/{module}/{fileName}")
	public void viewOrDownloadFile(@PathVariable String module, @PathVariable String fileName, HttpServletResponse response) {

		// 安全防御：白名单校验，防止“目录穿透攻击”
		// 只允许访问我们指定的子文件夹，乱传路径直接拒绝
		if (!"avatar".equals(module) && !"homework_attachment".equals(module)) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403 没权限
			return;
		}

		try {
			// 拼装真实物理路径
			File file = new File(UPLOAD_DIR + module + File.separator + fileName);
			if (!file.exists()) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "文件不存在");
				return;
			}

			// 文件类型判断
			String lowerName = fileName.toLowerCase();
			if (lowerName.endsWith(".png") || lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) {
				response.setContentType("image/jpeg");
			} else if (lowerName.endsWith(".pdf")) {
				response.setContentType("application/pdf");
			} else {
				// 不认识的文件兜底走二进制流
				response.setContentType("application/octet-stream");
			}

			// inline：能预览的就直接在网页打开，不能预览的浏览器会自动下载
			response.setHeader("Content-Disposition", "inline; filename=" + fileName);

			// 输出文件流
			FileInputStream fis = new FileInputStream(file);
			FileCopyUtils.copy(fis, response.getOutputStream());
			fis.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 上传头像
	@PostMapping("/upload/avatar")
	public R<String> upload(@RequestParam("file") MultipartFile file) {
		if (file.isEmpty()) {
			return R.failed("文件不能为空");
		}

		// 文件类型校验
		String contentType = file.getContentType();
		// 判断 MIME 类型是否是以 "image/" 开头
		if (contentType == null || !contentType.startsWith("image/")) {
			return R.failed(null,"非法文件类型，只能上传图片！");
		}

		try {
			// 建一个专属的子文件夹
			String avatarDir = UPLOAD_DIR + "avatar/";
			// 确保本地文件夹存在，不存在就创建
			File dir = new File(avatarDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			// 获取原本的文件名，并提取后缀 (无法处理则设为.jpg)
			String originalFilename = file.getOriginalFilename();
			String suffix = originalFilename != null && originalFilename.contains(".")
					? originalFilename.substring(originalFilename.lastIndexOf("."))
					: ".jpg";

			// 用 UUID 生成一个全新的文件名，防止重名文件互相覆盖
			String newFileName = UUID.randomUUID().toString().replace("-", "") + suffix;

			// 保存到电脑硬盘
			File dest = new File(avatarDir + newFileName);
			file.transferTo(dest);

			// 拼装一个可以通过浏览器访问的 网络URL 给前端
			String url = "http://eyjasakura.vip.cpolar.cn/admin/local-file/file/avatar/" + newFileName;

			return R.ok(url,"获取链接成功");

		} catch (IOException e) {
			e.printStackTrace();
			return R.failed(null,"文件上传失败: " + e.getMessage());
		}
	}

	// 上传作业附件
	@PostMapping("/upload/attachment")
	public R<String> uploadAttachment(@RequestParam("file") MultipartFile file) {
		if (file.isEmpty()) {
			return R.failed("文件不能为空");
		}

		try {
			// 给作业附件建一个专属的子文件夹，和头像隔离开
			String attachmentDir = UPLOAD_DIR + "homework_attachment/";
			File dir = new File(attachmentDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			// 获取后缀
			String originalFilename = file.getOriginalFilename();
			String suffix = originalFilename != null && originalFilename.contains(".")
					? originalFilename.substring(originalFilename.lastIndexOf("."))
					: "";

			// 生成 UUID 文件名
			String newFileName = UUID.randomUUID().toString().replace("-", "") + suffix;

			// 存入硬盘
			File dest = new File(attachmentDir + newFileName);
			file.transferTo(dest);

			// 返回专用的下载链接
			String url = "http://eyjasakura.vip.cpolar.cn/admin/local-file/file/homework_attachment/" + newFileName;

			return R.ok(url, "附件上传成功");

		} catch (IOException e) {
			e.printStackTrace();
			return R.failed(null, "文件上传失败: " + e.getMessage());
		}
	}

}
