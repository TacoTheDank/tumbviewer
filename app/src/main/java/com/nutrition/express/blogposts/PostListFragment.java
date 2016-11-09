package com.nutrition.express.blogposts;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nutrition.express.R;
import com.nutrition.express.common.CommonRVAdapter;
import com.nutrition.express.common.CommonViewHolder;
import com.nutrition.express.model.rest.bean.PostsItem;

/**
 * Created by huang on 5/26/16.
 */
public class PostListFragment extends Fragment
        implements CommonRVAdapter.OnLoadListener, PostContract.View {
    private RecyclerView recyclerView;
    private CommonRVAdapter adapter;
    private String blogName;
    private boolean loaded = false;
    private PostPresenter presenter;
    private PostListActivity postListActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Bundle bundle = getArguments();
        blogName = bundle.getString("blog_name");
        postListActivity = (PostListActivity) context;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isResumed() && !loaded) {
            getPostsVideo();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint() && !loaded) {
            getPostsVideo();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = buildAdapter();
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            presenter.onDetach();
        }
    }

    @Override
    public void showData(Object[] items, boolean autoLoadingNext) {
        loaded = true;
        adapter.append(items, autoLoadingNext);
    }

    @Override
    public void onFollowed() {
        postListActivity.onFollow();
    }

    @Override
    public void onFailure(Throwable t) {
        adapter.showLoadingFailure(t.getMessage());
    }

    @Override
    public void onError(int code, String error) {
        adapter.showLoadingFailure(getString(R.string.load_failure_des, code, error));
    }

    @Override
    public void retry() {
        getPostsVideo();
    }

    @Override
    public void loadNextPage() {
        getPostsVideo();
    }

    private void getPostsVideo() {
        if (presenter == null) {
            presenter = new PostPresenter(this);
        }
        presenter.loadData(blogName);
    }

    private CommonRVAdapter buildAdapter() {
        CommonRVAdapter.Builder builder = CommonRVAdapter.newBuilder();
        builder.addItemType(PostsItem.class, R.layout.item_post,
                new CommonRVAdapter.CreateViewHolder() {
                    @Override
                    public CommonViewHolder createVH(View view) {
                        return new PostVH(view);
                    }
                });
        builder.setLoadListener(this);
        return builder.build();
    }

    public void scrollToTop() {
        recyclerView.scrollToPosition(0);
    }

}