package com.coamctech.bxloan.manager.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户浏览历史
 * Created by Administrator on 2017/11/11.
 */
@Entity
public class UserViewHistory implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long userId;//用户ID

    //智库报告 新闻动态 专题跟踪 相关
    private Long docInfoId;//文章ID

    //数据中心相关
    private String conceptUri;//数据概念
    private String entityid;//实体ID
    private Date updateTime;//浏览时间
    private Date createTime;//创建时间，第一次浏览时间

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getDocInfoId() {
        return docInfoId;
    }

    public void setDocInfoId(Long docInfoId) {
        this.docInfoId = docInfoId;
    }

    public String getConceptUri() {
        return conceptUri;
    }

    public void setConceptUri(String conceptUri) {
        this.conceptUri = conceptUri;
    }

    public String getEntityid() {
        return entityid;
    }

    public void setEntityid(String entityid) {
        this.entityid = entityid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}
