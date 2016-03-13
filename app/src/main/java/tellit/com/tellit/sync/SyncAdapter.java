/*
 * Copyright 2012-2014 Daniel Serdyukov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tellit.com.tellit.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import javax.inject.Inject;

import tellit.com.tellit.Injector;
import tellit.com.tellit.model.UserData;
import tellit.com.tellit.service.XmppService;

/**

 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private final Context mContext;
    @Inject
    UserData userData;

    public SyncAdapter(Context context) {
        super(context, true);
        mContext = context;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider,
                              SyncResult syncResult) {

        Log.e("Sync   ", "contact!!!!!!!!!");
        Injector.inject(this);
        String autoriz = AccountManager.get(getContext()).getUserData(account,"autoriz");

        if(autoriz!=null && autoriz.length()>0) {
            mContext.startService(new Intent(mContext, XmppService.class).putExtra(XmppService.COMMAND, XmppService.SYNC));
        }
    }


}
