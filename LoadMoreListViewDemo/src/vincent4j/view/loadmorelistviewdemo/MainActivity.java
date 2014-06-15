package vincent4j.view.loadmorelistviewdemo;

import java.util.ArrayList;
import java.util.LinkedList;

import vincent4j.view.LoadMoreListView;
import vincent4j.view.LoadMoreListView.OnLoadMoreListener;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;


public class MainActivity extends ListActivity {

    // list with the data to show in the listview
    private LinkedList<String> mListItems;

    private int mTotalCount = 28;
    private int mLoadedCount = 0;
    private int mSingelPageCount = 10;

    private LoadMoreListView mLoadList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mLoadList = (LoadMoreListView) getListView();

        ArrayList<String> initData = genContentList();

        mListItems = new LinkedList<String>();
        mListItems.addAll(initData);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mListItems);

        mLoadList.setTotalCount(mTotalCount);

        setListAdapter(adapter);
        mLoadedCount = initData.size();

        // set a listener to be invoked when the list reaches the end
        ((LoadMoreListView) getListView())
                .setOnLoadMoreListener(new OnLoadMoreListener() {
                    public void onLoadMore() {
                        // Do the work to load more items at the end of list
                        // here
                        // new LoadDataTask().execute();
                        ArrayList<String> loadMoreData = genContentList();
                        mListItems.addAll(loadMoreData);
                        mLoadedCount += loadMoreData.size();

                        // Call onLoadMoreComplete when the LoadMore task, has
                        // finished
                        ((LoadMoreListView) getListView()).onLoadMoreComplete();
                    }
                });
    }

    private ArrayList<String> genContentList() {
        ArrayList<String> ret = new ArrayList<String>(mSingelPageCount);

        for (int i = 1; i <= 10; i++) {
            if (i + mLoadedCount > mTotalCount) {
                break;
            }

            ret.add("ListItem" + (i + mLoadedCount));
        }

        Log.d("4J", "genContentList(): " + ret.toString() + ", mLoadedCount: "
                + mLoadedCount);
        return ret;
    }
}
