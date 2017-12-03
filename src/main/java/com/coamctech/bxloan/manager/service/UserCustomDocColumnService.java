package com.coamctech.bxloan.manager.service;

import com.alibaba.fastjson.JSONReader;
import com.coamctech.bxloan.manager.common.JsonResult;
import com.coamctech.bxloan.manager.common.ResultCode;
import com.coamctech.bxloan.manager.dao.DocColumnDao;
import com.coamctech.bxloan.manager.dao.UserCustomDocColumnDao;
import com.coamctech.bxloan.manager.domain.DocColumn;
import com.coamctech.bxloan.manager.domain.UserCustomDocColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.function.Consumer;

/**
 * Created by Administrator on 2017/11/12.
 */
@Service
@Transactional
public class UserCustomDocColumnService extends BaseService<UserCustomDocColumn,Long>{
    private  final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private UserCustomDocColumnDao userCustomDocColumnDao;
    @Autowired
    private DocColumnService docColumnService;

    /**
     * 根据一级栏目查询某用户已定制的下级栏目id
     * @param userId
     * @param docColumnParentId
     * @return
     */
    public List<Long> getCustomColumnIds(Long userId,Long docColumnParentId){
        List<UserCustomDocColumn> list = userCustomDocColumnDao.findByUserIdAndDocColumnParentId(userId,docColumnParentId);
        List<Long> childColumns = new ArrayList<>();
        list.forEach(userCustomDocColumn->{childColumns.add(userCustomDocColumn.getDocColumnId());});
        return childColumns;
    }
    /**
     * 根据一级栏目查询某用户已定制的下级栏目id
     * @param userId
     * @param docColumnParentId
     * @return
     */
    public List<UserCustomDocColumn> getCustomColumns(Long userId,Long docColumnParentId){
        List<UserCustomDocColumn> list = userCustomDocColumnDao.findByUserIdAndDocColumnParentId(userId,docColumnParentId);
        return list;
    }
    /**
     * 某用户是否已经定制某栏目
     * @param userId
     * @param docColumnId
     * @return
     */
    public boolean ifCustomColumnId(Long userId,Long docColumnId){
        UserCustomDocColumn userCustomDocColumn = userCustomDocColumnDao.findByUserIdAndDocColumnId(userId, docColumnId);
        return userCustomDocColumn!=null;
    }
    /**
     * 订阅某个栏目
     * @param userId
     * @param customColumnId
     * @return
     */
    public JsonResult customColumn(Long userId,Long customColumnId){
        if(customColumnId==null){
            return new JsonResult(ResultCode.PARAM_ERROR_CODE,"该栏目不存在");
        }
        if(ifCustomColumnId(userId,customColumnId)){
            return new JsonResult(ResultCode.ERROR_CODE_701,"该栏目已订阅");
        }
        DocColumn docColumn = docColumnService.findOne(customColumnId);
        if(docColumn==null){
            return new JsonResult(ResultCode.PARAM_ERROR_CODE,"该栏目不存在");
        }

        UserCustomDocColumn userCustomDocColumn = new UserCustomDocColumn();
        userCustomDocColumn.setDocColumnId(customColumnId);
        userCustomDocColumn.setCreateTime(new Date());
        Map<String,Object> params = new HashMap<>();
        params.put("userId",userId);
        List<UserCustomDocColumn> userCustomDocColumns = userCustomDocColumnDao.findByUserId(userId);
        userCustomDocColumn.setCustomOrder(userCustomDocColumns.size()+1);
        userCustomDocColumn.setDocColumnParentId(docColumn.getParentId());
        userCustomDocColumn.setUserId(userId);
        userCustomDocColumnDao.save(userCustomDocColumn);
        return new JsonResult(ResultCode.SUCCESS_CODE,ResultCode.SUCCESS_MSG);
    }
    /**
     * 订阅某个栏目
     * @param userId
     * @param customColumnId
     * @return
     */
    public JsonResult cancelCustomColumn(Long userId,Long customColumnId){
        if(customColumnId==null){
            return new JsonResult(ResultCode.PARAM_ERROR_CODE,"该栏目不存在");
        }
        UserCustomDocColumn delUserCustomDocColumn = userCustomDocColumnDao.findByUserIdAndDocColumnId(userId, customColumnId);
        if(delUserCustomDocColumn==null){
            return new JsonResult(ResultCode.PARAM_ERROR_CODE,"该栏目未订阅");
        }
        int delCustomOrder = delUserCustomDocColumn.getCustomOrder();
        userCustomDocColumnDao.delete(delUserCustomDocColumn);
        //调整所有该客户已订阅栏目的顺序
        List<UserCustomDocColumn> userCustomDocColumns = userCustomDocColumnDao.findByUserId(userId);
        userCustomDocColumns.forEach(userCustomDocColumn->{
            int customOrder = userCustomDocColumn.getCustomOrder();
            if(customOrder > delCustomOrder){
                userCustomDocColumn.setCustomOrder(customOrder-1);
            }
        });
        userCustomDocColumnDao.save(userCustomDocColumns);
        return new JsonResult(ResultCode.SUCCESS_CODE,ResultCode.SUCCESS_MSG);
    }

    /**
     * 调整订阅的栏目的顺序
     * @param userId
     * @param customColumnIdOne
     * @param customColumnIdTwo
     * @return
     */
    public JsonResult switchOrder(Long userId,Long customColumnIdOne,Long customColumnIdTwo){
        if(customColumnIdOne==null ||customColumnIdTwo==null){
            return new JsonResult(ResultCode.PARAM_ERROR_CODE,"该栏目不存在");
        }
        UserCustomDocColumn userCustomDocColumnOne = userCustomDocColumnDao.findByUserIdAndDocColumnId(userId, customColumnIdOne);
        if(userCustomDocColumnOne==null){
            return new JsonResult(ResultCode.PARAM_ERROR_CODE,"该栏目未订阅");
        }
        UserCustomDocColumn userCustomDocColumnTwo = userCustomDocColumnDao.findByUserIdAndDocColumnId(userId, customColumnIdTwo);
        if(userCustomDocColumnTwo==null){
            return new JsonResult(ResultCode.PARAM_ERROR_CODE,"该栏目未订阅");
        }
        Integer customOrderOne = userCustomDocColumnOne.getCustomOrder();
        Integer customOrderTwo = userCustomDocColumnTwo.getCustomOrder();
        userCustomDocColumnOne.setCustomOrder(customOrderTwo);
        userCustomDocColumnTwo.setCustomOrder(customOrderOne);

        userCustomDocColumnDao.save(userCustomDocColumnOne);
        userCustomDocColumnDao.save(userCustomDocColumnTwo);
        return new JsonResult(ResultCode.SUCCESS_CODE,ResultCode.SUCCESS_MSG);
    }
}
