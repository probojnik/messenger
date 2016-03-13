package tellit.com.tellit.model.custom_xmpp.providers.review;


import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import tellit.com.tellit.model.custom_xmpp.requests.review.ReviewIQ;


/**
 * Created by probojnik on 22.06.15.
 *  <iq type="result" id="Myn6E-353" to="+380635716703@ip-172-31-7-92/ffffffff-b8c9-d697-ffff-ffff89ef02cb">
 *      <response xmlns="custom:iq:review"><reviewid>1</reviewid></response>
 *  </iq>
 *
 *  xpp.<a href="#setInput">setInput</a>( new StringReader ( "&lt;foo>Hello World!&lt;/foo>" ) );
 *         int eventType = xpp.getEventType();
 *         while (eventType != XmlPullParser.END_DOCUMENT) {
 *          if(eventType == XmlPullParser.START_DOCUMENT) {
 *              System.out.println("Start document");
 *          } else if(eventType == XmlPullParser.START_TAG) {
 *              System.out.println("Start tag "+xpp.<a href="#getName()">getName()</a>);
 *          } else if(eventType == XmlPullParser.END_TAG) {
 *              System.out.println("End tag "+xpp.getName());
 *          } else if(eventType == XmlPullParser.TEXT) {
 *              System.out.println("Text "+xpp.<a href="#getText()">getText()</a>);
 *          }
 *          eventType = xpp.next();
 *         }
 *         System.out.println("End document");
 */
public class RewiewIQProvider extends IQProvider<ReviewIQ>{
    public static final String ELEMENT = "response";

    @Override
    public ReviewIQ parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {

        ReviewIQ reviewIQ = new ReviewIQ();
        int eventType = parser.getEventType();
        while(eventType!= XmlPullParser.END_DOCUMENT){
             if(eventType == XmlPullParser.START_TAG){
                 if("reviewid".equals(parser.getName())){
                     parser.next();
                     reviewIQ.setReviewid(Long.parseLong(parser.getText()));
                 }
             }
            eventType = parser.next();
            if(eventType == XmlPullParser.END_TAG && ELEMENT.equals(parser.getName()))
                break;

        }

        return reviewIQ;
    }
}
