package com.anilaltunkan.security.dto;

public class SearchDTO {

    private String searchKey;

    public SearchDTO() {

    }

    public SearchDTO(String searchKey) {
        this.searchKey = searchKey;
    }

    public String getSearchKey() {
        return searchKey;
    }
    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

}
