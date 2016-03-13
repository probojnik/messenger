package tellit.com.tellit.model.custom_xmpp.requests;

import org.jivesoftware.smack.packet.IQ;

/**
 * Created by probojnik on 07.07.15.
 * 	.append("<query xmlns='custom:iq:gcs'>")
 .append("<token>")
 .append("fUy9RjinAsg:APA91bEPwT9LehoBqEe-pnw5fJ6qh8W--r-UgwiNwwQD2C6gcfyivpVXing3SmXRPDsBO0oFkzVy0ZZwfYWTBtsukbBXXN-_wMI8uVpJakYq8ZxJMwxy4jBO5RyItHxsryz6PctghBG3")
 .append("</token>")
 .append("</query>")
 */
public class GCMReq extends IQ {
    public static final String ELEMENT = "query",NAMESPACE = "custom:iq:gcs";
    private String token;

    public GCMReq(String token) {
        super(ELEMENT, NAMESPACE);
        this.token = token;
        setType(Type.set);

    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.append(">");
        xml.append("<token>" + token + "</token>");
        xml.append("<id>A</id>");
        return xml;
    }
}
