package com.lancefallon.controllers;

import com.lancefallon.command.LoginCommand;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {

	@RequestMapping("/login")
	public String showLoginForm(Model model) {

		model.addAttribute("loginCommand", new LoginCommand());

		return "loginform";
	}
	
	@RequestMapping("/logout-success")
	public String logout() {
		return "logoutsuccess";
	}

	@ResponseBody
	@RequestMapping("/api/user")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Authentication> getUser(Authentication auth){
		return new ResponseEntity<>(auth, HttpStatus.OK);
	}

}
