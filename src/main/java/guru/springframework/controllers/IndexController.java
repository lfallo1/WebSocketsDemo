package guru.springframework.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class IndexController {

    @ModelAttribute("pageTitle")
    public String pageTitle() {
        return "Home Page";
    }

    @RequestMapping("/")
    public ModelAndView getIndex(@RequestParam("category") Optional<Integer> category, Model model) {
        return new ModelAndView("index");
    }

}
