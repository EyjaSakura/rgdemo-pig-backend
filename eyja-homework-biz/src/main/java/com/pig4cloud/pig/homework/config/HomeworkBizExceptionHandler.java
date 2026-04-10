package com.pig4cloud.pig.homework.config;

import com.pig4cloud.pig.common.core.exception.CheckedException;
import com.pig4cloud.pig.common.core.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 作业督学模块 - 全局异常处理器
 * <p>
 * 优先级高于框架的 GlobalBizExceptionHandler（Order=10000），
 * 专门处理本模块特有的业务异常和数据库约束异常。
 * </p>
 *
 * @author EyjaSakura
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackages = "com.pig4cloud.pig.homework")
public class HomeworkBizExceptionHandler {

	// ======================== 业务异常 ========================

	/**
	 * 受检业务异常（CheckedException）
	 * <p>
	 * 由 Service 层主动抛出，前端友好提示。
	 * </p>
	 */
	@ExceptionHandler(CheckedException.class)
	@ResponseStatus(HttpStatus.OK)
	public R handleCheckedException(CheckedException e) {
		log.warn("业务校验异常: {}", e.getMessage());
		return R.failed(e.getMessage());
	}

	// ======================== 数据库约束异常 ========================

	/**
	 * 唯一键冲突（如 INSERT/UPDATE 时违反唯一索引）
	 */
	@ExceptionHandler(DuplicateKeyException.class)
	@ResponseStatus(HttpStatus.OK)
	public R handleDuplicateKeyException(DuplicateKeyException e) {
		log.warn("数据唯一键冲突: {}", e.getMessage());
		String msg = extractReadableMessage(e.getCause().getMessage());
		return R.failed("数据重复：" + msg);
	}

	/**
	 * 数据完整性违反（包含外键约束、非空约束等）
	 */
	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseStatus(HttpStatus.OK)
	public R handleDataIntegrityViolationException(DataIntegrityViolationException e) {
		log.warn("数据完整性异常: {}", e.getMessage());
		String msg = extractReadableMessage(e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
		return R.failed("数据操作异常：" + msg);
	}

	// ======================== 参数异常 ========================

	/**
	 * 缺少必要请求参数
	 */
	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public R handleMissingParam(MissingServletRequestParameterException e) {
		log.warn("缺少必要请求参数: {}", e.getMessage());
		return R.failed("缺少必要参数：" + e.getParameterName());
	}

	// ======================== 兜底 ========================

	/**
	 * 兜底：其他所有未捕获异常
	 * <p>
	 * 不再泄漏堆栈信息给前端，只返回通用提示，详细日志输出到后台。
	 * </p>
	 */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public R handleException(Exception e) {
		log.error("系统异常: {}", e.getMessage(), e);
		return R.failed("服务器内部错误，请联系管理员");
	}

	// ======================== 工具方法 ========================

	/**
	 * 从 SQL 异常消息中提取可读的中文提示
	 * <p>
	 * 示例输入: "Duplicate entry 'TEST5' for key 'biz_college.uk_college_code'"
	 * 输出: "字段值 'TEST5' 已存在（违反唯一约束 uk_college_code）"
	 * </p>
	 */
	private String extractReadableMessage(String sqlMessage) {
		if (sqlMessage == null) {
			return "未知数据约束冲突";
		}

		// 1. Duplicate entry
		if (sqlMessage.contains("Duplicate entry")) {
			// 提取重复值和约束名
			String value = extractGroup(sqlMessage, "Duplicate entry '(.+?)'");
			String key = extractGroup(sqlMessage, "for key '(.+?)'");
			if (value != null && key != null) {
				String shortKey = key.contains(".") ? key.substring(key.lastIndexOf('.') + 1) : key;
				return "字段值 '" + value + "' 已存在（违反唯一约束 " + shortKey + "）";
			}
		}

		// 2. 外键约束
		if (sqlMessage.contains("foreign key constraint")) {
			String table = extractGroup(sqlMessage, "FOREIGN KEY \\(`(.+?)`\\)");
			if (table != null) {
				return "操作违反外键约束（" + table + "）";
			}
		}

		// 3. 非空约束
		if (sqlMessage.contains("cannot be null")) {
			String field = extractGroup(sqlMessage, "Column '(.+?)' cannot be null");
			if (field != null) {
				return "必填字段 '" + field + "' 不能为空";
			}
		}

		// 兜底：截取前80个字符
		return sqlMessage.length() > 80 ? sqlMessage.substring(0, 80) + "..." : sqlMessage;
	}

	/**
	 * 正则提取第一个捕获组
	 */
	private String extractGroup(String input, String pattern) {
		try {
			java.util.regex.Matcher m = java.util.regex.Pattern.compile(pattern).matcher(input);
			return m.find() ? m.group(1) : null;
		} catch (Exception e) {
			return null;
		}
	}

}
