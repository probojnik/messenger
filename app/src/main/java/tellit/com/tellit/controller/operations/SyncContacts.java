//package tellit.com.tellit.controller.operations;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.database.CursorIndexOutOfBoundsException;
//import android.provider.ContactsContract;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.util.Log;
//
//import com.j256.ormlite.android.AndroidDatabaseResults;
//import com.j256.ormlite.dao.CloseableIterator;
//import com.j256.ormlite.dao.Dao;
//import com.j256.ormlite.stmt.DeleteBuilder;
//import com.j256.ormlite.stmt.PreparedQuery;
//import com.j256.ormlite.stmt.QueryBuilder;
//import com.j256.ormlite.stmt.UpdateBuilder;
//
//import org.jivesoftware.smack.AbstractXMPPConnection;
//import org.jivesoftware.smack.packet.IQ;
//import org.jivesoftware.smack.roster.Roster;
//import org.jivesoftware.smack.roster.RosterEntry;
//
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.regex.Pattern;
//
//import javax.inject.Inject;
//
//import de.greenrobot.event.EventBus;
//import tellit.com.tellit.Injector;
//import tellit.com.tellit.MyApplication;
//import tellit.com.tellit.model.UserData;
//import tellit.com.tellit.model.contacts.ContactComparable;
//import tellit.com.tellit.model.contacts.ContactCompareBean;
//import tellit.com.tellit.model.contacts.ContactData;
//import tellit.com.tellit.model.contacts.ContactJoinerBean;
//import tellit.com.tellit.model.contacts.ContactRosterBean;
//import tellit.com.tellit.model.custom_xmpp.CustomStanzaCallback;
//import tellit.com.tellit.model.custom_xmpp.CustomStanzaController;
//import tellit.com.tellit.model.custom_xmpp.requests.AddContactsToRosterReq;
//import tellit.com.tellit.model.custom_xmpp.requests.rating.RatingAllUserResp;
//import tellit.com.tellit.model.custom_xmpp.requests.rating.RatingAllUsersReq;
//import tellit.com.tellit.model.database.HelperFactory;
//import tellit.com.tellit.model.review.ReviewData;
//import tellit.com.tellit.model.user_creation.LoginPasswAPIFacade;
//import tellit.com.tellit.model.user_creation.PhoneNormaliseResp;
//import tellit.com.tellit.model.user_creation.PhonesNormaliseReq;
//import tellit.com.tellit.modules.VCardModule;
//import tellit.com.tellit.tools.CRUD;
//import tellit.com.tellit.tools.CollectionUtil;
//import tellit.com.tellit.tools.CursorUtil;
//import tellit.com.tellit.tools.DebugUtil;
//import tellit.com.tellit.tools.IOUtil;
//import tellit.com.tellit.tools.MyCursorJoiner;
//import tellit.com.tellit.tools.TextUtil;
//import tellit.com.tellit.tools.log.TraceHelper;
//
//public class SyncContacts extends BaseOperation {
//
//    @Inject
//    UserData userData;
//    @Inject
//    AbstractXMPPConnection connection;
//    @Inject
//    MyApplication myApplication;
//
//    List<String> jids = new ArrayList<>();
//    @Inject
//    VCardModule vCard;
//
//    public SyncContacts(OperationCalback operationCalback) {
//        super(operationCalback);
//        Injector.inject(this);
//    }
//
//    @Override
//    protected Object doInBackground(Object[] params) {
//        Log.d("SyncContacts", "start synk");
//
//        try {
//            execSyncContacts();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    @Nullable
//    private static String getOrganizationName(Context ctx, String contactID){
//        Cursor cursor = null;
//        String result = null;
//        try{
//            cursor = ctx.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
//                    null,
//                    ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?",
//                    new String[]{contactID, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE},
//                    ContactsContract.CommonDataKinds.Phone.NUMBER + " ASC");
//            if( cursor != null && cursor.moveToFirst()) {
//                result = cursor.getString(cursor.getColumnIndex("data1"));
//                TraceHelper.print(result, contactID, cursor);
//            }
//        }finally {
//            IOUtil.closeQuietly(cursor);
//        }
//        return result;
//    }
//
//    private void execSyncContacts() throws SQLException {
//        Cursor contactsContract = null;
//        Set<ContactComparable> androidContacts = new HashSet<>();
//        try {
//            // получает курсор контактов андроида
//            contactsContract = userData.getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                    new String[]{ContactsContract.CommonDataKinds.Phone._ID,
//                            "contact_id",
//                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
//                            ContactsContract.CommonDataKinds.Phone.NUMBER,
//                            ContactsContract.CommonDataKinds.Phone.PHOTO_URI
//                    }, null, null, ContactsContract.CommonDataKinds.Phone.NUMBER + " ASC");
//
//            for (contactsContract.moveToFirst(); !contactsContract.isAfterLast(); contactsContract.moveToNext()) {
//                CursorUtil.printCursorSingle(contactsContract, null);
//
//                int id = contactsContract.getInt(contactsContract.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
//                int contactID = contactsContract.getInt(contactsContract.getColumnIndex("contact_id"));
//                String name = contactsContract.getString(contactsContract.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//                if(TextUtil.isEmpty(name)){
//                    name = getOrganizationName(userData.getContext(), String.valueOf(contactID));
//                }
//                if(TextUtil.isEmpty(name)){
//                    name = contactsContract.getString(contactsContract.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                }
//
//                ContactComparable temp = new ContactComparable(id, name,
//                        contactsContract.getString(contactsContract.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)),
//                        contactsContract.getString(contactsContract.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)));
//
//                // валидирует контакты андроида
//                if (temp.isValid())
//                    androidContacts.add(temp);
//            }
//        } finally {
//            IOUtil.closeQuietly(contactsContract);
//        }
//
//        // нормализация контактов андроида
//        if (CollectionUtil.isEmpty(androidContacts))
//            return;
//
//        PhonesNormaliseReq req = new PhonesNormaliseReq(userData.getLOGIN_CODE() == 0 ? "us" : "ua");
//        req.setPhones(androidContacts);
//
//        PhoneNormaliseResp phoneNormaliseResp = LoginPasswAPIFacade.getInstance().getService().getNormalizPhones(req); // exec. sync request to server
//        TraceHelper.print(phoneNormaliseResp);
//
//        // сопоставление нормализованного списка контактов со списком андроида контактов
//        for (PhoneNormaliseResp.Phone normalizedPhone : phoneNormaliseResp.getPhones()) {
//            for (ContactComparable androidPhone : androidContacts) {
//                if (androidPhone.androidID == normalizedPhone.getId()) {
//                    androidPhone.uuid = normalizedPhone.getUuid();
//                    TraceHelper.print(androidPhone.androidID, androidPhone.uuid, androidPhone.name, androidPhone.number);
//                    break;
//                }
//            }
//        }
//
//        // контакты телита
//        Dao dao = HelperFactory.getInstans().getDao(ContactData.class);
//        QueryBuilder queryBuilder = dao.queryBuilder();
//        queryBuilder.where().ge("id", 0); // контакт был добавлен в базу не при синхронизации
//        queryBuilder.orderBy("number", true);
//        List<ContactData> tellitContacts = queryBuilder.query();
//        TraceHelper.print(tellitContacts);
//
//        List<ContactRosterBean> listOfRoadster = new ArrayList<>();
//
//        // сравнение двух списков <номер, имя, фото> нормализированного и из БД телит
//        for (ContactComparable androidContact : androidContacts) {
//            if (!androidContact.isNormalized())
//                continue;
//            TraceHelper.print("Normalized", androidContact.number);
//
//            boolean newContact = true;
//            for (ContactData tellitContact : tellitContacts) {
//                // поиск до первого совпадения
//                // если контакт есть в андроиде и телит = EDIT -- записываем из андроид в телит
//                if (androidContact.androidID == tellitContact.getId()) {
//                    newContact = false;
//                    tellitContact.setHandled(true);
//
//                    // если ничего не изменилось
//                    boolean isSame = androidContact.getMetaHash().equals(tellitContact.getMetaHash());
//                    TraceHelper.print(isSame ? "same numbers" : "syncDo EDIT", androidContact.name + androidContact.number + androidContact.photoUri,
//                            tellitContact.getName() + tellitContact.getNumber() + tellitContact.getPhoto_uri());
//
//                    if (isSame)
//                        break;
//
//                    // Изменился номер телефона
//                    if (!androidContact.uuid.equals(tellitContact.getUuid())) {
//                        listOfRoadster.add(new ContactRosterBean(tellitContact.getJid(), ContactRosterBean.RosterAction.REMOVE, null));
//                        listOfRoadster.add(new ContactRosterBean(androidContact.getJid(connection.getServiceName()), ContactRosterBean.RosterAction.ADD, androidContact.name));
//                    }
//                    androidContact.tellitID = tellitContact.get_id();
//                    updateDB(androidContact);
//                    break;
//                }
//            }
//
//             если контакт есть только в андроиде = NEW -- добавляем в телит
//            if (newContact) {
//                TraceHelper.print("syncDo NEW", androidContact);
//                listOfRoadster.add(new ContactRosterBean(androidContact.getJid(connection.getServiceName()), ContactRosterBean.RosterAction.ADD, androidContact.name));
//                createDB(androidContact, connection.getServiceName());
//            }
//        }
//        // если контакт есть только в телит = OLD -- удаляем из телит
//        for (ContactData tellitContact : tellitContacts) {
//            if (!tellitContact.isHandled()) {
//                TraceHelper.print("syncDo DELETE", tellitContact);
//                listOfRoadster.add(new ContactRosterBean(tellitContact.getJid(), ContactRosterBean.RosterAction.REMOVE, null));
//                deleteDB(tellitContact);
//            }
//        }
//
//        EventBus.getDefault().post(new SyncContacts.SincOperationResult(false));
//        TraceHelper.print(listOfRoadster);
//
//        if (!CollectionUtil.isEmpty(listOfRoadster)) {
//            /**
//             * удаляем связанные ReviewData
//             */
//            for (ContactRosterBean contact : listOfRoadster) {
//                /**
//                 * Выбираем из списка контакты на удаление, игнорируем контакты на добавление
//                 */
//                if (contact.getAction() == ContactRosterBean.RosterAction.REMOVE) {
//                    DeleteBuilder deleteBuilder = HelperFactory.getInstans().getDao(ReviewData.class).deleteBuilder();
//                    deleteBuilder.where().eq("toJID", contact.getJid());
//                    int delete = deleteBuilder.delete();
//                    System.out.println(contact.getJid() + " delete = " + delete);
//                }
//            }
//        }
//
//        handleRoster(listOfRoadster);
//    }
//
//    @Override
//    protected void onPreExecute() {
//        super.onPreExecute();
//        Injector.inject(this);
//    }
//
//
//    private static int createDB(@NonNull ContactComparable bean, String serviceName) throws SQLException {
//        try {
//            DebugUtil.assertTrue(bean.androidID >= 0, "androidID == " + bean.androidID);
//            ContactData contact = (ContactData) HelperFactory.getInstans().getDao(ContactData.class).queryBuilder().where().eq("jid", bean.uuid + "@" + serviceName).queryForFirst();
//            if (contact == null) {
//                contact = new ContactData(bean.androidID, bean.uuid, bean.name, bean.number, bean.photoUri, serviceName);
//            } else {
//                contact.update(bean.androidID, bean.uuid, bean.name, bean.number, bean.photoUri, serviceName);
//            }
//            Dao.CreateOrUpdateStatus orUpdate = HelperFactory.getInstans().getDao(ContactData.class).createOrUpdate(contact);
//            return orUpdate.getNumLinesChanged() > 0 ? contact.get_id() : -3;
//        } catch (SQLException e) {
//            TraceHelper.print(bean);
//        }
//        return -4;
//    }
//
//    private static int updateDB(@NonNull ContactComparable bean) throws SQLException {
//        UpdateBuilder updateBuilder = HelperFactory.getInstans().getDao(ContactData.class).updateBuilder();
//        updateBuilder.updateColumnValue("id", bean.androidID);
//        updateBuilder.updateColumnValue("uuid", bean.uuid);
//        updateBuilder.updateColumnValue("name", bean.name);
//        updateBuilder.updateColumnValue("number", bean.number);
//        updateBuilder.updateColumnValue("photo_uri", bean.photoUri);
//        updateBuilder.where().eq("_id", bean.tellitID);
//        int result = updateBuilder.update();
//        TraceHelper.print(result, bean);
//        return result;
//    }
//
//    private static int deleteDB(@NonNull ContactData bean) throws SQLException {
//        DeleteBuilder deleteBuilder = HelperFactory.getInstans().getDao(ContactData.class).deleteBuilder();
//        deleteBuilder.where().eq("_id", bean.get_id());
//        return deleteBuilder.delete();
//    }
//
//    /**
//     * Принимает список контактов с типом действия и добавляет/изменяет/удаляет соответствующий контакт из ростера.
//     */
//    private void handleRoster(List<ContactRosterBean> contactsOfRoster) {
//        AddContactsToRosterReq addContactsToRosterReq = new AddContactsToRosterReq(contactsOfRoster);
//        addContactsToRosterReq.setType(IQ.Type.set);
//
//        CustomStanzaController.getInstance().sendStanza(addContactsToRosterReq, new CustomStanzaCallback() {
//            @Override
//            public void resultOK(Object result) {
//                syncWithRoster();
//            }
//
//            @Override
//            public void error(Exception ex) {
//                ex.printStackTrace();
//                syncWithRoster();
//            }
//        });
//    }
//
//    private void syncWithRoster() {
//        Roster roster = Roster.getInstanceFor(connection);
//
//        for (RosterEntry entry : roster.getEntries()) {
//            setIsValid(true, entry.getUser());
//        }
//        uppRateToContacts();
//        if (operationCalback != null)
//            operationCalback.onComplete();
//    }
//
//    private void uppRateToContacts() {
//        Log.d("SincOperation", "uppRate");
//        RatingAllUsersReq ratingAllUsersReq = new RatingAllUsersReq();
//        ratingAllUsersReq.setOwnerRosterJID(userData.getMyJid());
//        CustomStanzaController.getInstance().sendStanza(ratingAllUsersReq, new CustomStanzaCallback<RatingAllUserResp>() {
//            @Override
//            public void resultOK(RatingAllUserResp result) {
//                List<RatingAllUserResp.RatingUser> ratingUserList = result.getRatingUserList();
//                for (final RatingAllUserResp.RatingUser ratingUser : ratingUserList) {
//                    vCard.getContactByJid(ratingUser.getJid(), new VCardModule.ContactDataCallback() {
//                        @Override
//                        public void result(ContactData contactData) {
//                            if (contactData != null) {
//                                contactData.setRate(ratingUser.getRating());
//                                try {
//                                    ContactData.getDao().update(contactData);
//                                } catch (SQLException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void error(Exception ex) {
//
//            }
//        });
//    }
//
//    public boolean setIsValid(boolean bool, String jid) {
//        boolean result = false;
//        try {
//            Dao<ContactData, Integer> contactDao = ContactData.getDao();
//            ContactData contactData = contactDao.queryBuilder().where().eq("jid", jid).queryForFirst();
//            if (contactData != null) {
//                if (!contactData.isValid()) {
//                    contactData.setIsValid(bool);
//                    result = contactDao.update(contactData) > 0;
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }
//
//    public static final class SincOperationResult {
//        boolean result;
//
//        public SincOperationResult(boolean result) {
//            this.result = result;
//        }
//
//        public boolean isResult() {
//            return result;
//        }
//    }
//}
