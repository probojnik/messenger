package tellit.com.tellit.ui.fragments.chat;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.List;

;import javax.inject.Inject;

import tellit.com.tellit.Injector;
import tellit.com.tellit.R;
import tellit.com.tellit.model.CreateDateObject;
import tellit.com.tellit.model.UserData;
import tellit.com.tellit.model.chat.MessageData;
import tellit.com.tellit.model.database.HelperFactory;
import tellit.com.tellit.model.review.LikeData;
import tellit.com.tellit.model.review.ReviewData;
import tellit.com.tellit.modules.VCardModule;

public class ReviewAdapter extends ArrayAdapter {
    private Context mContext;
    private List<CreateDateObject> mMessages;
    boolean like_mode = false;
    @Inject UserData userData;
    @Inject VCardModule vcard;

    public ReviewAdapter(Context context, List<CreateDateObject> messages) {
        super(context, R.layout.im_message_multi_row);
        Injector.inject(this);
        this.mContext = context;
        this.mMessages = messages;
    }

    @Override
    public int getCount() {
        return mMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return mMessages.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CreateDateObject message = mMessages.get(position);//(MessageData) this.getItem(position);

        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.im_message_multi_row, parent, false);
            holder.message = (TextView) convertView.findViewById(R.id.message_text);
            holder.name_like = (TextView) convertView.findViewById(R.id.im_message_row_like_name);
            holder.name_buble = (TextView) convertView.findViewById(R.id.im_message_row_buble_name);
            holder.date = (TextView) convertView.findViewById(R.id.im_message_row_date);
            holder.status = (TextView) convertView.findViewById(R.id.im_message_row_status);
            holder.bubble_lt = (LinearLayout) convertView.findViewById(R.id.im_message_row_buble_lt);
            holder.like_lt = (FrameLayout) convertView.findViewById(R.id.im_message_row_like_lt);
            holder.msg_lt = (LinearLayout) convertView.findViewById(R.id.im_message_row_msg_lt);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        holder.name_like.setText("");
        holder.name_buble.setText("");

        vcard.getNameByJid(message.getFromJID(), new VCardModule.NameCallback() {
            @Override
            public void result(String name) {
                holder.name_like.setText(name);
                holder.name_buble.setText(name);
            }
        });


        if(message instanceof MessageData) {
            holder.like_lt.setVisibility(View.GONE);
            holder.msg_lt.setVisibility(View.VISIBLE);
            MessageData messageData = (MessageData) message;
            holder.message.setText(messageData.getBody());
            holder.date.setText(messageData.getCreateDateSimple());
            holder.status.setText(messageData.getStatus());



            if (like_mode) {
                holder.bubble_lt.setVisibility(View.GONE);
                holder.name_buble.setVisibility(View.GONE);
            } else {
                holder.bubble_lt.setVisibility(View.VISIBLE);
                holder.name_buble.setVisibility(View.VISIBLE);
            }
            holder.status.setVisibility(View.GONE);

            LayoutParams lp = (LayoutParams) holder.bubble_lt.getLayoutParams();


            String jid = messageData.getJid();
            int index = jid.indexOf("@");
            String room_id = jid.substring(0,index);
            String from_jid = messageData.getFromJID();

            try {
                ReviewData reviewData = (ReviewData) HelperFactory.getInstans().getDao(ReviewData.class).queryBuilder().where().eq("id",room_id).queryForFirst();
                LikeData likeData = (LikeData) HelperFactory.getInstans().getDao(LikeData.class).queryBuilder().where().eq("reviewId_id",reviewData).and().eq("fromJID",from_jid).queryForFirst();
                if(reviewData.getToJID().equals(from_jid) || reviewData.getFromJID().equals(from_jid)){
                    holder.bubble_lt.setBackgroundResource(R.drawable.speech_bubble_blue);
                    lp.gravity = Gravity.CENTER_HORIZONTAL;
                }else if(from_jid.equals(userData.getMyJid())){
                    if(likeData!=null){
                        if (likeData.getVote() == 1) {
                            holder.bubble_lt.setBackgroundResource(R.drawable.speech_bubble_green_right);
                            lp.gravity = Gravity.RIGHT;
                        }else if (likeData.getVote() == -1) {
                            holder.bubble_lt.setBackgroundResource(R.drawable.speech_bubble_orange_right);
                            lp.gravity = Gravity.RIGHT;
                        }else{
                            holder.bubble_lt.setBackgroundResource(R.drawable.speech_bubble_gery_right);
                            lp.gravity = Gravity.RIGHT;
                        }

                    }else{
                        holder.bubble_lt.setBackgroundResource(R.drawable.speech_bubble_gery_right);
                        lp.gravity = Gravity.RIGHT;
                    }
                }else{
                    if(likeData!=null){
                        if (likeData.getVote() == 1) {
                            holder.bubble_lt.setBackgroundResource(R.drawable.speech_bubble_green_left);
                            lp.gravity = Gravity.LEFT;
                        }else if (likeData.getVote() == -1) {
                            holder.bubble_lt.setBackgroundResource(R.drawable.speech_bubble_orange_left);
                            lp.gravity = Gravity.LEFT;
                        }else{
                            holder.bubble_lt.setBackgroundResource(R.drawable.speech_bubble_gery_left);
                            lp.gravity = Gravity.LEFT;
                        }

                    }else{
                        holder.bubble_lt.setBackgroundResource(R.drawable.speech_bubble_gery_left);
                        lp.gravity = Gravity.LEFT;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            holder.bubble_lt.setLayoutParams(lp);
            holder.message.setTextColor(userData.getContext().getResources().getColor(R.color.textColor));
            holder.name_buble.setLayoutParams(lp);
        }else if(message instanceof LikeData){
            holder.like_lt.setVisibility(View.VISIBLE);
            holder.msg_lt.setVisibility(View.GONE);
            LikeData likeData = (LikeData) message;
            switch (likeData.getVote() ) {
                case 1:
                    holder.like_lt.setBackgroundColor(Color.parseColor("#ff00c400"));
                    break;
                case -1:
                    holder.like_lt.setBackgroundColor(Color.parseColor("#ffc4693d"));
                    break;
                default:
                    holder.like_lt.setVisibility(View.GONE);
            }

        }


        return convertView;
    }

    private static class ViewHolder {
        TextView message;
        LinearLayout bubble_lt;
        FrameLayout like_lt;
        TextView date, status, name_like, name_buble;
        LinearLayout msg_lt;
    }

    @Override
    public long getItemId(int position) {
        //Unimplemented, because we aren't using Sqlite.
        return ((CreateDateObject) this.getItem(position)).get_id();
    }

//    public void updateItem(int i, MessageData message) {
//        mMessages.get(i).update(message);
//        notifyDataSetChanged();
//
//    }

    public boolean isLike_mode() {
        return like_mode;
    }

    public void setLike_mode(boolean like_mode) {
        this.like_mode = like_mode;
    }
}
