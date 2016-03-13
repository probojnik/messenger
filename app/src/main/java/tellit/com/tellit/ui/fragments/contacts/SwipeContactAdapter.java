package tellit.com.tellit.ui.fragments.contacts;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;

import java.util.List;

import tellit.com.tellit.R;
import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.tools.log.TraceHelper;
import tellit.com.tellit.ui.activitys.BaseActivity;
import tellit.com.tellit.ui.fragments.contacts.ContactAdapter;

/**
 * Created by Stas on 12.08.2015.
 * ContactAdapter with Swipe functionality.
 */
public class SwipeContactAdapter extends ContactAdapter {
    public SwipeContactAdapter(BaseActivity act, List<ContactData> contactList) {
        super(act, contactList, false);
    }

    @LayoutRes
    protected int obtainLayoutID(){
        return R.layout.im_swipe_item;
    }

    @Override
    protected View initView(final int i, @NonNull View view) {
        view = super.initView(i, view);

        ImageView favor_img = (ImageView) view.findViewById(R.id.im_swipe_item_favor);
        ImageView star_img = (ImageView) view.findViewById(R.id.im_swipe_item_star);

        ((SwipeLayout) view).setClickToClose(true);

        favor_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(app, "" + contactList.get(i).getJid() + " favor", Toast.LENGTH_SHORT).show();
            }
        });
        star_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(app, "" + contactList.get(i).getJid() + " star", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
