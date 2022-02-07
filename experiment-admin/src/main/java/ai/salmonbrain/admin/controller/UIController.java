package ai.salmonbrain.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UIController {

    @RequestMapping({"/", "/ui/**"})
    public String index() {
        return "forward:/index.html";
    }
}
