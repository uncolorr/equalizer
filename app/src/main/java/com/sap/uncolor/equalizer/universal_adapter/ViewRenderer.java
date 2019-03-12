package com.sap.uncolor.equalizer.universal_adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public abstract class ViewRenderer <M extends ItemModel, VH extends RecyclerView.ViewHolder>
{
    protected int type;
    protected Context context;
    public ViewRenderer(int type, Context context){
        this.type = type;
        this.context = context;
    }

    public abstract void bindView(@NonNull M model, @NonNull VH holder);

    @NonNull
    public abstract VH createViewHolder(@Nullable ViewGroup parent);

    protected int getType(){
        return type;
    }
}