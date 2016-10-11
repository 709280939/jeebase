package com.doubleview.jeebase.support.web;


import com.doubleview.jeebase.support.config.Constant;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 分页支持类
 */
public class Page<T> {

    private static String pageNoParam = "pageNo";//当前页面请求参数
    private static String pageSizeParam = "pageSize";//当前页面大小请求参数
    private static String orderByParam = "orderByParam";//当前页面排序请求参数


    private int pageNo = 1; // 当前页码
    private int pageSize = Integer.valueOf(Constant.getConfig("page.pageSize")); // 页面大小
    private long totalSize;// 总记录数


    private int first;// 首页索引
    private int last;// 尾页索引
    private int prev;// 上一页索引
    private int next;// 下一页索引


    private boolean firstPage;//是否是第一页
    private boolean lastPage;//是否是最后一页

    private int length = 8;// 显示页面长度
    private int slider = 1;// 前后显示页面长度

    private List<T> list = new ArrayList();

    private String orderBy = ""; // 标准查询有效， 实例： updatedate desc, name asc

    private String funcName = "page"; // 设置点击页码调用的js函数名称，默认为page，在一页有多个分页对象时使用。

    private String funcParam = ""; // 函数的附加参数，第三个参数值。

    private String message = ""; // 设置提示消息，显示在“共n条”之后


    /**
     * 默认构造器
     */
    public Page() {
        this(1 , Integer.valueOf(Constant.getConfig("page.pageSize")));
    }

    /**
     *分页构造函数 ，默认第一页
     * @param request
     */
    public Page(HttpServletRequest request){
        this(request,Integer.valueOf(Constant.getConfig("page.pageSize")));
    }

    /**
     * 分页构造函数
     * @param request
     * @param defaultPageSize
     */
    public Page(HttpServletRequest request, int defaultPageSize){
        String pageNo = request.getParameter(pageNoParam);
        String pageSize = request.getParameter(pageSizeParam);
        String orderBy = request.getParameter(orderByParam);
        this.pageNo = StringUtils.isNotBlank(pageNo) ? Integer.parseInt(pageNo) : 1;
        this.pageSize = StringUtils.isNotBlank(pageSize) ? Integer.parseInt(pageSize): defaultPageSize;
        this.orderBy = orderBy;
    }

    /**
     * 构造方法
     * @param pageNo 当前页码
     * @param pageSize 分页大小
     */
    public Page(int pageNo, int pageSize) {
        this(pageNo, pageSize, 0);
    }

    /**
     * 构造方法
     * @param pageNo 当前页码
     * @param pageSize 分页大小
     * @param totalSize 数据条数
     */
    public Page(int pageNo, int pageSize, long totalSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.totalSize = totalSize;
    }


    /**
     * 初始化参数
     */
    public void initialize(){

        //1
        this.first = 1;

        this.last = (int)(totalSize / (this.pageSize < 1 ? 20 : this.pageSize) + first - 1);

        if (this.totalSize % this.pageSize != 0 || this.last == 0) {
            this.last++;
        }

        if (this.last < this.first) {
            this.last = this.first;
        }

        if (this.pageNo <= 1) {
            this.pageNo = this.first;
            this.firstPage=true;
        }

        if (this.pageNo >= this.last) {
            this.pageNo = this.last;
            this.lastPage=true;
        }

        if (this.pageNo < this.last - 1) {
            this.next = this.pageNo + 1;
        } else {
            this.next = this.last;
        }

        if (this.pageNo > 1) {
            this.prev = this.pageNo - 1;
        } else {
            this.prev = this.first;
        }

        //2
        if (this.pageNo < this.first) {// 如果当前页小于首页
            this.pageNo = this.first;
        }

        if (this.pageNo > this.last) {// 如果当前页大于尾页
            this.pageNo = this.last;
        }

    }

    /**
     * 默认输出当前分页标签
     */
    @Override
    public String toString() {
        return null;
    }

    /**
     * 获取分页HTML代码
     * @return
     */
    public String getHtml(){
        return toString();
    }

    /**
     * 获取设置总数
     * @return
     */
    public long getTotalSize(){
        return totalSize;
    }

    /**
     * 设置数据总数
     * @param totalSize
     */
    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
        if (pageSize >= totalSize){
            pageNo = 1;
        }
    }

    /**
     * 获取当前页码
     * @return
     */
    public int getPageNo() {
        return pageNo;
    }

    /**
     * 设置当前页码
     * @param pageNo
     */
    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    /**
     * 获取页面大小
     * @return
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * 设置页面大小，最大为100条
     * @param pageSize
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize <= 0 ? 10 : pageSize > 100 ? 100 : pageSize;
    }

    /**
     * 首页索引
     * @return
     */
    public int getFirst() {
        return first;
    }

    /**
     * 尾页索引
     * @return
     */
    public int getLast() {
        return last;
    }

    /**
     * 获取页面总数
     * @return getLast();
     */
    public int getTotalPage() {
        return getLast();
    }

    /**
     * 是否为第一页
     * @return
     */
    public boolean isFirstPage() {
        return firstPage;
    }

    /**
     * 是否为最后一页
     * @return
     */
    public boolean isLastPage() {
        return lastPage;
    }

    /**
     * 上一页索引值
     * @return
     */
    public int getPrev() {
        if (isFirstPage()) {
            return pageNo;
        } else {
            return pageNo - 1;
        }
    }

    /**
     * 下一页索引值
     * @return
     */
    public int getNext() {
        if (isLastPage()) {
            return pageNo;
        } else {
            return pageNo + 1;
        }
    }

    /**
     * 获取本页数据对象列表
     * @return List<T>
     */
    public List<T> getList() {
        return list;
    }

    /**
     * 设置本页数据对象列表
     * @param list
     */
    public Page<T> setList(List<T> list) {
        this.list = list;
        initialize();
        return this;
    }

    /**
     * 获取查询排序字符串
     * @return
     */
    public String getOrderBy() {
        // SQL过滤，防止注入
        String reg = "(?:')|(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|"
                + "(\\b(select|update|and|or|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)";
        Pattern sqlPattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
        if (sqlPattern.matcher(orderBy).find()) {
            return "";
        }
        return orderBy;
    }

    /**
     * 设置查询排序，标准查询有效， 实例： updatedate desc, name asc
     */
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    /**
     * 获取点击页码调用的js函数名称
     * function ${page.funcName}(pageNo){location="${ctx}/list-${category.id}${urlSuffix}?pageNo="+i;}
     * @return
     */
    public String getFuncName() {
        return funcName;
    }

    /**
     * 设置点击页码调用的js函数名称，默认为page，在一页有多个分页对象时使用。
     * @param funcName 默认为page
     */
    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    /**
     * 获取分页函数的附加参数
     * @return
     */
    public String getFuncParam() {
        return funcParam;
    }

    /**
     * 设置分页函数的附加参数
     * @return
     */
    public void setFuncParam(String funcParam) {
        this.funcParam = funcParam;
    }

    /**
     * 设置提示消息，显示在“共n条”之后
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 是否进行总数统计
     * @return this.count==-1
     */
    public boolean isNotCount() {
        return this.totalSize==-1;
    }

    /**
     * 获取 Hibernate FirstResult
     */
    public int getFirstResult(){
        int firstResult = (getPageNo() - 1) * getPageSize();
        if (firstResult >= getTotalSize()) {
            firstResult = 0;
        }
        return firstResult;
    }

    /**
     * 获取 Hibernate MaxResults
     */
    public int getMaxResults(){
        return getPageSize();
    }
}