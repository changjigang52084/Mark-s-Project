package com.xunixianshi.vrshow.my.assemble.addAssemble;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.obj.assembleObj.AssembleContentItemObj;
import com.xunixianshi.vrshow.recyclerview.RecyclerBaseAdapter;
import com.xunixianshi.vrshow.util.StringUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by duan on 2016/9/24.
 */

public class EditContentAdapter extends RecyclerBaseAdapter<AssembleContentItemObj,EditContentAdapter.EditContentHolder> {

    private Context mContext;

    private int mContentListType = 0;

    public EditContentAdapter(Context context){
        mContext = context;
    }

    public void setContentListType(int type){
        this.mContentListType = type;
    }

    public List<AssembleContentItemObj>  getSameSetAssembleList(List<AssembleContentItemObj> totalItems, List<AssembleContentItemObj> compareItems ){
        List<AssembleContentItemObj> sameSetAssemble = new ArrayList<>();
        for(Iterator<AssembleContentItemObj> allIterator = totalItems.iterator(); allIterator.hasNext();){
            AssembleContentItemObj assembleContentItemObj = allIterator.next();
            for (Iterator<AssembleContentItemObj> removesIterator = compareItems.iterator(); removesIterator.hasNext();){
                AssembleContentItemObj needRemoveItemObj = removesIterator.next();
                if(assembleContentItemObj.getResourceId() == needRemoveItemObj.getResourceId()){
                    sameSetAssemble.add(assembleContentItemObj);
                }
            }
        }
        return sameSetAssemble;
    }

    public AssembleContentItemObj getSameAssemble(List<AssembleContentItemObj> totalItems,AssembleContentItemObj compareItem){
        for(Iterator<AssembleContentItemObj> allIterator = totalItems.iterator(); allIterator.hasNext();){
            AssembleContentItemObj assembleContentItemObj = allIterator.next();
            if(assembleContentItemObj.getResourceId() == compareItem.getResourceId()){
                return assembleContentItemObj;
            }
        }
        return null;
    }


    @Override
    public RecyclerView.ViewHolder createRecyclerHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_edit_assemble_content, parent, false);
        return new EditContentHolder(v);
    }

    @Override
    public void bindRecyclerHolder(RecyclerView.ViewHolder viewHolder, int RealPosition, AssembleContentItemObj data) {
        EditContentHolder editContentHolder = ((EditContentHolder)viewHolder);
        editContentHolder.setContentDate(data);
        if(mContentListType == 0) {
            if(data.isSelect()){
                editContentHolder.setHolderState(EditContentHolder.CHECKED);
            }
            else{
                editContentHolder.setHolderState(EditContentHolder.UNCHECKED);
            }
        }
        else{
            editContentHolder.setHolderState(EditContentHolder.UnBIND);
        }
    }

    public class EditContentHolder extends RecyclerView.ViewHolder{
        private static final int UNCHECKED = 1;
        private static final int CHECKED = 2;
        private static final int UnBIND  = 3;

        @Bind(R.id.edit_assemble_content_title)
        TextView edit_assemble_content_title;
        @Bind(R.id.edit_assemble_choice_iv)
        ImageView edit_assemble_choice_iv;

        private AssembleContentItemObj mContentItemObj;

        private int holderState; //状态 1 未选中 2 选中 3 需绑定
        public EditContentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            edit_assemble_choice_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   if(mRecyclerBaseItemOnClickListener != null){
                       mRecyclerBaseItemOnClickListener.onClick(getAdapterPosition());
                   }
                }
            });
        }

        void setContentDate(AssembleContentItemObj contentItemObj ){
            mContentItemObj = contentItemObj;
            edit_assemble_content_title.setText(StringUtil.isEmpty(contentItemObj.getResourceTitle()) ? "未知内容"+getAdapterPosition(): contentItemObj.getResourceTitle());
        }

        private void attachView(){
            switch (holderState){
                case UNCHECKED:
                    edit_assemble_choice_iv.setImageResource(R.drawable.content_item_add);
                    break;
                case CHECKED:
                    edit_assemble_choice_iv.setImageResource(R.drawable.content_item_select);
                    break;
                case UnBIND:
                    edit_assemble_choice_iv.setImageResource(R.drawable.content_item_remove);
                    break;
            }
        }

        private void setHolderState(int holderState){
            this.holderState = holderState;
            attachView();
        }

        public AssembleContentItemObj getContentItemObj(){
            return mContentItemObj;
        }
    }

}
