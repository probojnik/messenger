package tellit.com.tellit.model.custom_xmpp.providers;


import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tellit.com.tellit.model.custom_xmpp.requests.feedbwck.FeedbackListResp;
import tellit.com.tellit.model.database.HelperFactory;
import tellit.com.tellit.model.review.LikeData;
import tellit.com.tellit.model.review.ReviewData;


/**
 *
 <feedbackList xmlns="custom:iq:feedback">
 <feedback>
 <id>1</id>
 <reviewId>2</reviewId>
 <fromJID>user1</fromJID>
 <vote>1</vote>
 <state>0</state>
 <createDate>1050</createDate>
 <updateDate>12</updateDate>
 </feedback>
 </feedbackList>

 */
public class FeedbackListIQProvider extends IQProvider<FeedbackListResp>{
    public static final String ELEMENT = "feedbackList";

    @Override
    public FeedbackListResp parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
        FeedbackListResp feedbackListResp = new FeedbackListResp();
        List<LikeData> likeDataList = new ArrayList<>();

        String name = null;
        outerloop: while (true) {
            int eventType = parser.next();
            switch (eventType){
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    switch (name){
                        case "feedback":
                            parseFeedback(parser,likeDataList);
                            break ;
                        default:
                            break ;
                    }
                    break ;
                case XmlPullParser.END_TAG:
                    if (parser.getDepth() == initialDepth) {
                        break outerloop;
                    }
                    break;
                default:
                    break;
            }
        }

        feedbackListResp.setLikeDataList(likeDataList);

        return feedbackListResp;
    }

    private void parseFeedback(XmlPullParser parser, List<LikeData> likeDataList) throws IOException, XmlPullParserException {
        final int initialDepth = parser.getDepth();
        String name = null;
        LikeData likeData = new LikeData();
        outerloop: while (true) {
            int eventType = parser.next();
            switch (eventType){
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    switch (name) {
                        case "id":
                            likeData.setId(Integer.parseInt(parser.nextText()));
                            break;
                        case "fromJID":
                            likeData.setFromJID(parser.nextText());
                            break;
                        case "reviewId":
                            ReviewData reviewData = null;
                            try {
                                List<ReviewData> reviewDatas = HelperFactory.getInstans().getDao(ReviewData.class).queryForEq("id", Long.parseLong(parser.nextText()));
                                if (reviewDatas != null && reviewDatas.size() > 0) {
                                    reviewData = reviewDatas.get(0);
                                    likeData.setReviewId(reviewData);
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "vote":
                            likeData.setVote(Integer.parseInt(parser.nextText()));
                            break;
                        case "state":
                            likeData.setState(Integer.parseInt(parser.nextText()));
                            break;
                        case "createDate":
                            likeData.setCreateDate(new Date(Long.parseLong(parser.nextText())));
                            break ;
                        case "updateDate":
                            likeData.setUpdateDate(new Date(Long.parseLong(parser.nextText())));
                            break ;

                        default:
                            break;
                    }

                case XmlPullParser.END_TAG:
                    if (parser.getDepth() == initialDepth) {
                        break outerloop;
                    }
                    break;
                default:
                    break;
            }
        }
        likeDataList.add(likeData);
    }
}
