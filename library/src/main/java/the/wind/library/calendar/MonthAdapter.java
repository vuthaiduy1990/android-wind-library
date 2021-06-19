package the.wind.library.calendar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import androidx.annotation.NonNull;
import the.wind.library.R;
import the.wind.library.view.WindRecycleView;

class MonthAdapter extends WindRecycleView.Adapter<DateInfo> {

    /**
     * Constructor
     *
     * @param monthInfo monthInfo
     */
    public MonthAdapter(MonthInfo monthInfo) {
        super(monthInfo.getDateInfoList());
    }

    /* ---------------------- OVERRIDE ----------------------- */

    @NonNull
    @Override
    public WindRecycleView.ViewHolder<DateInfo> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wl_calendard_date_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WindRecycleView.ViewHolder<DateInfo> holder, int position) {
        super.onBindViewHolder(holder, position);
        if (holder instanceof ViewHolder) {
            ViewHolder _holder = (ViewHolder) holder;
            DateInfo data = getData(position);
            _holder.bindData(data);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_ITEM;
    }

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /* ---------------------- METHOD ------------------------- */

    /* ---------------------- INNER CLASS -------------------- */

    /**
     * View holder
     */
    static class ViewHolder extends WindRecycleView.ViewHolder<DateInfo> {

        private final TextView _dateNumber;

        /**
         * Constructor
         *
         * @param itemView item view
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // bind views
            _dateNumber = itemView.findViewById(R.id._dateNumber);
        }

        @Override
        protected void bindData(DateInfo data) {
            super.bindData(data);
            if (data == null) {
                return;
            }
            _dateNumber.setText(String.format(Locale.getDefault(), "%d", data.getDayOfMonth()));
        }
    }
}
