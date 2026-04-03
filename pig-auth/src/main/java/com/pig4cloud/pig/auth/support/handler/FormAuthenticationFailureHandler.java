/*
 * Copyright (c) 2020 pig4cloud Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pig4cloud.pig.auth.support.handler;

import com.pig4cloud.pig.common.core.util.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * 表单登录失败处理逻辑
 *
 * @author lengleng
 * @date 2025/05/30
 */
@Slf4j
public class FormAuthenticationFailureHandler implements AuthenticationFailureHandler {

	/**
	 * 当认证失败时调用
	 * @param request 认证尝试发生的请求
	 * @param response 响应对象
	 * @param exception 拒绝认证时抛出的异常
	 */
	@Override
	@SneakyThrows
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) {
		log.debug("表单登录失败:{}", exception.getLocalizedMessage());

		// 获取当前请求的context-path
		String contextPath = request.getContextPath();

		// 使用 Java 原生的 URLEncoder 替代 Hutool，避免依赖报错
		String errorMsg = URLEncoder.encode(exception.getMessage(), "UTF-8");
		String url = String.format("%s/token/login?error=%s", contextPath, errorMsg);

		try {
			WebUtils.getResponse().sendRedirect(url);
		}
		catch (IOException e) {
			log.error("重定向失败", e);
		}
	}

}
