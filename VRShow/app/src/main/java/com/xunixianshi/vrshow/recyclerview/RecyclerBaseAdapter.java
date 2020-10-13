package com.xunixianshi.vrshow.recyclerview;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;


/**
 * recycler基础adapter
 *@author DuanChunLin
 *@time 2016/10/20 11:36
 */
public abstract class RecyclerBaseAdapter<T,VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_NORMAL = 1;
    private static final int TYPE_END = 2;
    private BindHeadViewHolderListener mBindHeadViewHolderListener;
    private BindEndViewHolderListener mBindEndViewHolderListener;
    protected RecyclerBaseItemOnClickListener mRecyclerBaseItemOnClickListener;
    private List<T> group = new ArrayList<T>();
    protected int mSpanCount = 1;
//    @NonNull
//    private List<RecyclerView> mAttachedRecyclerViewPool = new ArrayList<>();

    public void setHeaderViewInterface(BindHeadViewHolderListener bindHeadViewHolderListener) {
        mBindHeadViewHolderListener = bindHeadViewHolderListener;
        notifyItemInserted(0);
    }

    public void removeHeaderViewInterface(){
        if(mBindHeadViewHolderListener!=null){
            removeItem(0);
            mBindHeadViewHolderListener = null;
        }
    }
    public void setBindEndViewHolderInterface(BindEndViewHolderListener bindEndViewHolderListener) {
        mBindEndViewHolderListener = bindEndViewHolderListener;
        notifyItemInserted(getItemCount()-1);
    }

    public void removeBindEndViewHolderInterface(){
        if(mBindEndViewHolderListener!=null){
            removeItem(getItemCount()-1);
            mBindEndViewHolderListener = null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(mBindHeadViewHolderListener !=null && position == 0 ) return TYPE_HEADER;
        if(mBindEndViewHolderListener != null && position == getItemCount()-1) return TYPE_END;
        return TYPE_NORMAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        if(mBindHeadViewHolderListener != null && viewType == TYPE_HEADER) {
            return mBindHeadViewHolderListener.onCreateHeadViewHolder(parent,viewType);
        }
        if(mBindEndViewHolderListener !=null && viewType == TYPE_END){
            return mBindEndViewHolderListener.onCreateEndViewHolder(parent,viewType);
        }
        return createRecyclerHolder(parent, viewType);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if(getItemViewType(position) == TYPE_HEADER && mBindHeadViewHolderListener != null) {
            mBindHeadViewHolderListener.onBindHeadViewHolder(viewHolder);
            return;
        }
        if(getItemViewType(position) == TYPE_END && mBindEndViewHolderListener !=null ){
             mBindEndViewHolderListener.onBindEndViewHolder(viewHolder);
            return;
        }
        final int pos = getRealPosition(viewHolder);
        final T data = group.get(pos);
        bindRecyclerHolder(viewHolder, pos, data);
    }

    /**
     * 添加表格布局监听 如果是头部布局，返回size表示占一整行
     *@author DuanChunLin
     *@time 2016/10/24 14:08
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
//        mAttachedRecyclerViewPool.add(recyclerView);
        if(manager instanceof GridLayoutManager) {
            GridLayoutManager gridManager = ((GridLayoutManager) manager);
            mSpanCount = gridManager.getSpanCount();
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (getItemViewType(position) == TYPE_HEADER || getItemViewType(position) == TYPE_END)
                            ? mSpanCount : 1;
                }
            });
        }
    }

    @Override
    @UiThread
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
//        mAttachedRecyclerViewPool.remove(recyclerView);
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if(lp != null&& lp instanceof StaggeredGridLayoutManager.LayoutParams
                && holder.getLayoutPosition() == 0) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            p.setFullSpan(true);
        }
    }

    private int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        return mBindHeadViewHolderListener == null ? position : position - 1;
    }

    private int getRealPosition(int position) {
        return mBindHeadViewHolderListener == null ? position : position - 1;
    }

    @Override
    public int getItemCount() {
        int offsetSize =0;
        if(mBindHeadViewHolderListener != null){
            offsetSize = offsetSize +1;
        }
        if(mBindEndViewHolderListener!=null){
            offsetSize = offsetSize +1;
        }
        return group.size() + offsetSize;
    }

    public abstract RecyclerView.ViewHolder createRecyclerHolder(ViewGroup parent, final int viewType);
    public abstract void bindRecyclerHolder(RecyclerView.ViewHolder viewHolder, int RealPosition, T data);

    public T getItem(int position){
        /**防止外部调用时越界*/
        int realPosition = getRealPosition(position);
        if(group==null || realPosition<0 || realPosition>group.size())
            return null;
        return group.get(realPosition);
    }

    public boolean isEmpty() {
        return (group == null) || group.isEmpty();
    }

    public void setGroup(List<T> g) {
        if(group != null){
            group.clear();
        }
        else{
            group = new ArrayList<>();
        }
        group.addAll(g);
        notifyViewDataSetChanged();
    }

    public List<T> getGroup() {
        return group;
    }

    public void addItem(T item){
        group.add(item);
        notifyViewDataSetChanged();
    }

    public void addItemNoNotify(T item){
        group.add(item);
    }

    public void addItemsNoNotify(List<T> items){
        if(items != null){
            group.addAll(items);
        }
    }

    public void addItems(List<T> items){
        if(items != null){
            group.addAll(items);
            notifyViewDataSetChanged();
        }
    }

    private void notifyViewDataSetChanged(){
//        int startPosition = mHeaderView!=null? 1:0;
//        if(startPosition < group.size()){
//            notifyItemRangeChanged(startPosition,group.size());
//        }
        notifyDataSetChanged();
    }

    public void removeItems(List<T> items,boolean notify){
        if(items != null){
            group.removeAll(items);
            if(notify){
                notifyViewDataSetChanged();
            }
        }
    }

    public void removeItem(int position){
        int realPosition = getRealPosition(position);
        group.remove(realPosition);
        notifyItemRemoved(position);
        if(position != group.size()){      // 这个判断的意义就是如果移除的是最后一个
            notifyItemRangeChanged(position, group.size() - position);
        }
    }

    public void clearGroup(boolean notify){
        if(group != null){
            group.clear();
            if(notify){
                notifyViewDataSetChanged();
            }
        }
    }
    public void setRecyclerBaseItemOnClickListener(RecyclerBaseItemOnClickListener recyclerBaseItemOnClickListener){
        this.mRecyclerBaseItemOnClickListener = recyclerBaseItemOnClickListener;
    }
}
