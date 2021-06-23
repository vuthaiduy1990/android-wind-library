package the.wind.library.calendar;

class CalendarStyle {

    private int dateCellSize;
    private float dateTextSize;
    private float lunarDateTextSize;
    private float eventSymbolSize;
    private int cellBackground;
    private int eventBackground;
    private int weekendBackground;
    private int todayBackground;
    private int dateTextColor;
    private int eventTextColor;
    private int weekendTextColor;
    private int todayTextColor;

    /**
     * Create new style configuration
     *
     * @return style
     */
    public static CalendarStyle config() {
        return new CalendarStyle();
    }

    /**
     * @return date cell size
     */
    public int dateCellSize() {
        return dateCellSize;
    }

    /**
     * Set date cell size
     *
     * @param dateCellSize size
     * @return style
     */
    public CalendarStyle dateCellSize(int dateCellSize) {
        this.dateCellSize = dateCellSize;
        return this;
    }

    /**
     * @return date text size
     */
    public float dateTextSize() {
        return dateTextSize;
    }


    /**
     * Set date text size
     *
     * @param dateTextSize size
     * @return style
     */
    public CalendarStyle dateTextSize(float dateTextSize) {
        this.dateTextSize = dateTextSize;
        return this;
    }

    /**
     * @return lunar date text size
     */
    public float lunarDateTextSize() {
        return lunarDateTextSize;
    }

    /**
     * Set lunar date text size
     *
     * @param lunarDateTextSize size
     * @return style
     */
    public CalendarStyle lunarDateTextSize(float lunarDateTextSize) {
        this.lunarDateTextSize = lunarDateTextSize;
        return this;
    }

    /**
     * @return event symbol size
     */
    public float eventSymbolSize() {
        return eventSymbolSize;
    }

    /**
     * Set event symbol size
     *
     * @param eventSymbolSize size
     * @return style
     */
    public CalendarStyle eventSymbolSize(float eventSymbolSize) {
        this.eventSymbolSize = eventSymbolSize;
        return this;
    }

    /**
     * @return normal date cell background
     */
    public int cellBackground() {
        return cellBackground;
    }

    /**
     * Set cell background
     *
     * @param cellBackground background
     * @return style
     */
    public CalendarStyle cellBackground(int cellBackground) {
        this.cellBackground = cellBackground;
        return this;
    }

    /**
     * @return date cell background when having event
     */
    public int eventBackground() {
        return eventBackground;
    }

    /**
     * Set background when cell has event
     *
     * @param eventBackground background
     * @return style
     */
    public CalendarStyle eventBackground(int eventBackground) {
        this.eventBackground = eventBackground;
        return this;
    }

    /**
     * @return date cell background when date is weekend
     */
    public int weekendBackground() {
        return weekendBackground;
    }

    /**
     * Set cell background when date is weekend
     *
     * @param weekendBackground background
     * @return style
     */
    public CalendarStyle weekendBackground(int weekendBackground) {
        this.weekendBackground = weekendBackground;
        return this;
    }

    /**
     * @return date cell background when date is today
     */
    public int todayBackground() {
        return todayBackground;
    }

    /**
     * Set cell background when date is today
     *
     * @param todayBackground background
     * @return style
     */
    public CalendarStyle todayBackground(int todayBackground) {
        this.todayBackground = todayBackground;
        return this;
    }

    /**
     * @return date text color
     */
    public int dateTextColor() {
        return dateTextColor;
    }

    /**
     * Set date text color
     *
     * @param dateTextColor color
     * @return style
     */
    public CalendarStyle dateTextColor(int dateTextColor) {
        this.dateTextColor = dateTextColor;
        return this;
    }

    /**
     * @return date event text color
     */
    public int eventTextColor() {
        return eventTextColor;
    }

    /**
     * Set date event text color
     *
     * @param eventTextColor color
     * @return style
     */
    public CalendarStyle eventTextColor(int eventTextColor) {
        this.eventTextColor = eventTextColor;
        return this;
    }

    /**
     * @return date weekend text color
     */
    public int weekendTextColor() {
        return weekendTextColor;
    }

    /**
     * Set date weekend text color
     *
     * @param weekendTextColor color
     * @return style
     */
    public CalendarStyle weekendTextColor(int weekendTextColor) {
        this.weekendTextColor = weekendTextColor;
        return this;
    }

    /**
     * @return today text color
     */
    public int todayTextColor() {
        return todayTextColor;
    }

    /**
     * Set today text color
     *
     * @param todayTextColor color
     * @return style
     */
    public CalendarStyle todayTextColor(int todayTextColor) {
        this.todayTextColor = todayTextColor;
        return this;
    }
}
