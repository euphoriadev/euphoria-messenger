package ru.euphoria.messenger;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ru.euphoria.messenger.adapter.AudiosAdapter;
import ru.euphoria.messenger.adapter.BaseAdapter;
import ru.euphoria.messenger.adapter.DocsAdapter;
import ru.euphoria.messenger.adapter.LinksAdapter;
import ru.euphoria.messenger.adapter.PhotosAdapter;
import ru.euphoria.messenger.api.VKApi;
import ru.euphoria.messenger.api.model.VKAttachments;
import ru.euphoria.messenger.api.model.VKAudio;
import ru.euphoria.messenger.api.model.VKDoc;
import ru.euphoria.messenger.api.model.VKLink;
import ru.euphoria.messenger.api.model.VKModel;
import ru.euphoria.messenger.api.model.VKPhoto;
import ru.euphoria.messenger.util.AndroidUtils;
import ru.euphoria.messenger.view.SpacesItemDecoration;

/**
 * Created by Igor on 24.03.17.
 */
public class DialogAttachmentsFragment extends Fragment {
    private static final int TAB_IMAGES = 0;
    private static final int TAB_VIDEOS = 1;
    private static final int TAB_AUDIOS = 2;
    private static final int TAB_DOCS = 3;
    private static final int TAB_LINKS = 4;

    private int position;
    private long peerId;

    private RecyclerView recycler;
    private RecyclerView.LayoutManager layoutManager;
    private BaseAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        peerId = getArguments().getLong("peer_id", -1);
        position = getArguments().getInt("position", -1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recycler_view, container, false);
        recycler = (RecyclerView) rootView;

        recycler = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        recycler.setHasFixedSize(true);

        getAttachments();;
        return rootView;
    }

    public static DialogAttachmentsFragment newInstance(int pos, long id) {
        Bundle args = new Bundle();
        args.putInt("position", pos);
        args.putLong("peer_id", id);

        DialogAttachmentsFragment fragment = new DialogAttachmentsFragment();
        fragment.setArguments(args);
        return fragment;
    }


    private void getAttachments() {
        String type = "";
        switch (position) {
            case TAB_IMAGES: type = VKAttachments.TYPE_PHOTO; break;
            case TAB_AUDIOS: type = VKAttachments.TYPE_AUDIO; break;
            case TAB_LINKS: type = VKAttachments.TYPE_LINK; break;
            case TAB_DOCS: type = VKAttachments.TYPE_DOC; break;
        }

        if (TextUtils.isEmpty(type)) {
            return;
        }

        VKApi.messages().getHistoryAttachments()
                .mediaType(type)
                .peerId(peerId)
                .count(200)
                .execute(VKModel.class, new VKApi.OnResponseListener<VKModel>() {
                    @Override
                    public void onSuccess(ArrayList<VKModel> attachments) {
                        if (!isAdded()) {
                            return;
                        }
                        switch (position) {
                            case TAB_IMAGES:
                                layoutManager = new GridLayoutManager(getActivity(), 3);
                                recycler.addItemDecoration(new SpacesItemDecoration(Math.round(AndroidUtils.px(1)), 3));

                                ArrayList<VKPhoto> photos = AndroidUtils.unsafeCast(attachments);
                                adapter = new PhotosAdapter(getActivity(), photos);
                                break;

                            case TAB_AUDIOS:
                                layoutManager = new LinearLayoutManager(getActivity());
                                recycler.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

                                ArrayList<VKAudio> audios = AndroidUtils.unsafeCast(attachments);
                                adapter = new AudiosAdapter(getActivity(), audios);
                                break;

                            case TAB_DOCS:
                                layoutManager = new LinearLayoutManager(getActivity());
                                recycler.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

                                ArrayList<VKDoc> docs = AndroidUtils.unsafeCast(attachments);
                                adapter = new DocsAdapter(getActivity(), docs);
                                break;

                            case TAB_LINKS:
                                layoutManager = new LinearLayoutManager(getActivity());
                                recycler.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

                                ArrayList<VKLink> links = AndroidUtils.unsafeCast(attachments);
                                adapter = new LinksAdapter(getActivity(), links);
                                break;

                        }

                        recycler.setLayoutManager(layoutManager);
                        recycler.setAdapter(adapter);
                    }

                    @Override
                    public void onError(Exception ex) {

                    }
                });
    }
}
