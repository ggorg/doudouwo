package com.gen.common.util;

import java.util.ArrayList;
import java.util.List;

public  class Page<E> {  
    private int pageNum = 1;  
    private int pageSize = 15;  
    private int startRow = 0;  
    private int endRow = 0;  
    private long total = 0;  
    private int pages =0;  
    private List<E> result = new ArrayList<E>();  

    public Page(int pageNum, int pageSize) {  
        this.pageNum = pageNum;  
        if(pageSize > 200){
        	pageSize = 200;
        }
        this.pageSize = pageSize;  
        this.startRow = pageNum > 0 ? (pageNum - 1) * pageSize : 0;  
        this.endRow = pageSize;  
    }  

    public List<E> getResult() {  
        return result;  
    }  
	public int getNextPageNo() {
		if (pageNum >= pages) {
			return pages==0?1:pages;
		}
		return pageNum + 1;
	}
    public void setResult(List<E> result) {  
        this.result = result;  
    }  

    public int getPages() {  
        return (int)(total%pageSize==0?total/pageSize:total/pageSize+1);  
    }  

    public void setPages(int pages) {  
        this.pages = pages;  
    }  

    public int getEndRow() {  
        return endRow;  
    }  

    public void setEndRow(int endRow) {  
        this.endRow = endRow;  
    }  

    public int getPageNum() {  
        return pageNum;  
    }  

    public void setPageNum(int pageNum) {  
        this.pageNum = pageNum;  
    }  

    public int getPageSize() {  
        return pageSize;  
    }  

    public void setPageSize(int pageSize) {  
        this.pageSize = pageSize;  
    }  

    public int getStartRow() {  
        return startRow;  
    }  

    public void setStartRow(int startRow) {  
        this.startRow = startRow;  
    }  

    public long getTotal() {  
        return total;  
    }  

    public void setTotal(long total) {  
        this.total = total;  
        if(pageSize>0)
        	pages = (int) (total%pageSize==0?total/pageSize:total/pageSize+1);
    }  

      
    public String toString() {  
        return "Page{" +  
                "pageNum=" + pageNum +  
                ", pageSize=" + pageSize +  
                ", startRow=" + startRow +  
                ", endRow=" + endRow +  
                ", total=" + total +  
                ", pages=" + pages +  
                '}';  
    }  
}  
