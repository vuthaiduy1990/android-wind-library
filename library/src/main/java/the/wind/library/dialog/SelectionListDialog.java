package the.wind.library.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.List;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import the.wind.library.R;
import the.wind.library.adapter.SelectionListAdapter;
import the.wind.library.view.Button;
import the.wind.library.view.SearchBox;
import the.wind.library.view.WindRecycleView;

public abstract class SelectionListDialog<T> extends WindDialog {

    // views
    private final WindRecycleView _rvHolder;
    private final SelectionListAdapter<T> listAdapter;
    private final ViewGroup _customViewLayout;

    // search box
    protected final SearchBox _searchBox;
    private SearchBox.OnSearchListener searchListener;
    private SearchBox.OnEnterListener searchEnterListener;
    private SearchBox.OnToggleListener searchToggleListener;

    // style and string
    private int itemBackgroundHoverRes;

    /**
     * Constructor
     *
     * @param context application context
     */
    public SelectionListDialog(@NonNull Context context, List<T> dataset) {
        super(context, LayoutType.FUBUKI);
        setContentView(R.layout.wl_dialog_selection_list);
        setInOutAnimType(InOutAnimType.SWEET_ALERT);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        setItemBackgroundHoverRes(R.drawable.wl_background_hover);

        // bind custom view layout
        _customViewLayout = contentView().findViewById(R.id._customViewLayout);

        // bind list view
        _rvHolder = contentView().findViewById(R.id._rvHolder);
        _rvHolder.setHasFixedSize(true); // to improve the performance
        listAdapter = new SelectionListAdapter<>(dataset);
        _rvHolder.setAdapter(listAdapter);
        listAdapter.setDataTransformer(new SelectionListAdapter.DataTransformer<T>() {
            @Override
            public String dataToText(@NonNull T itemData) {
                return itemText(itemData);
            }

            @Override
            public boolean compare(@NonNull T a, @NonNull T b) {
                return equal(a, b);
            }
        });
        listAdapter.setOnItemClickListener(new WindRecycleView.Adapter.OnItemClickListener<T>() {
            @Override
            public void onClick(WindRecycleView.ViewHolder<T> viewHolder, View view, T data) {
                listAdapter.setSelected(data);
                listAdapter.notifyDataSetChanged();
                if (!onSelection(SelectionListDialog.this, viewHolder.itemView, data)) {
                    dismiss();
                }
            }
        }).setOnItemTouchDownListener(new WindRecycleView.Adapter.OnItemTouchDownListener<T>() {
            @Override
            public void onTouchDown(WindRecycleView.ViewHolder<T> viewHolder, View view, T t) {
                viewHolder.itemView.setBackgroundResource(itemBackgroundHoverRes);
            }
        }).setOnItemTouchUpListener(new WindRecycleView.Adapter.OnItemTouchUpListener<T>() {
            @Override
            public void onTouchUp(WindRecycleView.ViewHolder<T> viewHolder, View view, T t) {
                viewHolder.itemView.setBackground(null);
            }
        });

        // bind search box
        _searchBox = new SearchBox(getContext());
        LinearLayout.LayoutParams searchBoxLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        _searchBox.setLayoutParams(searchBoxLp);
        _searchBox.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        _searchBox.setCompactMode(true);
        _searchBox.setBackgroundResource(R.drawable.wl_form_input_background);
        addViewToHeader(_searchBox);
        _searchBox.setVisibility(View.GONE);
        _searchBox.setOnSearchListener(new SearchBox.OnSearchListener() {
            @Override
            public int onSearch(EditText view, String oldInput, String newInput) {
                if (searchListener != null) {
                    return searchListener.onSearch(view, oldInput, newInput);
                }
                return 0;
            }
        });
        _searchBox.setOnToggleListener(new SearchBox.OnToggleListener() {
            @Override
            public void onToggle(boolean compactMode) {
                setTitleVisible(compactMode);
                if (searchToggleListener != null) {
                    searchToggleListener.onToggle(compactMode);
                }
            }
        });
        _searchBox.setOnEnterListener(new SearchBox.OnEnterListener() {
            @Override
            public void onEnter(EditText view, String oldInput, String newInput) {
                if (searchEnterListener != null) {
                    searchEnterListener.onEnter(view, oldInput, newInput);
                }
            }
        });

        // bind button
        addButton(Button.Type.GRAY, context.getString(R.string.wl_close), null).setOnClickListener(v -> dismiss());

        // bind event
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                listAdapter.setSelected(null);
                onDialogDismiss(dialog);
            }
        });
    }

    /* ---------------------- OVERRIDE ----------------------- */

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /**
     * Check if two item is equal or not
     *
     * @param a item data 1
     * @param b item data 2
     * @return true if equal
     */
    protected abstract boolean equal(@NonNull T a, @NonNull T b);

    /**
     * @param itemData data respective to selection item
     * @return selection item's text
     */
    protected abstract String itemText(@NonNull T itemData);

    /**
     * Trigger when user click on item item view
     *
     * @param itemView item view
     * @param data     item data
     * @return true if consume the event, else return false
     */
    protected abstract boolean onSelection(@NonNull SelectionListDialog<T> dialog, @NonNull View itemView, @NonNull T data);

    /**
     * On dismiss
     *
     * @param dialog dialog
     */
    protected void onDialogDismiss(@NonNull DialogInterface dialog) {

    }

    /* ---------------------- GET-SET ------------------------ */

    /**
     * @return selection list adapter
     */
    public SelectionListAdapter<T> getAdapter() {
        return listAdapter;
    }

    /**
     * @return custom view layout
     */
    public ViewGroup getCustomViewLayout() {
        return _customViewLayout;
    }

    /**
     * Add custom view to layout
     *
     * @param customView custom view
     */
    public void addCustomView(View customView) {
        _customViewLayout.addView(customView);
    }

    /**
     * @return list view holder
     */
    public WindRecycleView getListView() {
        return _rvHolder;
    }

    /**
     * Set search box visible
     *
     * @param visible visible
     */
    public void setSearchBoxVisible(boolean visible) {
        _searchBox.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /**
     * Set searching listener
     *
     * @param listener listener
     */
    public void setOnSearchBoxListener(SearchBox.OnSearchListener listener) {
        searchListener = listener;
    }

    /**
     * Set search enter listener
     *
     * @param listener listener
     */
    public void setOnSearchEnterListener(SearchBox.OnEnterListener listener) {
        searchEnterListener = listener;
    }

    /**
     * Set search action listener
     *
     * @param listener listener
     */
    public void setOnSearchToggleListener(SearchBox.OnToggleListener listener) {
        searchToggleListener = listener;
    }

    /**
     * Set search box background
     *
     * @param resId string resource id
     * @return dialog
     */
    public SelectionListDialog<T> setSearchBoxBackgroundRes(@DrawableRes int resId) {
        if (_searchBox != null) {
            _searchBox.setBackgroundResource(resId);
        }
        return this;
    }

    /**
     * Set search box background
     *
     * @param resId string resource id
     * @return dialog
     */
    public SelectionListDialog<T> setItemBackgroundHoverRes(@DrawableRes int resId) {
        itemBackgroundHoverRes = resId;
        return this;
    }

    /* ---------------------- METHOD ------------------------- */

    /**
     * Show dialog with given selected data
     *
     * @param data selected data
     */
    public void show(@Nullable T data) {
        show();
        if (data == null) {
            listAdapter.notifyDataSetChanged();
            return;
        }

        // scroll to selected item
        listAdapter.setSelected(data);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < listAdapter.getItemCount(); i++) {
                    if (equal(listAdapter.getData(i), listAdapter.getSelected())) {
                        _rvHolder.scrollToPosition(i);
                        break;
                    }
                }
                listAdapter.notifyDataSetChanged();
            }
        }, 300);
    }

    /* ---------------------- INNER CLASS -------------------- */
}
