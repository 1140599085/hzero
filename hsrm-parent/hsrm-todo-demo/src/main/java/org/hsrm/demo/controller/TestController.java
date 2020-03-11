package org.hsrm.demo.controller;

/**
 * @author 张洪涛
 * @create 2020-03-08 17:26
 */

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/test")
    public String getTest(){
        return "hello";
    }
}
