package com.flightfight.flightfight.yzc;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.flightfight.flightfight.R;
import com.flightfight.flightfight.yankunwei.GameAchieveInfo;
import com.flightfight.flightfight.yankunwei.GameSaveService;
import com.flightfight.flightfight.yankunwei.database.bean.PlayerRecord;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;


class ScoreViewHolder {

    public TextView itemScoreGameTime;
    public TextView itemSCoreGamePass;
    public TextView itemScoreGame;

    public int defaultTextColor;

    View itemView;
    public ScoreViewHolder(View itemView){
        if(itemView == null){
            throw new IllegalArgumentException("itemView can not be null");
        }
        this.itemView = itemView;
        itemScoreGameTime = itemView.findViewById(R.id.item_game_score_time);
        itemSCoreGamePass = itemView.findViewById(R.id.item_game_scroe_pass);
        itemScoreGame = itemView.findViewById(R.id.item_game_name_score);
    }
}

public class GameScoreAdapter extends BaseAdapter {
    private List<PlayerRecord> playerRecordList;
    private LayoutInflater layoutInflater;
    private Context context;
    private int currentPos = -1;
    private ScoreViewHolder holder = null;


    public GameScoreAdapter(Context context, List<PlayerRecord> playerRecordList){
        this.playerRecordList = playerRecordList;
        this.context = context;

        layoutInflater = LayoutInflater.from(context);
    }

    public void setFocisitemPos(int pos){
        currentPos = pos;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return playerRecordList.size();
    }

    @Override
    public PlayerRecord getItem(int position) {
        return  playerRecordList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return  position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.layout_score_list, null);
            holder = new ScoreViewHolder(convertView);
            convertView.setTag(holder);
        }
        else {
            holder = (ScoreViewHolder)convertView.getTag();
        }
        SimpleDateFormat f = new SimpleDateFormat("yyyy 年 MM 月 dd 日 HH 点 mm 分 ss 秒");
      //  DateFormat df1 = DateFormat.getDateInstance(DateFormat.LONG, Locale.CHINA);
       // holder.itemIcon.setImageResource();
        String date = f.format(playerRecordList.get(position).getDate());

//        String date1 =  date.substring(0, 18);
//        String date2 = date.substring(19);
//         date = date1 +"\n" + date2;

        holder.itemScoreGameTime.setText(date);
       holder.itemSCoreGamePass.setText(String.valueOf(position+1) );
        holder.itemScoreGame.setText("姓名:"+getItem(position).getPlayerName()+"分数:"+getItem(position).getScore());
        return convertView;
    }

    public void remove(int index){
        playerRecordList.remove(index);
    }

    public void refreshDataSet(){
        notifyDataSetChanged();
    }


}
