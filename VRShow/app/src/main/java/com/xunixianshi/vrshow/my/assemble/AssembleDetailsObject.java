package com.xunixianshi.vrshow.my.assemble;

import com.xunixianshi.vrshow.obj.assembleObj.AssembleContentItemObj;
import com.xunixianshi.vrshow.obj.assembleObj.AssembleDetailObj;

import java.io.Serializable;
import java.util.List;

/**
 * Created by duan on 2016/10/12.
 */

public class AssembleDetailsObject implements Serializable {

    int mTotalPage;
    private List<AssembleContentItemObj> mContentList;
    private AssembleDetailObj mAssembleDetailObj;

    public List<AssembleContentItemObj> getContentList() {
        return mContentList;
    }

    public void setContentList(List<AssembleContentItemObj> mContentList) {
        this.mContentList = mContentList;
    }

    public AssembleDetailObj getAssembleDetailObj() {
        return mAssembleDetailObj;
    }

    public void setAssembleDetailObj(AssembleDetailObj mAssembleDetailObj) {
        this.mAssembleDetailObj = mAssembleDetailObj;
    }

    public int getTotalPage() {
        return mTotalPage;
    }

    public void setTotalPage(int mTotalPage) {
        this.mTotalPage = mTotalPage;
    }
}
