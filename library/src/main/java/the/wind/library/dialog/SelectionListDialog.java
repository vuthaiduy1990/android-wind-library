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
import androidx.annotation.StringRes;
import the.wind.library.R;
import the.wind.library.view.Button;
import the.wind.library.view.SearchBox;
import the.wind.library.view.WindRecycleView;

public abstract class SelectionListDialog<T> extends WindDialog {

    // views
    protected final WindRecycleView _rvHolder;
    protected final SelectionListAdapter<T> listAdapter;

    // search box
    protected final SearchBox _searchBox;
    private SearchBox.OnActionListener searchListener;

    // style and string
    private int closeButtonTextRes;
    private int searchBoxBackgroundRes;
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

        setCloseButtonTextRes(R.string.wl_dialog_button_close);
        setItemBackgroundHoverRes(R.drawable.wl_background_hover);
        setSearchBoxBackgroundRes(R.drawable.wl_form_input_background);

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
                onSelection(SelectionListDialog.this, viewHolder.itemView, data);
                dismiss();
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
        _searchBox.setInputBackground(searchBoxBackgroundRes);
        addViewToHeader(_searchBox);
        _searchBox.setVisibility(View.GONE);
        _searchBox.setOnActionListener(new SearchBox.OnActionListener() {
            @Override
            public void onSearch(EditText editText, String oldInput, String newInput) {
                if (searchListener != null) {
                    searchListener.onSearch(editText, oldInput, newInput);
                }
            }

            @Override
            public void onToggle(boolean compactMode) {
                setTitleVisible(compactMode);
                if (searchListener != null) {
                    searchListener.onToggle(compactMode);
                }
            }
        });

        // bind button
        addButton(Button.Type.GRAY, context.getString(closeButtonTextRes), null).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

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
     */
    protected abstract void onSelection(@NonNull SelectionListDialog<T> dialog, @NonNull View itemView, @NonNull T data);

    /**
     * On dismiss
     *
     * @param dialog dialog
     */
    protected void onDialogDismiss(@NonNull DialogInterface dialog) {

    }

    /* ---------------------- GET-SET ------------------------ */

    /**
     * Set search box visible
     *
     * @param visible visible
     */
    public void setSearchBoxVisible(boolean visible) {
        _searchBox.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /**
     * Set search action listener
     *
     * @param listener listener
     */
    public void setOnSearchBoxListener(SearchBox.OnActionListener listener) {
        searchListener = listener;
    }

    /**
     * Set close button text resource id
     *
     * @param resId string resource id
     * @return dialog
     */
    public SelectionListDialog<T> setCloseButtonTextRes(@StringRes int resId) {
        closeButtonTextRes = resId;
        return this;
    }

    /**
     * Set search box background
     *
     * @param resId string resource id
     * @return dialog
     */
    public SelectionListDialog<T> setSearchBoxBackgroundRes(@DrawableRes int resId) {
        searchBoxBackgroundRes = resId;
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
