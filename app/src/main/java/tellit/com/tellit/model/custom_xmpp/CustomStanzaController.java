package tellit.com.tellit.model.custom_xmpp;

import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ExceptionCallback;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.IQTypeFilter;
import org.jivesoftware.smack.filter.OrFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaIdFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import javax.inject.Inject;

import tellit.com.tellit.Injector;


/**
 * Created by probojnik on 23.06.15.
 */
public class CustomStanzaController<T extends Stanza,R extends Stanza> {
    @Inject
    AbstractXMPPConnection connection;
    private static CustomStanzaController ourInstance = new CustomStanzaController();

    public static CustomStanzaController getInstance() {
        return ourInstance;
    }

    private CustomStanzaController() {
        Injector.inject(this);
    }

    public void sendStanza(final T _stanza, final CustomStanzaCallback<R> callback){


        try {
            StanzaFilter stanzaFilter = new StanzaIdFilter(_stanza);

            connection.sendStanzaWithResponseCallback(_stanza, stanzaFilter, new StanzaListener() {
                        @Override
                        public void processPacket(Stanza packet) throws SmackException.NotConnectedException {

                            callback.resultOK((R) packet);


                        }
                    }, new ExceptionCallback() {

                        @Override
                        public void processException(Exception exception) {
                            callback.error(exception);

                        }
                    }

            );
        } catch (SmackException.NotConnectedException e) {
            callback.error(e);
        }
    }

    public void sendStanza(T _stanza){
        try {
            connection.sendStanza(_stanza);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }
    public R sendStanzaSerial(final T _stanza) throws Exception {

            final Stanza[] stanza = {null};
            final Exception[] _exception = {null};
        final CountDownLatch lock = new CountDownLatch(1);

            final Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                        try {

                            StanzaFilter stanzaFilter = new StanzaIdFilter(_stanza);
                            connection.sendStanzaWithResponseCallback(_stanza, stanzaFilter, new StanzaListener() {
                                        @Override
                                        public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
                                            stanza[0] = packet;
                                            Log.d("CustomStanzaController", "res OK");
                                            lock.countDown();
                                        }
                                    }, new ExceptionCallback() {

                                        @Override
                                        public void processException(Exception exception) {
                                            _exception[0] = exception;
                                            Log.d("CustomStanzaController", "res ERROR");
                                            lock.countDown();
                                        }
                                    }
                            );

                        } catch (SmackException.NotConnectedException e) {
                            _exception[0] = e;
                            lock.countDown();
                        }


                    }

            });
            thread.start();
        try {
            lock.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (stanza[0] == null) {
                String mess = "CustomStanzaController error!!!";
            Log.e("CustomStanzaController", mess);
                if (_exception[0] != null) {
                    mess = _exception[0].getMessage();
                }
                throw new Exception(mess);
            }


            return (R)stanza[0];

    }


}
