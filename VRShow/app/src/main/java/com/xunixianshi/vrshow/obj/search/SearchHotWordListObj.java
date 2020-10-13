package com.xunixianshi.vrshow.obj.search;

/**
 * User: Mark.Chang(Mark.Chang@3glasses.com)
 * Date: 2016-10-17
 * Time: 14:47
 * FIXME
 */
public class SearchHotWordListObj {

    private String hotKeyId;
    private String keyWord;
    private int searchTotal;
    private String lastSearchTime;

    public String getHotKeyId() {
        return hotKeyId;
    }

    public void setHotKeyId(String hotKeyId) {
        this.hotKeyId = hotKeyId;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public int getSearchTotal() {
        return searchTotal;
    }

    public void setSearchTotal(int searchTotal) {
        this.searchTotal = searchTotal;
    }

    public String getLastSearchTime() {
        return lastSearchTime;
    }

    public void setLastSearchTime(String lastSearchTime) {
        this.lastSearchTime = lastSearchTime;
    }
}