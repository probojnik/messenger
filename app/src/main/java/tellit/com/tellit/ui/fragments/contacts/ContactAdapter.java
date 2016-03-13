package tellit.com.tellit.ui.fragments.contacts;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.squareup.picasso.Picasso;

import java.util.Collection;
import java.util.List;

import tellit.com.tellit.MyApplication;
import tellit.com.tellit.R;
import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.tools.RoundedTransformationForPicasso;
import tellit.com.tellit.tools.U;
import tellit.com.tellit.tools.log.TraceHelper;
import tellit.com.tellit.ui.activitys.BaseActivity;

/**
 * Created by Stas on 12.08.2015.
 */
public class ContactAdapter extends ArrayAdapter<ContactData> {
    protected MyApplication app;
    protected List<ContactData> contactList;
    LayoutInflater inflater;
    private boolean smallImage; // размер картинки как в лайоуте контактов

    public ContactAdapter(BaseActivity act, List<ContactData> contactList, boolean smallImage) {
        super(act, R.layout.item_contacts, contactList);
        this.app = act.getApp();
        this.contactList = contactList;
        inflater = LayoutInflater.from(act);
        this.smallImage = smallImage;
    }

    @Override
    public void addAll(Collection<? extends ContactData> collection) {
        contactList = (List<ContactData>) collection;
    }

    @Override
    public int getCount() {
        return contactList.size();
    }

    @Override
    public ContactData getItem(int i) {
        return contactList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return contactList.get(i).get_id();
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null)
            view = inflater.inflate(obtainLayoutID(), viewGroup, false);
        return initView(i, view);
    }

    protected View initView(final int i, @NonNull View view){
        ImageView imageView = (ImageView) view.findViewById(R.id.im_contact_item_photo);
        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.im_contact_item_ratingBar);
        TextView name = (TextView) view.findViewById(R.id.im_contact_list_item_fio);
        TextView num_review = (TextView) view.findViewById(R.id.contact_item_num);

        if(smallImage){
            ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
            layoutParams.width = U.dp2Px(50);
            layoutParams.height = U.dp2Px(50);
            imageView.setLayoutParams(layoutParams);
        }

        ratingBar.setRating(contactList.get(i).getRate());

        if (contactList.get(i).getName() != null && contactList.get(i).getName().length() > 0) {
            name.setText(contactList.get(i).getName());
        }else {
            name.setText(contactList.get(i).getJid());
        }

        num_review.setText(""+contactList.get(i).getReviewNumber());

        try {
            String photo_uri = contactList.get(i).getPhoto_uri();
            if (photo_uri != null && photo_uri.length() > 0) {
                Picasso.with(app)
                        .load(Uri.parse(photo_uri))
                        .fit().centerCrop()
                        .transform(new RoundedTransformationForPicasso(500, 0))
                        .into(imageView);
            } else {
                Picasso.with(app)
                        .load(R.mipmap.ic_launcher)
                        .fit().centerCrop()
                        .transform(new RoundedTransformationForPicasso(500, 0))
                        .into(imageView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    @LayoutRes
    protected int obtainLayoutID(){ // for Override
        return R.layout.item_contacts;
    }
}