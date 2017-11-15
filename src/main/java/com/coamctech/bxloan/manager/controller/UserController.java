package com.coamctech.bxloan.manager.controller;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.coamctech.bxloan.manager.dao.UserDao;
import com.coamctech.bxloan.manager.domain.User;

/**
 * Created by Administrator on 2017/10/20.
 */
@Controller
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserDao userDao;

   /* @RequestMapping("/")
    public Page<User> home(HttpSession session){
        Page<User> page = userDao.findAll(new PageRequest(0, 1));
        logger.info("ddd");
        logger.info("page={}",page);
        System.out.println("ss");
        return page;

    }*/
  
    
    @RequestMapping(value="/userMng")
    public String index(Model model){
    	model.addAttribute("role", "ss");
    	return "/userMng/main";
    }
}