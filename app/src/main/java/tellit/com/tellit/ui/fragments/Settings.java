package tellit.com.tellit.ui.fragments;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import tellit.com.tellit.Injector;
import tellit.com.tellit.R;
import tellit.com.tellit.model.UserData;
import tellit.com.tellit.tools.U;
import tellit.com.tellit.ui.activitys.BaseActivity;
import tellit.com.tellit.ui.fragments.autorithation.ProfileFr;

/**
 * Created by probojnik on 26.06.15.
 */
public class Settings extends BaseFragment {
    @InjectView(R.id.content)
    LinearLayout content;
    @InjectView(R.id.version_txt)
    TextView versionTxt;
    @InjectView(R.id.my_phone_txt)
    TextView myPhoneTxt;
    private View view;
    @Inject
    UserData userData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        if (view == null) {
            view = inflater.inflate(R.layout.im_settings, container, false);

            ButterKnife.inject(this, view);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content, ProfileFr.newInstance(true)).commit();
            myPhoneTxt.setText(userData.getLOGIN_PHONE());
            PackageInfo pInfo = null;
            try {
                pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                versionTxt.setText(pInfo.versionName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isVisible()) {
            ((BaseActivity) getActivity()).getSupportActionBar().setTitle("Settings");
            getActivity().invalidateOptionsMenu();
        }
        U.adjustPan(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_empty, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
