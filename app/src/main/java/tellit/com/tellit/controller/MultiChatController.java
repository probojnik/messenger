package tellit.com.tellit.controller;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.delay.packet.DelayInformation;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;

import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import tellit.com.tellit.Injector;
import tellit.com.tellit.model.UserData;
import tellit.com.tellit.model.chat.ChatData;
import tellit.com.tellit.model.chat.MessageData;
import tellit.com.tellit.model.custom_xmpp.messages.ReviewMessage;
import tellit.com.tellit.model.database.HelperFactory;
import tellit.com.tellit.model.review.LikeData;
import tellit.com.tellit.model.review.ReviewData;

/**
 * Created by probojnik on 29.06.15.
 */
public class MultiChatController {
    @Inject
    AbstractXMPPConnection connection;
    MultiUserChatManager manager;
    private static MultiChatController ourInstance = new MultiChatController();
    @Inject
    UserData userData;

    ChatData chatData;

    public static MultiChatController getInstance() {
        return ourInstance;
    }

    private MultiChatController() {
        Injector.inject(this);
    }

    public String getJidByID(String reviewData ){
        connection = ChatController.getInstance().getConnection();
        if(connection!=null && connection.isConnected())
            return reviewData+"@conference."+connection.getServiceName();
//            return "test@conference."+connection.getServiceName();

        return null;
    }

    public void createRoom(final ReviewData reviewData) throws XMPPException.XMPPErrorException, SmackException {
        connection = ChatController.getInstance().getConnection();
        if(connection!=null && connection.isConnected()) {

            manager = MultiUserChatManager.getInstanceFor(connection);



            final String jid = getJidByID("" + reviewData.getId());
            try {

                chatData = (ChatData) HelperFactory.getInstans().getDao(ChatData.class).queryBuilder().where().eq("jid",jid).queryForFirst();
                if(chatData == null) {
                    chatData = new ChatData();
                    chatData.setType(Message.Type.groupchat.toString());
                    chatData.setJid(jid);
                    HelperFactory.getInstans().getDao(ChatData.class).create(chatData);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            MultiUserChat muc = manager.getMultiUserChat(jid);
            if (muc.isJoined()) return;




                final ChatData finalChatData = chatData;

                muc.addMessageListener(new MessageListener() {
                    @Override
                    public void processMessage(Message message) {
                        if (message.getBody() != null && message.getBody().length() > 0) {

                            final MessageData messageData = new MessageData();
                            messageData.setBody(message.getBody());
                            int index = message.getFrom().indexOf("/");
                            String fromUID = message.getFrom().substring(index+1);
                            StringBuilder fromJid  = new StringBuilder(fromUID);
                            fromJid.append("@");
                            fromJid.append(connection.getServiceName());
                            messageData.setFromJid(fromJid.toString());
                            messageData.setChat(finalChatData);
                            messageData.setJid(jid);
                            messageData.setCreateDate(new Date());
                            messageData.setId(message.getStanzaId());
                            for (ExtensionElement element : message.getExtensions()) {
                                if (element instanceof ReviewMessage) {
                                    ReviewMessage reviewMessage = (ReviewMessage) element;
                                    messageData.setId(reviewMessage.getUuid());
                                    messageData.setLike(reviewMessage.getVote());

                                } else if (element instanceof DelayInformation) {
                                    DelayInformation delayInformation = (DelayInformation) element;
                                    messageData.setCreateDate(delayInformation.getStamp());
                                }
                            }

//                            try {
//                                HelperFactory.getInstans().getDao(MessageData.class).create(messageData);
                                EventBus.getDefault().post(messageData);
//                            } catch (SQLException e) {
//                                e.printStackTrace();
//                            }
                        }

                    }

                });

                if (!muc.isJoined()) {

                    DiscussionHistory discussionHistory = new DiscussionHistory();
                    discussionHistory.setMaxStanzas(20);
                        boolean b = muc.createOrJoin(userData.getMyLogin(),null,discussionHistory,connection.getPacketReplyTimeout());

                    if(b) {
                        Form form = new Form(DataForm.Type.submit);
                        FormField f1 = new FormField("muc#roomconfig_roomname");
                        f1.setType(FormField.Type.text_single);
                        f1.addValue(""+reviewData.getId());
                        FormField f2 = new FormField("muc#roomconfig_roomdesc");
                        f2.setType(FormField.Type.text_single);
                        f2.addValue("Chat about review - "+reviewData.getId());
                        FormField f3 = new FormField("muc#roomconfig_roomowners");
                        f3.setType(FormField.Type.jid_multi);
                        f3.addValue(userData.getMyJid());
                        form.addField(f1);
                        form.addField(f2);
                        form.addField(f3);
                        muc.sendConfigurationForm(form);
                        muc.join(userData.getMyLogin());
                    }
                }
        }

    }
    public void sendMessage(ReviewData reviewData,String mess) throws Exception {
        createRoom(reviewData);
        String jid =getJidByID("" + reviewData.getId());
        manager = MultiUserChatManager.getInstanceFor(connection);
        MultiUserChat muc = manager.getMultiUserChat(jid);
        try {

            ;

            Message message = muc.createMessage();
            message.setBody(mess);
            final MessageData messageData = new MessageData();
            messageData.setBody(mess);
            messageData.setChat(chatData);
            messageData.setJid(jid);
            messageData.setCreateDate(new Date());
            messageData.setMy(true);
            messageData.setFromJid(userData.getMyJid());
            messageData.setId(UUID.randomUUID().toString());


            LikeData myLike = reviewData.getMyLike();
            int vote=0;
            if(myLike != null)
                vote = myLike.getVote();
            messageData.setLike(vote);

            EventBus.getDefault().post(messageData);


            ReviewMessage reviewMessage = new ReviewMessage(messageData.getId(),vote);
            message.addExtension(reviewMessage);
            muc.sendMessage(message);

        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    public void leave(ReviewData reviewData) {
        try {
            String jid = getJidByID("" + reviewData.getId());
            MultiUserChat muc = manager.getMultiUserChat(jid);

            muc.leave();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
