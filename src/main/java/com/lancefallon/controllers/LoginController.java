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
    public ResponseEntity<Authentication> getUser(Authentication auth) {
        return new ResponseEntity<>(auth, HttpStatus.OK);
    }

    /**
     * most communication in app takes place over websockets. there is no specification in websockets
     * to keep http sessions alive, so the front is tracking this manually and keeping sessions alive periodically
     * based on websocket activity
     *
     * @param auth
     * @return
     */
    @ResponseBody
    @RequestMapping("/api/user/keepalive")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> keepSessionAlive(Authentication auth) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
