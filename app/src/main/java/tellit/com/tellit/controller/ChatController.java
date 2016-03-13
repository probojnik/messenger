package tellit.com.tellit.controller;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.view.View;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.UpdateBuilder;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.ChatStateManager;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.delay.DelayInformationManager;
import org.jivesoftware.smackx.delay.packet.DelayInformation;
import org.jivesoftware.smackx.receipts.DeliveryReceipt;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;


import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import tellit.com.tellit.Injector;
import tellit.com.tellit.MyApplication;
import tellit.com.tellit.R;
import tellit.com.tellit.model.UserData;
import tellit.com.tellit.model.chat.ChatData;
import tellit.com.tellit.model.chat.ChatMessage;
import tellit.com.tellit.model.chat.MessageData;
import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.model.custom_xmpp.messages.LikeNotifycation;
import tellit.com.tellit.model.custom_xmpp.messages.ReadedMessage;
import tellit.com.tellit.model.custom_xmpp.messages.ReviewNotifycation;
import tellit.com.tellit.model.custom_xmpp.messages.VCardNotification;
import tellit.com.tellit.model.database.HelperFactory;
import tellit.com.tellit.modules.VCardModule;
import tellit.com.tellit.ui.activitys.Tellit;

/**
 * Created by probojnik on 6/17/15.
 */
public  class ChatController  {
    @Inject
    AbstractXMPPConnection connection;
    List<ChatData> chatDataList = new LinkedList<>();
    boolean notification = false;
    @Inject
    MyApplication myApplication;


    private static ChatController ourInstance = new ChatController();
    @Inject
    UserData userData;
    @Inject
    VCardModule vCard;

    public static ChatController getInstance() {
        return ourInstance;
    }


    private ChatController() {
        Injector.inject(this);
        try {
            chatDataList = HelperFactory.getInstans().getDao(ChatData.class).queryBuilder().orderBy("last_date", false).where().eq("type","chat").query();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        EventBus.getDefault().unregister(this);
    }

    public void all_viewed(final String jid){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    ChatData chat = (ChatData) HelperFactory.getInstans().getDao(ChatData.class).queryBuilder().where().eq("jid",jid).queryForFirst();
                    if(chat == null) return null;
                    chat.setCount(0);
                    HelperFactory.getInstans().getDao(ChatData.class).update(chat);
                    ChatController.this.chatDataList = HelperFactory.getInstans().getDao(ChatData.class).queryBuilder().orderBy("last_date", false).where().eq("type","chat").query();
                    UpdateBuilder updateBuilder = HelperFactory.getInstans().getDao(MessageData.class).updateBuilder();
                    updateBuilder.updateColumnValue("status","")
                            .where().eq("status", MessageData.MessageStatus.NEW.toString()).and().eq("chat_id",chat);
                    updateBuilder.update();
                    EventBus.getDefault().post(chatDataList);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();



    }

    public void sendState(ChatState state, String jid){
        try {
            final ChatManager chatManager = ChatManager.getInstanceFor(connection);
            final Chat chat = chatManager.createChat(jid);
            ChatStateManager chatStateManager = ChatStateManager.getInstance(connection);
            chatStateManager.setCurrentState(state, chat);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendMessage(String message, String jid){
        ChatData chatData = null;
        try {
            final Dao<ChatData,Integer> chatDao= HelperFactory.getInstans().getDao(ChatData.class);
            Dao<MessageData,Integer> messDao= HelperFactory.getInstans().getDao(MessageData.class);
            List<ChatData> chatDataList = chatDao.queryForEq("jid",jid);
            if (chatDataList != null && chatDataList.size() > 0) {
                chatData = chatDataList.get(0);
            } else {
                chatData = new ChatData();
                chatData.setJid(jid);
                final ChatData finalChatData = chatData;
                vCard.getContactByJid(jid, new VCardModule.ContactDataCallback() {
                    @Override
                    public void result(ContactData contactData) {
                        finalChatData.setName(contactData.getName());
                        finalChatData.setPhoto_uri(contactData.getPhoto_uri());
                        finalChatData.setDate(new Date(System.currentTimeMillis()));
                        try {
                            chatDao.create(finalChatData);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });


            }

            MessageData messageData = new MessageData();
            messageData.setBody(message);
            messageData.setJid(jid);
            messageData.setCreateDate(new Date());
            messageData.setChat(chatData);
            messageData.setMy(true);
            chatData.setLastMessage(message);
            chatData.setLast_date(new Date());
            chatDao.update(chatData);
            this.chatDataList = HelperFactory.getInstans().getDao(ChatData.class).queryBuilder().orderBy("last_date", false).where().eq("type","chat").query();


            EventBus.getDefault().post(chatDataList);



            Message xmpp_message = new Message();
            xmpp_message.setType(Message.Type.chat);
            xmpp_message.setTo(jid);
            xmpp_message.setBody(message);
            DeliveryReceiptRequest.addTo(xmpp_message);
            messageData.setId(xmpp_message.getStanzaId());
            messDao.create(messageData);
            EventBus.getDefault().post(messageData);
            final ChatManager chatManager = ChatManager.getInstanceFor(connection);
            final Chat chat = chatManager.createChat(jid);
            chat.sendMessage(xmpp_message);
            messageData.setStatus(MessageData.MessageStatus.SEND.toString());
            messDao.update(messageData);
            EventBus.getDefault().post(messageData);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public List<ChatData> getChatDataList() {
        try {
            chatDataList = HelperFactory.getInstans().getDao(ChatData.class).queryBuilder().orderBy("last_date", false).where().eq("type","chat").query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chatDataList;
    }
    public  void reciveMessage(final ChatMessage chatMessage) {
        setNotification(false);
        Message message = chatMessage.getMessage();
        Chat chat = chatMessage.getChat();

        ChatData chatData = null;
        String tempJid = chat.getParticipant();
        int index = tempJid.lastIndexOf("/");
        if (index > 0)
            tempJid = tempJid.substring(0, index);
        final String jid = tempJid;
        try {
            String mess_text = message.getBody();
            if (mess_text != null && mess_text.length() > 0) {
                List<ChatData> chatDataList = HelperFactory.getInstans().getDao(ChatData.class).queryForEq("jid", jid);
                if (chatDataList != null && chatDataList.size() > 0) {
                    chatData = chatDataList.get(0);
                } else {
                    chatData = new ChatData();
                    chatData.setJid(jid);
                    chatData.setType(message.getType().toString());
                    final ChatData finalChatData = chatData;

                    vCard.getContactByJid(jid, new VCardModule.ContactDataCallback() {
                        @Override
                        public void result(ContactData contactData) {
                            if (!contactData.isValid()) {
                                vCard.requestUnknownUser(jid, 0, new VCardModule.ContactDataCallback() {
                                    @Override
                                    public void result(ContactData contactData) {
                                        finalChatData.setName(contactData.getNumber());
                                        finalChatData.setPhoto_uri(contactData.getPhoto_uri());
                                        try {
                                            HelperFactory.getInstans().getDao(ChatData.class).create(finalChatData);
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } else {
                                finalChatData.setName(contactData.getName());
                                finalChatData.setPhoto_uri(contactData.getPhoto_uri());
                                try {
                                    HelperFactory.getInstans().getDao(ChatData.class).create(finalChatData);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    });

                }

                MessageData messageData = new MessageData();
                messageData.setJid(jid);
                messageData.setBody(mess_text);
                messageData.setChat(chatData);
                messageData.setId(message.getStanzaId());
                messageData.setCreateDate(new Date());
                messageData.setType(message.getType().toString());
                messageData.setStatus(MessageData.MessageStatus.NEW.toString());
                HelperFactory.getInstans().getDao(MessageData.class).create(messageData);
                EventBus.getDefault().post(messageData);


                if (message.getType() == Message.Type.chat) {
                    setNotification(true);
                    chatData.setLastMessage(mess_text);
                    chatData.setLast_date(new Date());
                    long count = HelperFactory.getInstans().getDao(MessageData.class).queryBuilder().where().eq("jid", jid).and().eq("status", MessageData.MessageStatus.NEW.toString()).countOf();
                    chatData.setCount(count);
                    HelperFactory.getInstans().getDao(ChatData.class).update(chatData);
                    ChatController.this.chatDataList = HelperFactory.getInstans().getDao(ChatData.class).queryBuilder().orderBy("last_date", false).where().eq("type", "chat").query();
                    EventBus.getDefault().post(ChatController.this.chatDataList);

                }

            }


            List<ExtensionElement> elements = message.getExtensions();
            for (ExtensionElement element : elements) {
                if (element instanceof ChatStateExtension) {
                    HashMap<String,ChatState> stateHashMap = new HashMap<String,ChatState>();
                    stateHashMap.put(jid,((ChatStateExtension) element).getChatState());
                    EventBus.getDefault().post(stateHashMap);


                } else if (element instanceof DeliveryReceiptRequest) {
                    Message receiptMessage = DeliveryReceiptManager.receiptMessageFor(message);
                    chat.sendMessage(receiptMessage);

                } else if (element instanceof DeliveryReceipt) {
                    String mess_id = ((DeliveryReceipt) element).getId();
                    List<MessageData> messageDataList = HelperFactory.getInstans().getDao(MessageData.class).queryForEq("id", mess_id);
                    if (messageDataList != null && messageDataList.size() > 0) {
                        MessageData messageData = messageDataList.get(0);
                        if(!MessageData.MessageStatus.READ.name().equals(messageData.getStatus())) {
                            messageData.setStatus(MessageData.MessageStatus.DELIVERED.toString());
                            HelperFactory.getInstans().getDao(MessageData.class).update(messageData);
                            EventBus.getDefault().post(messageData);
                        }

                    }

                } else if (element instanceof DelayInformation) {
                    setNotification(false);
                    if(message.getStanzaId()!= null) {
                        Date date = DelayInformationManager.getDelayTimestamp(message);
                        List<MessageData> messageDatas = HelperFactory.getInstans().getDao(MessageData.class).queryForEq("id", message.getStanzaId());
                        if (messageDatas != null && messageDatas.size() > 0) {
                            MessageData messageData = messageDatas.get(0);
                            messageData.setCreateDate(date);
//                                messageData.setStatus(MessageData.MessageStatus.NEW.toString());
                            HelperFactory.getInstans().getDao(MessageData.class).update(messageData);
                            EventBus.getDefault().post(messageData);


                        }
                    }

                } else if (element instanceof ReadedMessage) {
                    ReadedMessage readedMessage = (ReadedMessage) element;
                    String readedMessageId = readedMessage.getId();
                    List<MessageData> messageDataList = HelperFactory.getInstans().getDao(MessageData.class).queryBuilder().where().eq("id", readedMessageId).query();
                    if (messageDataList != null && messageDataList.size() > 0) {
                        MessageData messageData = messageDataList.get(0);
                        messageData.setStatus(MessageData.MessageStatus.READ.toString());
                        HelperFactory.getInstans().getDao(MessageData.class).update(messageData);
                        EventBus.getDefault().post(messageData);
                    }

                } else if (element instanceof ReviewNotifycation) {
                    ReviewNotifycation reviewListReq = (ReviewNotifycation) element;
                    ReviewController.getInstance().addReview(reviewListReq);
                } else if (element instanceof LikeNotifycation) {
                    LikeNotifycation likeNotifycation = (LikeNotifycation) element;
                    ReviewController.getInstance().addLike(likeNotifycation);
                } else if(element instanceof VCardNotification){
                    VCardNotification vCardNotification = (VCardNotification) element;
                    String change_jid = vCardNotification.getUser()+"@"+connection.getServiceName();
                    ContactData change_contact = (ContactData) HelperFactory.getInstans().getDao(ContactData.class).queryBuilder().where().eq("jid",change_jid).queryForFirst();
                    if(change_contact!=null){
                        if(change_contact.getPhoto_uri() == null || change_contact.getPhoto_uri().length() == 0 || change_contact.getPhoto_uri().startsWith("file") || change_contact.getId() >= 0){
                            vCard.getVcard(change_jid, new VCardModule.ContactDataCallback() {
                                @Override
                                public void result(ContactData contactData) {
                                    EventBus.getDefault().post(contactData);
                                }
                            });
                        }
                    }


                }
            }
            if (message.getType() == Message.Type.chat) {
                if (isNotification()) {
                    userData.playNotificationSound();
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void sendNotification(MessageData messageData){
        Intent notificationIntent = new Intent(myApplication.getBaseContext(), Tellit.class);
        notificationIntent.putExtra("chat",messageData.getJid());
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(myApplication.getBaseContext(), 0,
                notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);





        NotificationCompat.Builder builder = new NotificationCompat.Builder(myApplication.getBaseContext())
                .setContentTitle(messageData.getJid())
                .setContentText(messageData.getBody())
                .setTicker("Incoming message")
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{1000})
                .setLights(Color.MAGENTA, 3000, 3000)
                .setDefaults(Notification.DEFAULT_SOUND).setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher);


        NotificationManager notificationManager = (NotificationManager) myApplication.getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(messageData.getChat().get_id(), builder.build());
    }

    public AbstractXMPPConnection getConnection() {
        return connection;
    }

    public void setConnection(AbstractXMPPConnection connection) {
        this.connection = connection;
    }

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }





}