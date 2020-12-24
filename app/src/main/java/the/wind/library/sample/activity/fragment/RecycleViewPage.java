package the.wind.library.sample.activity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import the.wind.library.CWHandler;
import the.wind.library.sample.R;
import the.wind.library.utils.CWImageUtils;
import the.wind.library.utils.CWMathUtils;
import the.wind.library.utils.CWStringUtils;
import the.wind.library.view.Checkbox;
import the.wind.library.view.WindRecycleView;

public class RecycleViewPage extends Fragment {

    public final int[] BACKGROUNDS = new int[]{
            R.drawable.wl_button_background_gray,
            R.drawable.wl_button_background_highlight,
            R.drawable.wl_button_background_info,
            R.drawable.wl_button_background_danger,
            R.drawable.wl_button_background_neutral,
            R.drawable.wl_button_background_primary,
            R.drawable.wl_button_background_warning,
    };

    public final String PARAGRAPH = "She closed her eyes,sinking down to her knees. " +
            "The memories washed over her like waves as she felt them come back. " +
            "It was only a time ago when it all happened. It was all coming back now, one enormous wave. " +
            "That one army coming after them. They all had charged towards them. There had been too many of them. " +
            "Indeed they had been outnumbered in battle. But she could'nt give up. She fought on as her comrades fell. " +
            "She pushed forward as they fought to stop her. Nothing could stop her. " +
            "Not even the large gashes on her sides. All she focused on was the final goal. " +
            "To destroy the shard and return hope to her kingdom.";
    public final int MAX_WORDS = 24;
    private List<String> BagOfWords = CWStringUtils.text2words(PARAGRAPH);

    private SwipeRefreshLayout _swipeRefreshLayout;
    private WindRecycleView _recycleView;
    private CustomAdapter mAdapter;
    private List<DummyData> mDataset = new LinkedList<>();

    public RecycleViewPage() {
        initDataset();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycle_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // refresh layout
        _swipeRefreshLayout = view.findViewById(R.id._swipeRefreshLayout);
        _swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.clearData();
                initDataset();
                mAdapter.setData(mDataset);
                _swipeRefreshLayout.setRefreshing(false);
            }
        });

        _recycleView = view.findViewById(R.id._recycleView);
        mAdapter = new CustomAdapter(mDataset);
        _recycleView.setAdapter(mAdapter);

        // loadmore listener
        _recycleView.setOnLoadMoreListener(new WindRecycleView.OnLoadMoreListener() {
            @Override
            public void onLoadMore(final CWHandler<Void> handler) {
                _recycleView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        handler.onBefore();
                        loadmoreData();
                        handler.onAfter();
                    }
                }, 2000);
            }
        });

        // enable swap position
        // _recycleView.enableSwapPosition(null);

        // enable swipe to remove
        _recycleView.setLeftSwipeIcon(CWImageUtils.drawbleToBitmap(view.getContext(), R.drawable.wl_ic_setting));
        _recycleView.setRightSwipeIcon(CWImageUtils.drawbleToBitmap(view.getContext(), R.drawable.wl_ic_trash));
        _recycleView.setSwipeIconSize(view.getResources().getDimension(R.dimen.wl_icon));
        _recycleView.enableSwipeToRemove(new WindRecycleView.OnSwipeToRemoveListener() {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                mAdapter.removeData(viewHolder.getAdapterPosition());
            }
        });

        // item click listener
        mAdapter.setOnItemClickListener(new WindRecycleView.Adapter.OnItemClickListener<DummyData>() {
            @Override
            public void onClick(WindRecycleView.ViewHolder viewHolder, View view, DummyData data) {
                Toast.makeText(getContext(), "Clicked! - " + ((CustomViewHolder) viewHolder).TextView.getText(), Toast.LENGTH_SHORT).show();
            }
        }).setOnItemLongClickListener(new WindRecycleView.Adapter.OnItemLongClickListener<DummyData>() {
            @Override
            public void onLongClick(WindRecycleView.ViewHolder viewHolder, View view, DummyData data) {
                Toast.makeText(getContext(), "Long Clicked - " + data.text, Toast.LENGTH_SHORT).show();
            }
        }).setOnItemDoubleClickListener(new WindRecycleView.Adapter.OnItemDoubleClickListener<DummyData>() {
            @Override
            public void onDoubleClick(WindRecycleView.ViewHolder viewHolder, View view, DummyData data) {
                Toast.makeText(getContext(), "Double Clicked - " + data.text, Toast.LENGTH_SHORT).show();
            }
        });
        mAdapter.setOnItemTouchDownListener(new WindRecycleView.Adapter.OnItemTouchDownListener<DummyData>() {
            @Override
            public void onTouchDown(WindRecycleView.ViewHolder<DummyData> viewHolder, View view, DummyData data) {
                viewHolder.itemView.setBackgroundResource(R.drawable.wl_button_background_success);
            }
        }).setOnItemTouchUpListener(new WindRecycleView.Adapter.OnItemTouchUpListener<DummyData>() {
            @Override
            public void onTouchUp(WindRecycleView.ViewHolder<DummyData> viewHolder, View view, DummyData data) {
                viewHolder.itemView.setBackgroundResource(data.background);
            }
        });
    }

    /**
     * Init dataset
     */
    public void initDataset() {
        for (int i = 0; i < 38; i++) {
            int pos = CWMathUtils.random(0, BagOfWords.size() - MAX_WORDS);
            int length = CWMathUtils.random(8, MAX_WORDS);
            String text = CWStringUtils.join(" ", BagOfWords.subList(pos, pos + length));
            mDataset.add(new DummyData(text, i % 2 == 0, BACKGROUNDS[CWMathUtils.random(0, BACKGROUNDS.length - 1)]));
        }
    }

    /**
     * Loadmore data
     */
    public void loadmoreData() {
        if (mDataset.size() > 500) return;
        for (int i = 0; i < 18; i++) {
            int pos = CWMathUtils.random(0, BagOfWords.size() - MAX_WORDS);
            int length = CWMathUtils.random(8, MAX_WORDS);
            String text = CWStringUtils.join(" ", BagOfWords.subList(pos, pos + length));
            mAdapter.addData(new DummyData(
                    text,
                    CWMathUtils.random(0, 10) % 2 == 0,
                    BACKGROUNDS[CWMathUtils.random(0, BACKGROUNDS.length - 1)]));
        }
    }

    /**
     * Change view type
     *
     * @param type view type
     */
    public void changeViewType(ViewType type) {
        RecyclerView.LayoutManager lm = null;
        if (ViewType.LIST_VIEW.equals(type)) {
            lm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        } else if (ViewType.GRID_VIEW.equals(type)) {
            lm = new GridLayoutManager(getContext(), 2);
        }
        if (lm != null) {
            _recycleView.setLayoutManager(lm);
            _recycleView.setAdapter(mAdapter);
        }
    }

    /**
     * Change layout animation
     *
     * @param anim layout animation
     */
    public void changeLayoutAnim(WindRecycleView.LayoutAnim anim) {
        _recycleView.setLayoutAnimation(anim.getAnim(getContext()));
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Enable swap position
     */
    public void enableSwapPosition() {
        _recycleView.enableSwapPosition(null);
        mAdapter.notifyDataSetChanged();
    }

    public enum ViewType {
        LIST_VIEW,
        GRID_VIEW
    }


    /**
     * Dummy data
     */
    public static class DummyData {
        public boolean isComplete;
        public String text;
        public int background;

        public DummyData(String text, boolean isComplete, int background) {
            this.text = text;
            this.isComplete = isComplete;
            this.background = background;
        }
    }

    /**
     * Custom adapter
     */
    public static class CustomAdapter extends WindRecycleView.Adapter<DummyData> {

        /**
         * Constructor
         *
         * @param dataset data
         */
        public CustomAdapter(List<DummyData> dataset) {
            super(dataset);
        }

        @NonNull
        @Override
        public WindRecycleView.ViewHolder<DummyData> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_ITEM) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_recycle_view_adapter, parent, false);
                return new CustomViewHolder(view);
            }
            return super.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull WindRecycleView.ViewHolder<DummyData> holder, int position) {
            super.onBindViewHolder(holder, position);
            if (holder instanceof CustomViewHolder) {
                DummyData data = getData(position);
                CustomViewHolder _holder = (CustomViewHolder) holder;
                _holder.itemView.setBackgroundResource(data.background);
                _holder.TextView.setText(data.text);
                _holder.CheckBox.setChecked(data.isComplete);
            }
        }

        @Override
        public int getItemViewType(int position) {
            // You can set custom view type here
            return super.getItemViewType(position);
        }
    }

    /**
     * Custom view holder
     */
    public static class CustomViewHolder extends WindRecycleView.ViewHolder<DummyData> {

        public Checkbox CheckBox;
        public TextView TextView;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            CheckBox = itemView.findViewById(R.id._checkbox);
            TextView = itemView.findViewById(R.id._textView);
        }
    }
}