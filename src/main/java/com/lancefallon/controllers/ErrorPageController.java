package com.lancefallon.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.lancefallon.services.ErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorPageController implements ErrorController {

	@Autowired
	private ErrorService errorService;

	@RequestMapping("/error")
	public String redirectNonExistentUrlsToHome(HttpServletResponse response, Model model) throws IOException {
		model.addAttribute("error", this.errorService.getErrorByStatus(response.getStatus()));
		return "error";
	}

	@Override
	public String getErrorPath() {
		return "/error";
	}
}