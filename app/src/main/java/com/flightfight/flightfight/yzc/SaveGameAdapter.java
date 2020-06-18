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
import com.flightfight.flightfight.yankunwei.GameAchieveInfo;
import com.flightfight.flightfight.yankunwei.GameSaveService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


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
        itemSaveGameTime = itemView.findViewById(R.id.item_game_time);
        itemSaveGamePass = itemView.findViewById(R.id.item_game_pass);
        button = itemView.findViewById(R.id.item_game_delete_button);
    }
}

public class SaveGameAdapter extends BaseAdapter {
    private List<GameAchieveInfo> gameInfoList;
    private LayoutInflater layoutInflater;
    private Context context;
    private int currentPos = -1;
    private ViewHolder holder = null;


    public SaveGameAdapter(Context context, List<GameAchieveInfo> gameInfoList){
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
    public GameAchieveInfo getItem(int position) {
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
        SimpleDateFormat f = new SimpleDateFormat("yyyy 年 MM 月 dd 日 HH 点 mm 分 ss 秒");
        DateFormat df1 = DateFormat.getDateInstance(DateFormat.LONG, Locale.CHINA);
       // holder.itemIcon.setImageResource();
        String date = f.format(gameInfoList.get(position).date);

//        String date1 =  date.substring(0, 18);
//        String date2 = date.substring(19);
//         date = date1 +"\n" + date2;
        holder.itemSaveGameTime.setText(date);
       holder.itemSaveGamePass.setText(String.valueOf(position));

        holder.button.setOnClickListener(v -> {

            showExitAlert(position);
        });
        return convertView;
    }

    public void remove(int index){
        gameInfoList.remove(index);
    }

    public void refreshDataSet(){
        notifyDataSetChanged();
    }


    public void showExitAlert(int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("警告").setIcon(R.drawable.ic_launcher_foreground).setMessage("要删除吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent  = new Intent(context, GameSaveService.class);
                                intent.setAction(GameSaveService.SERVICE_ACTION_DELETE_GAME_ACHIEVE);
                                intent.putExtra(GameSaveService.SERVICE_ACTION_DELETE_GAME_ACHIEVE_ARG, getItem(pos).uuid);
                                context.startService(intent);
                                remove(pos);
                               // notifyDataSetChanged();
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
