package vincent4j.view;

import vincent.view.R;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoadMoreListView extends ListView {

    private static final String TAG = "LoadMoreListView";

    private int mTotalCount = 0;
    private int mLoadedCount = 0;

    private String mFooterNormalMessage = "共%s条数据，当前展示了其中%s条";
    private String mFooterLoadMore = "点击加载更多";

    private final int LOAD_STATUS_NORMAL = 0;
    private final int LOAD_STATUS_LOADING = 1;
    private int mLoadState = -1;

    private LayoutInflater mInflater;

    // footer view
    private View mFooterView;
    private TextView mCountInfoTxt;
    private TextView mLoadMoreTxt;
    private ProgressBar mProgressBar;

    // Listener to process load more items when user reaches the end of the list
    private OnLoadMoreListener mOnLoadMoreListener;

    public LoadMoreListView(Context context) {
        super(context);
        init(context);
    }

    public LoadMoreListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadMoreListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // footer
        mFooterView = mInflater.inflate(R.layout.load_more_footer, this, false);

        mCountInfoTxt = (TextView) mFooterView
                .findViewById(R.id.load_more_count_info);

        mLoadMoreTxt = (TextView) mFooterView
                .findViewById(R.id.load_more_load_more);
        mLoadMoreTxt.setText(mFooterLoadMore);

        mProgressBar = (ProgressBar) mFooterView
                .findViewById(R.id.load_more_progressbar);

        addFooterView(mFooterView);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        updateFooterViews(LOAD_STATUS_NORMAL, true);
    }

    /**
     * Register a callback to be invoked when this list reaches the end (last
     * item be visible)
     * 
     * @param onLoadMoreListener
     *            The callback to run.
     */
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }

//  private void updateFooterViews(int status) {
//  updateFooterViews(status, false);
//}

private void updateFooterViews(int status, boolean isForced) {
  if (isForced || (mLoadState != status)) {
      mLoadState = status;
      updateFooterViews();
  }
}

    private void updateFooterViews() {
        Log.d(TAG, "updateFooterViews(" + mLoadState + ")");

        mLoadedCount = getAdapter().getCount() - 1;

        switch (mLoadState) {
        case LOAD_STATUS_NORMAL:
            String footerNormalMessage = String.format(mFooterNormalMessage,
                    mTotalCount, mLoadedCount);

            mCountInfoTxt.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);

            if (mLoadedCount >= mTotalCount) {
                mCountInfoTxt.setText(footerNormalMessage);
                mLoadMoreTxt.setVisibility(View.GONE);
                mFooterView.setOnClickListener(null);
            } else {
                mCountInfoTxt.setText(footerNormalMessage);
                mLoadMoreTxt.setVisibility(View.VISIBLE);
                mFooterView.setOnClickListener(new FooterOnClickListener());
            }

            break;

        case LOAD_STATUS_LOADING:
            mCountInfoTxt.setVisibility(View.GONE);
            mLoadMoreTxt.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);

            mFooterView.setOnClickListener(null);
            break;

        default:
            break;
        }
    }

    private class FooterOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            updateFooterViews(LOAD_STATUS_LOADING, true);
            onLoadMore();
        }
    }

    public void onLoadMore() {
        Log.d(TAG, "onLoadMore");

        if (mOnLoadMoreListener != null) {
            mOnLoadMoreListener.onLoadMore();
        }
    }

    /**
     * Notify the loading more operation has finished
     */
    public void onLoadMoreComplete() {
        ((BaseAdapter) (((HeaderViewListAdapter) getAdapter())
                .getWrappedAdapter())).notifyDataSetChanged();
        updateFooterViews(LOAD_STATUS_NORMAL, true);
    }

    /**
     * Interface definition for a callback to be invoked when list reaches the
     * last item (the user load more items in the list)
     */
    public interface OnLoadMoreListener {
        /**
         * Called when the list reaches the last item (the last item is visible
         * to the user)
         */
        public void onLoadMore();
    }

    public void setTotalCount(int count) {
        mTotalCount = count;

        if (getAdapter() != null) {
            updateFooterViews();
        }
    }

}
