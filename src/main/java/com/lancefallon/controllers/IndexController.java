package com.lancefallon.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController {

    @ModelAttribute("pageTitle")
    public String pageTitle() {
        return "Home Page";
    }

    @RequestMapping("/")
    public ModelAndView getIndex() {
        return new ModelAndView("index");
    }

}
