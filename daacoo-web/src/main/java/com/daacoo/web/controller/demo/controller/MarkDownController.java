package com.daacoo.web.controller.demo.controller;

import com.daacoo.common.core.controller.BaseController;
import com.daacoo.common.utils.WebUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Description MarkDown控制层
 * @Author daacoo
 * @Date 2021/6/21
 * @Version 1.0
 **/
@Controller
@RequestMapping("/demo/markdown")
public class MarkDownController extends BaseController {
    private String prefix = "/demo/markdown";

    @RequestMapping("/index")
    public String importIndex() {
        return prefix + "/markdownIndex";
    }

    @RequestMapping("/markdownEdit")
    public String markdownEdit() {
        return prefix + "/markdownEdit";
    }

    @RequestMapping("/publish")
    public String publish(HttpServletRequest request, Model model) {
        Map map = WebUtil.paramsToMap(request.getParameterMap());
        model.addAttribute("content",map.get("content"));
        return prefix+"/markdownIndex";
    }

}
