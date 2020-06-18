package com.flightfight.flightfight.yzc;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.flightfight.flightfight.MainActivity;
import com.flightfight.flightfight.R;

import java.util.Date;
import java.util.List;


class ViewHolder{
    public ImageView itemIcon;
    public TextView itemSaveGameTime;
    public TextView itemSaveGamePass;
    public Button button;
    public int defaultTextColor;

    View itemView;
    public ViewHolder(View itemView){
        if(itemView == null){
            throw new IllegalArgumentException("itemView can not be null");
        }
        this.itemView = itemView;

    }
}

public class SaveGameAdapter extends BaseAdapter {
    private List<Date> gameInfoList;
    private LayoutInflater layoutInflater;
    private Context context;
    private int currentPos = -1;
    private ViewHolder holder = null;


    public SaveGameAdapter(Context context, List<Date> gameInfoList){
        this.gameInfoList = gameInfoList;
        this.context = context;

        layoutInflater = LayoutInflater.from(context);
    }

    public void setFocisitemPos(int pos){
        currentPos = pos;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return gameInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return  gameInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return  position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.layout_loadgame_list, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }

       // holder.itemIcon.setImageResource();
        holder.itemSaveGameTime.setText(position);
       holder.itemSaveGamePass.setText((int) gameInfoList.get(position).getTime());

        holder.button.setOnClickListener(v -> {
            showExitAlert();
        });
        return convertView;
    }

    public void remove(int index){
        gameInfoList.remove(index);
    }

    public void refreshDataSet(){
        notifyDataSetChanged();
    }


    public void showExitAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("警告").setIcon(R.drawable.ic_launcher_foreground).setMessage("要删除吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }
                ).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
