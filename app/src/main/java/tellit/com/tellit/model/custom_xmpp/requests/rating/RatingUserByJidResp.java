package tellit.com.tellit.model.custom_xmpp.requests.rating;

import org.jivesoftware.smack.packet.IQ;

/**
 * Created by probojnik on 02.07.15.
 *
 <user xmlns="custom:iq:rating">
 <rating>1</rating>
 </user>



 */
public class RatingUserByJidResp extends IQ {
    public static final String ELEMENT = "user",NAMESPACE = "custom:iq:rating";
    float rating;
    int reviewNumber;

    public RatingUserByJidResp() {
        super(ELEMENT, NAMESPACE);
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {

        return null;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getReviewNumber() {
        return reviewNumber;
    }

    public void setReviewNumber(int reviewNumber) {
        this.reviewNumber = reviewNumber;
    }
}
