package com.seveniu.web;

import com.seveniu.user.UserManager;
import com.seveniu.util.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by seveniu on 6/6/16.
 * ConsumerApi
 */
@Controller
@RequestMapping("/api/consumer")
public class ConsumerRest {
    private Logger logger = LoggerFactory.getLogger(ConsumerRest.class);

    private final UserManager userManager;

    @Autowired
    public ConsumerRest(UserManager userManager) {
        this.userManager = userManager;
    }

    @RequestMapping(value = "/reg", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public ResponseEntity<String> reg(String name, String host) {
        try {
            String result = userManager.reg(name, host);
            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            logger.warn("reg consumer failed : error : {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @RequestMapping(value = "/running-task", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public ResponseEntity<String> addTask(String uuid) {
        try {
            return ResponseEntity.ok(Json.toJson(userManager.getRunningTasks(uuid)));
        } catch (Exception e) {
            logger.warn("reg consumer failed : error : {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @RequestMapping(value = "/resource-info", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public ResponseEntity<String> getResourceInfo(String uuid) {
        try {
            return ResponseEntity.ok(Json.toJson(userManager.getResourceInfo(uuid)));
        } catch (Exception e) {
            logger.warn("reg consumer failed : error : {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @RequestMapping(value = "/task-summary", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public ResponseEntity<String> getTaskSummary(String uuid) {
        try {
            return ResponseEntity.ok(Json.toJson(userManager.getTaskSummary(uuid)));
        } catch (Exception e) {
            logger.warn("get consumer task summary failed : error : {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
