/**
 * Copyright (C) 2014 Open Whisper Systems
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.thoughtcrime.securesms;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


import org.thoughtcrime.securesms.color.MaterialColor;
import org.thoughtcrime.securesms.database.loaders.ConversationListLoader;
import org.thoughtcrime.securesms.recipients.Recipient;
import org.thoughtcrime.securesms.recipients.Recipients;
import org.thoughtcrime.securesms.crypto.MasterSecret;

import java.util.HashMap;
import java.util.Map;

/**
 * A fragment to select and share to open conversations
 *
 * @author Jake McGinty
 */
public class ShareFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    private ConversationSelectedListener listener;
    private MasterSecret masterSecret;
    private Button mShareButton;
    private Recipients recipients;
    private Map<Recipient, Boolean> forwards;
    private int counter = 0;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        masterSecret = getArguments().getParcelable("master_secret");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.share_fragment, container, false);
        mShareButton = (Button) view.findViewById(R.id.forward_messages);

        mShareButton.setEnabled(false);
        mShareButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        initializeListAdapter();
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.listener = (ConversationSelectedListener) activity;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (v instanceof ShareListItem) {
            ShareListItem headerView = (ShareListItem) v;
            Recipient recipient = headerView.getRecipients().getPrimaryRecipient();
            if (forwards == null) {
                forwards = new HashMap<>();
                forwards.put(recipient, Boolean.TRUE);
                recipients = headerView.getRecipients();
                headerView.setBackgroundColor(MaterialColor.LIGHT_BLUE.toConversationColor(getContext()));
                counter++;
            } else {
                if (forwards.keySet().contains(recipient) && forwards.get(recipient)) {
                    forwards.put(recipient, Boolean.FALSE);
                    headerView.setBackgroundColor(Color.TRANSPARENT);
                    counter--;
                } else {
                    forwards.put(recipient, Boolean.TRUE);
                    headerView.setBackgroundColor(MaterialColor.LIGHT_BLUE.toConversationColor(getContext()));
                    counter++;
                }
            }
            if(counter==0){
                mShareButton.setEnabled(false);
            }else {
                mShareButton.setEnabled(true);
            }
        }
    }

    private void initializeListAdapter() {
        this.setListAdapter(new ShareListAdapter(getActivity(), null, masterSecret));
        getListView().setRecyclerListener((ShareListAdapter) getListAdapter());
        getLoaderManager().restartLoader(0, null, this);
    }

    private void handleCreateConversation(long threadId, Recipients recipients, int distributionType) {
        listener.onCreateConversation(threadId, recipients, distributionType);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        return new ConversationListLoader(getActivity(), null, false);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
        ((CursorAdapter) getListAdapter()).changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        ((CursorAdapter) getListAdapter()).changeCursor(null);
    }

    @Override
    public void onClick(View v) {
        for (Recipient recipient : forwards.keySet()) {
            if (forwards.get(recipient)) {
                if (!recipients.getRecipientsList().contains(recipient)) {
                    recipients.getRecipientsList().add(recipient);
                }
            } else {
                recipients.getRecipientsList().remove(recipient);
            }
        }
        handleCreateConversation(-1, recipients, 0);
    }


    public interface ConversationSelectedListener {
        public void onCreateConversation(long threadId, Recipients recipients, int distributionType);
    }
}
