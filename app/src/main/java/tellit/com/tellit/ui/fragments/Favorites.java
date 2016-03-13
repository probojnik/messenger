package tellit.com.tellit.ui.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.squareup.picasso.Picasso;

import java.sql.SQLException;
import java.util.List;

import de.greenrobot.event.EventBus;
import tellit.com.tellit.R;

import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.model.database.HelperFactory;
import tellit.com.tellit.tools.RoundedTransformationForPicasso;
import tellit.com.tellit.ui.activitys.BaseActivity;
import tellit.com.tellit.ui.fragments.contacts.ContactDetail;

/**
 * Created by probojnik on 15.06.15.
 */
public class Favorites extends BaseFragment {

    private LayoutInflater inflater;
    private List<ContactData> contactList;
    private FavoriteAdapter contactAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.inflater = inflater;
        EventBus.getDefault().register(this);
        View rootView = inflater.inflate(R.layout.im_favor_list,container,false);
        ListView listView = (ListView) rootView.findViewById(R.id.im_favorite_list);
        contactAdapter = new FavoriteAdapter(getActivity());

        listView.setAdapter(contactAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ContactDetail contactDetail = new ContactDetail();
                Bundle bundle = new Bundle();
                bundle.putSerializable("params", contactAdapter.getItem(i));
                contactDetail.setArguments(bundle);
                ((BaseContainerFragment) getParentFragment()).replaceFragment(contactDetail, true);
            }
        });
       update();

        return rootView;
    }

    public void update(){
        try {
            contactList = HelperFactory.getInstans().getDao(ContactData.class).queryBuilder().orderBy("name", true).where().eq("favorite",true).query();
            contactAdapter.clear();
            contactAdapter.addAll(contactList);
            contactAdapter.notifyDataSetChanged();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void onEventMainThread(ContactData contactData) {
        update();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isVisible()) {
            ((BaseActivity) getActivity()).getSupportActionBar().setTitle("Favorites");
            getActivity().supportInvalidateOptionsMenu();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    class FavoriteAdapter extends ArrayAdapter<ContactData> {
//        List<ContactData> contactList;
        public FavoriteAdapter(Context context) {
            super(context, R.layout.im_contact_list_item);
//            this.contactList = contactList;
        }



        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {

            if(view == null) view = inflater.inflate(R.layout.im_swipe_item, viewGroup,false);
            ((SwipeLayout)view).setClickToClose(true);
            ImageView imageView = (ImageView) view.findViewById(R.id.im_contact_item_photo);
            ImageView favor_img = (ImageView) view.findViewById(R.id.im_swipe_item_favor);
            ImageView star_img = (ImageView) view.findViewById(R.id.im_swipe_item_star);
            RatingBar ratingBar = (RatingBar) view.findViewById(R.id.im_contact_item_ratingBar);
            TextView rev_num = (TextView) view.findViewById(R.id.contact_item_num);

            ratingBar.setRating(getItem(i).getRate());
            rev_num.setText(""+getItem(i).getReviewNumber());
            favor_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "" + contactList.get(i).getJid() + " favor", Toast.LENGTH_SHORT).show();
                }
            });
            star_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(),""+contactList.get(i).getJid()+" star",Toast.LENGTH_SHORT).show();
                }
            });
            TextView name = (TextView) view.findViewById(R.id.im_contact_list_item_fio);
            if(contactList.get(i).getName()!=null && contactList.get(i).getName().length()>0)
                name.setText(contactList.get(i).getName());
            else
                name.setText(contactList.get(i).getJid());

            try {
                String photo_uri = contactList.get(i).getPhoto_uri();
//                Log.i("photo","name"+contactList.get(i).getName()+" "+"photo - "+photo_uri);
                if(photo_uri != null && photo_uri.length()>0) {


                    Picasso.with(getActivity())
                            .load(Uri.parse(photo_uri))
                            .fit().centerCrop()
                            .transform(new RoundedTransformationForPicasso(500, 0))
                            .into(imageView);
                }else{
                    Picasso.with(getActivity())
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
    }
}
