package tellit.com.tellit.ui.fragments.chat;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import org.jivesoftware.smack.packet.Message;

import java.sql.SQLException;
import java.util.List;

import tellit.com.tellit.R;
import tellit.com.tellit.model.chat.MessageData;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaController;
import tellit.com.tellit.model.custom_xmpp.messages.ReadedMessage;
import tellit.com.tellit.model.database.HelperFactory;


/**
 * AwesomeAdapter is a Custom class to implement custom row in ListView
 * 
 * @author Adil Soomro
 *
 */
public class ChatAdapter extends ArrayAdapter {
	private Context mContext;
	private List<MessageData> mMessages;

	public ChatAdapter(Context context, List<MessageData> messages) {
		super(context,R.layout.im_message_simple_row);
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
		final MessageData message = mMessages.get(position);//(MessageData) this.getItem(position);

		ViewHolder holder; 
		if(convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.im_message_simple_row, parent, false);
			holder.message = (TextView) convertView.findViewById(R.id.message_text);
			holder.date = (TextView) convertView.findViewById(R.id.im_message_row_date);
			holder.status = (TextView) convertView.findViewById(R.id.im_message_row_status);
			holder.bubble_lt = (LinearLayout) convertView.findViewById(R.id.im_message_row_buble_lt);
			convertView.setTag(holder);
		}
		else
			holder = (ViewHolder) convertView.getTag();
		
		holder.message.setText(message.getBody());
		holder.date.setText(message.getCreateDateSimple());
		holder.status.setText(message.getStatus());

//		LayoutParams lp = (LayoutParams) holder.message.getLayoutParams();
		LayoutParams lp = (LayoutParams) holder.bubble_lt.getLayoutParams();
		//check if it is a status message then remove background, and change text color.

			//Check whether message is mine to show green background and align to right
			if(message.isMy())
			{
				holder.bubble_lt.setBackgroundResource(R.drawable.speech_bubble_green_right);
				holder.status.setVisibility(View.VISIBLE);
				lp.gravity = Gravity.RIGHT;
			}
			//If not mine then it is from sender to show orange background and align to left
			else
			{
				if(!MessageData.MessageStatus.READ.name().equals(message.getStatus())){
					ReadedMessage readedMessage = new ReadedMessage();
					readedMessage.setId(message.getId());
					message.setStatus(MessageData.MessageStatus.READ.name());
					try {
						HelperFactory.getInstans().getDao(MessageData.class).update(message);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					Message sendMessage = new Message(message.getJid());
					sendMessage.addExtension(readedMessage);
					sendMessage.setType(Message.Type.chat);
					CustomStanzaController.getInstance().sendStanza(sendMessage);

				}
				holder.bubble_lt.setBackgroundResource(R.drawable.speech_bubble_orange_left);
				holder.status.setVisibility(View.GONE);
				lp.gravity = Gravity.LEFT;
			}
			holder.bubble_lt.setLayoutParams(lp);
			holder.message.setTextColor(mContext.getResources().getColor(R.color.textColor));
//			holder.message.setMovementMethod(LinkMovementMethod.getInstance());

		return convertView;
	}
	private static class ViewHolder
	{
		TextView message;
		LinearLayout bubble_lt;
		TextView date,status;
	}

	@Override
	public long getItemId(int position) {
		//Unimplemented, because we aren't using Sqlite.
		return ((MessageData) this.getItem(position)).get_id();
	}


	public void updateItem(int i, MessageData message){
		mMessages.get(i).update(message);
		notifyDataSetChanged();

	}

}
