package the.wind.library.calendar;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import androidx.annotation.NonNull;
import the.wind.library.R;
import the.wind.library.view.WindRecycleView;

public class MonthAdapter extends WindRecycleView.Adapter<DateInfo> {

    // styling
    private CalendarStyle calStyle;
    private CalendarInfo calInfo;
    private CalendarEvent calendarEvent;

    /**
     * Constructor
     *
     * @param monthInfo monthInfo
     * @param calInfo   calendar info
     * @param calStyle  calendar style
     * @param calEvent  calendar event
     */
    public MonthAdapter(MonthInfo monthInfo, final CalendarInfo calInfo, CalendarStyle calStyle, final CalendarEvent calEvent) {
        super(monthInfo.getDateInfoList());
        this.calInfo = calInfo;
        this.calStyle = calStyle;
        this.calendarEvent = calEvent;

        setOnItemTouchDownListener(new OnItemTouchDownListener<DateInfo>() {
            @Override
            public void onTouchDown(WindRecycleView.ViewHolder<DateInfo> viewHolder, View view, DateInfo data) {
                ViewHolder vh = (ViewHolder) viewHolder;
                if (calEvent.dateItemTouchDownListener != null) {
                    if (calEvent.dateItemTouchDownListener.onTouchDown(vh, view, data)) {
                        return;
                    }
                }
                vh.touchDown();
            }
        });

        setOnItemTouchUpListener(new OnItemTouchUpListener<DateInfo>() {
            @Override
            public void onTouchUp(WindRecycleView.ViewHolder<DateInfo> viewHolder, View view, DateInfo data) {
                ViewHolder vh = (ViewHolder) viewHolder;
                if (calEvent.dateItemTouchUpListener != null) {
                    if (calEvent.dateItemTouchUpListener.onTouchUp((ViewHolder) viewHolder, view, data)) {
                        return;
                    }
                }
                vh.bindData(data, calInfo);
            }
        });

        setOnItemClickListener(new OnItemClickListener<DateInfo>() {
            @Override
            public void onClick(WindRecycleView.ViewHolder<DateInfo> viewHolder, View view, DateInfo data) {
                ViewHolder vh = (ViewHolder) viewHolder;
                if (calEvent.dateItemClickListener != null) {
                    calEvent.dateItemClickListener.onClick(vh, view, data);
                }
            }
        });

        setOnItemLongClickListener(new OnItemLongClickListener<DateInfo>() {
            @Override
            public void onLongClick(WindRecycleView.ViewHolder<DateInfo> viewHolder, View view, DateInfo data) {
                ViewHolder vh = (ViewHolder) viewHolder;
                if (calEvent.dateItemLongClickListener != null) {
                    calEvent.dateItemLongClickListener.onLongClick(vh, view, data);
                }
            }
        });

        setOnItemDoubleClickListener(new OnItemDoubleClickListener<DateInfo>() {
            @Override
            public void onDoubleClick(WindRecycleView.ViewHolder<DateInfo> viewHolder, View view, DateInfo data) {
                ViewHolder vh = (ViewHolder) viewHolder;
                if (calEvent.dateItemDoubleClickListener != null) {
                    calEvent.dateItemDoubleClickListener.onDoubleClick(vh, view, data);
                }
            }
        });
    }

    /* ---------------------- OVERRIDE ----------------------- */

    @NonNull
    @Override
    public WindRecycleView.ViewHolder<DateInfo> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wl_calendard_date_view, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.setCalStyle(calStyle);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull WindRecycleView.ViewHolder<DateInfo> holder, int position) {
        super.onBindViewHolder(holder, position);
        if (holder instanceof ViewHolder) {
            ViewHolder _holder = (ViewHolder) holder;
            DateInfo data = getData(position);
            _holder.bindData(data, calInfo);
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
    public static class ViewHolder extends WindRecycleView.ViewHolder<DateInfo> {

        // Views
        private final TextView _dateTextView;
        private final TextView _lunarDateTextView;
        private final TextView _eventSymbol;

        // styling
        private CalendarStyle calStyle;

        /**
         * Constructor
         *
         * @param itemView item view
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // bind views
            _dateTextView = itemView.findViewById(R.id._dateTextView);
            _lunarDateTextView = itemView.findViewById(R.id._lunarDateTextView);
            _eventSymbol = itemView.findViewById(R.id._eventSymbol);
        }

        /**
         * Bind date
         *
         * @param data    date info data
         * @param calInfo calendar info data
         */
        protected void bindData(DateInfo data, CalendarInfo calInfo) {
            super.bindData(data);
            if (data == null) {
                itemView.setVisibility(View.GONE);
                return;
            }
            String dateId = data.getId();
            boolean hasEvent = calInfo.hasEvent(dateId);
            boolean isToday = calInfo.isToday(dateId);
            boolean isWeekend = data.isWeekend();
            boolean isHighlight = calInfo.isHighlight(dateId);

            // Set text value
            _dateTextView.setText(String.format(Locale.getDefault(), "%d", data.getDayOfMonth()));
            _lunarDateTextView.setText("25/12"); // TODO: set lunar date later
            _eventSymbol.setVisibility(hasEvent && !isToday ? View.VISIBLE : View.GONE);

            // Set text color
            if (isToday) {
                _dateTextView.setTextColor(calStyle.todayTextColor());
                itemView.setBackgroundResource(calStyle.todayBackground());

            } else if (isHighlight) {
                _dateTextView.setTextColor(calStyle.highlightTextColor());
                itemView.setBackgroundResource(calStyle.highlightBackground());

            } else if (isWeekend) {
                _dateTextView.setTextColor(calStyle.weekendTextColor());
                itemView.setBackgroundResource(calStyle.weekendBackground());

            } else if (hasEvent) {
                _dateTextView.setTextColor(calStyle.eventTextColor());
                itemView.setBackgroundResource(calStyle.eventBackground());

            } else {
                _dateTextView.setTextColor(calStyle.dateTextColor());
                itemView.setBackgroundResource(calStyle.cellBackground());
            }
        }

        /**
         * Set cell style
         *
         * @param calStyle cell style
         */
        private void setCalStyle(CalendarStyle calStyle) {
            this.calStyle = calStyle;

            // Configure cell size
            ViewGroup.LayoutParams lp = itemView.getLayoutParams();
            lp.width = calStyle.dateCellSize();
            lp.height = calStyle.dateCellSize();
            itemView.setLayoutParams(lp);

            // Configure date text
            _dateTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, calStyle.dateTextSize());
            _lunarDateTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, calStyle.lunarDateTextSize());
            _lunarDateTextView.setTextColor(calStyle.lunarDateTextColor());
            _eventSymbol.setTextSize(TypedValue.COMPLEX_UNIT_PX, calStyle.eventSymbolSize());
        }

        /**
         * Touch down on item
         */
        private void touchDown() {
            if (itemView.getBackground() == null) {
                itemView.setBackgroundResource(calStyle.touchBackground());
            }
        }
    }
}
