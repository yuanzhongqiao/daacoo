package com.daacoo.cms.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 文章实体类
 * @author daacoo
 * @date 2019/12/18
 */
public class Article implements Serializable {
    private static final long serialVersionUID = -1395263694069887623L;
    /**
     * 主键id
     **/
    private String id;
    /**
     * 栏目编号
     **/
    private String columnId;
    /**
     * 栏目名称
     **/
    private String columnName;
    /**
     * 栏目标识
     **/
    private String columnFlag;
    /**
     * 标题
     **/
    private String title;
    /**
     * 文章链接
     **/
    private String link;
    /**
     * 文章内容
     **/
    private String content;
    /**
     * 标题颜色
     **/
    private String titleColor;
    /**
     * 文章图片路径
     **/
    private String imageUrl;
    /**
     * 文章图片名称
     **/
    private String imageName;
    /**
     * 关键字
     **/
    private String keywords;
    /**
     * 描述、摘要
     **/
    private String description;
    /**
     * 权重，越大越靠前
     **/
    private String weight;
    /**
     * 权重期限
     **/
    private Date weightTime;
    /**
     * 点击数
     **/
    private String hits;
    /**
     * 推荐位，多选
     **/
    private String posId;
    /**
     * 0.显示、1.隐藏
     **/
    private String status;
    /**
     * 创建人
     **/
    private String createBy;
    /**
     * 创建时间
     **/
    private Date createTime;
    /**
     * 更新人
     **/
    private String updateBy;
    /**
     * 更新时间
     **/
    private Date updateTime;
    /**
     * 备注
     **/
    private String remark;
    /**
     * 排序
     **/
    private String sort;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnFlag() {
        return columnFlag;
    }

    public void setColumnFlag(String columnFlag) {
        this.columnFlag = columnFlag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(String titleColor) {
        this.titleColor = titleColor;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public Date getWeightTime() {
        return weightTime;
    }

    public void setWeightTime(Date weightTime) {
        this.weightTime = weightTime;
    }

    public String getHits() {
        return hits;
    }

    public void setHits(String hits) {
        this.hits = hits;
    }

    public String getPosId() {
        return posId;
    }

    public void setPosId(String posId) {
        this.posId = posId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
