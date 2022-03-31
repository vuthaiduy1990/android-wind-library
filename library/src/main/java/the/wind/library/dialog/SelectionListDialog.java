package the.wind.library.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.Collections;
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
    protected final SelectionListAdapter<T> listAdapter;
    private final ViewGroup _customViewLayout;

    // search box
    protected final SearchBox _searchBox;
    private SearchBox.OnSearchListener searchListener;
    private SearchBox.OnEnterListener searchEnterListener;
    private SearchBox.OnToggleListener searchToggleListener;

    // style and string
    private int itemBackgroundHoverRes;

    // listener
    protected OnItemSelectionListener<T> itemSelectionListener;
    private OnMultiSelectionListener<T> multiSelectionListener;

    // mode
    private SelectionMode selectionMode;

    /**
     * Constructor
     *
     * @param context       application context
     * @param selectionMode selection mode
     * @param dataset       data
     */
    public SelectionListDialog(@NonNull Context context, SelectionMode selectionMode, List<T> dataset) {
        super(context, LayoutType.FUBUKI);
        this.selectionMode = selectionMode;
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
        listAdapter.setDataTransformer(new SelectionListAdapter.DataTransformer<T>() {
            @Override
            public String dataToText(@NonNull T itemData) {
                return itemText(itemData);
            }

            @Override
            public String dataToId(@NonNull T itemData) {
                return itemId(itemData);
            }
        });
        listAdapter.setOnItemClickListener(new WindRecycleView.Adapter.OnItemClickListener<T>() {
            @Override
            public void onClick(WindRecycleView.ViewHolder<T> viewHolder, View view, T data) {
                if (SelectionMode.MULTIPLE.equals(selectionMode)) {
                    SelectionListAdapter.DefaultViewHolder<T> _holder = null;
                    if (viewHolder instanceof SelectionListAdapter.DefaultViewHolder) {
                        _holder = (SelectionListAdapter.DefaultViewHolder<T>) viewHolder;
                    }

                    boolean isSelected = listAdapter.isSelectedItem(data);
                    if (isSelected) {
                        listAdapter.removeSelector(data);
                        if (_holder != null) _holder.unselect();
                    } else {
                        listAdapter.addSelector(data);
                        if (_holder != null) _holder.select();
                    }

                    if (itemSelectionListener != null) {
                        itemSelectionListener.onSelect(SelectionListDialog.this, viewHolder.itemView, data);
                    }

                } else {
                    listAdapter.setSelector(data);
                    if (itemSelectionListener != null) {
                        if (!itemSelectionListener.onSelect(SelectionListDialog.this, viewHolder.itemView, data)) {
                            dismiss();
                        }
                    }
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
        _rvHolder.setAdapter(listAdapter);

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
        if (SelectionMode.MULTIPLE.equals(selectionMode)) {
            addButton(Button.Type.SUCCESS, context.getString(R.string.wl_select), null).setOnClickListener(v -> {
                if (multiSelectionListener != null) {
                    if (!multiSelectionListener.onSelect(SelectionListDialog.this, getAdapter().getSelectors())) {
                        dismiss();
                    }
                }
            });
        }

        // bind event
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                onDialogDismiss(dialog);
            }
        });
    }

    /* ---------------------- OVERRIDE ----------------------- */

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /**
     * @param itemData data respective to selection item
     * @return selection item's ID
     */
    protected abstract String itemId(@NonNull T itemData);

    /**
     * @param itemData data respective to selection item
     * @return selection item's text
     */
    protected abstract String itemText(@NonNull T itemData);

    /**
     * On dismiss
     *
     * @param dialog dialog
     */
    protected void onDialogDismiss(@NonNull DialogInterface dialog) {
        _searchBox.closeSearch();
    }

    /* ---------------------- GET-SET ------------------------ */

    /**
     * @return selection list adapter
     */
    public SelectionListAdapter<T> getAdapter() {
        return listAdapter;
    }

    /**
     * Set view holder generator
     *
     * @param generator view holder generator
     */
    public void setCustomViewHolderGenerator(SelectionListAdapter.ViewHolderGenerator<T> generator) {
        if (listAdapter != null) {
            listAdapter.setCustomViewHolderGenerator(generator);
        }
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

    /**
     * Set on item selection listener
     *
     * @param listener item selection listener
     */
    public void setOnItemSelectionListener(OnItemSelectionListener<T> listener) {
        this.itemSelectionListener = listener;
    }

    /**
     * Set on multiple item selection result listener
     *
     * @param listener listener
     */
    public void setOnMultiSelectionListener(OnMultiSelectionListener<T> listener) {
        this.multiSelectionListener = listener;
    }

    /* ---------------------- METHOD ------------------------- */

    /**
     * Show dialog with given selected data
     *
     * @param items selected data
     */
    public void show(@Nullable List<T> items) {
        show();
        if (items == null || items.isEmpty()) {
            listAdapter.setSelectors(null);
            listAdapter.notifyDataSetChanged();
            return;
        }

        // scroll to selected item
        listAdapter.setSelectors(items);
        postDelayed(() -> {
            for (int i = 0; i < listAdapter.getItemCount(); i++) {
                T data = listAdapter.getData(i);
                if (listAdapter.isSelectedItem(data)) {
                    _rvHolder.scrollToPosition(i);
                    break;
                }
            }
            listAdapter.notifyDataSetChanged();
        }, 300);
    }

    /**
     * Show dialog with given selected data
     *
     * @param item selected data
     */
    public void show(@Nullable T item) {
        show(item != null ? Collections.singletonList(item) : null);
    }

    /* ---------------------- INNER CLASS -------------------- */

    /**
     * On item selection listener
     *
     * @param <T> data type
     */
    public interface OnItemSelectionListener<T> {

        /**
         * Trigger when user click on item item view
         *
         * @param dialog   selection dialog
         * @param itemView selected item view
         * @param data     seected item data
         * @return true if consume the event, else return false
         */
        boolean onSelect(@NonNull SelectionListDialog<T> dialog, @NonNull View itemView, @NonNull T data);
    }

    /**
     * On multiple items selection listener
     *
     * @param <T> data type
     */
    public interface OnMultiSelectionListener<T> {

        /**
         * Trigger when user click select button
         *
         * @param dialog selection dialog
         * @param items  list of selected items
         * @return true if consume the event, else return false
         */
        boolean onSelect(@NonNull SelectionListDialog<T> dialog, List<T> items);
    }

    /**
     * Selection Mode
     */
    public enum SelectionMode {
        SINGLE,
        MULTIPLE
    }

}
