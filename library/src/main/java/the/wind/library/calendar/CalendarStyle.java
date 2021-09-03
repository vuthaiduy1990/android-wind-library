package the.wind.library.calendar;

public class CalendarStyle {

    private String[] weekDays;
    private float weekDayTextSize;
    private int weekDayTextColor;
    private int weekDayPanelBackground;
    private int weekDayHoverBackground;

    private int dateCellSize;
    private float dateTextSize;
    private float dateLunarTextSize;
    private float dateEventSymbolSize;

    private int dateTextColor;
    private int dateLunarTextColor;
    private int dateEventTextColor;
    private int dateWeekendTextColor;
    private int dateTodayTextColor;
    private int dateHighlightTextColor;
    private int dateHighlightLunarTextColor;

    private int monthPanelViewBackground;
    private int dateCellBackground;
    private int dateEventBackground;
    private int dateWeekendBackground;
    private int dateTodayBackground;
    private int dateHighlightBackground;

    /**
     * @return day of weeks (monday, tuesday ... sunday)
     */
    public String[] weekDays() {
        return weekDays;
    }

    /**
     * Set week days
     *
     * @param weekDays days of week
     * @return style
     */
    public CalendarStyle weekDays(String[] weekDays) {
        this.weekDays = weekDays;
        return this;
    }

    /**
     * @return week day text size
     */
    public float weekDayTextSize() {
        return weekDayTextSize;
    }

    /**
     * Set week day text size
     *
     * @param weekDayTextSize size
     * @return style
     */
    public CalendarStyle weekDayTextSize(float weekDayTextSize) {
        this.weekDayTextSize = weekDayTextSize;
        return this;
    }

    /**
     * @return week day text color
     */
    public int weekDayTextColor() {
        return weekDayTextColor;
    }

    /**
     * Set week day text color
     *
     * @param weekDayTextColor color
     * @return style
     */
    public CalendarStyle weekDayTextColor(int weekDayTextColor) {
        this.weekDayTextColor = weekDayTextColor;
        return this;
    }

    /**
     * @return week day panel background
     */
    public int weekDayPanelBackground() {
        return weekDayPanelBackground;
    }

    /**
     * Set week day panel background
     *
     * @param weekDayPanelBackground background
     * @return style
     */
    public CalendarStyle weekDayPanelBackground(int weekDayPanelBackground) {
        this.weekDayPanelBackground = weekDayPanelBackground;
        return this;
    }

    /**
     * @return week day hover background
     */
    public int weekDayHoverBackground() {
        return weekDayHoverBackground;
    }

    /**
     * Set week day hover background
     *
     * @param weekDayHoverBackground background
     * @return style
     */
    public CalendarStyle weekDayHoverBackground(int weekDayHoverBackground) {
        this.weekDayHoverBackground = weekDayHoverBackground;
        return this;
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
    public float dateLunarTextSize() {
        return dateLunarTextSize;
    }

    /**
     * Set lunar date text size
     *
     * @param dateLunarTextSize size
     * @return style
     */
    public CalendarStyle dateLunarTextSize(float dateLunarTextSize) {
        this.dateLunarTextSize = dateLunarTextSize;
        return this;
    }

    /**
     * @return event symbol size
     */
    public float dateEventSymbolSize() {
        return dateEventSymbolSize;
    }

    /**
     * Set event symbol size
     *
     * @param dateEventSymbolSize size
     * @return style
     */
    public CalendarStyle dateEventSymbolSize(float dateEventSymbolSize) {
        this.dateEventSymbolSize = dateEventSymbolSize;
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
     * @return lunar date text color
     */
    public int dateLunarTextColor() {
        return dateLunarTextColor;
    }

    /**
     * Set lunar date text color
     *
     * @param dateLunarTextColor color
     * @return style
     */
    public CalendarStyle dateLunarTextColor(int dateLunarTextColor) {
        this.dateLunarTextColor = dateLunarTextColor;
        return this;
    }

    /**
     * @return date event text color
     */
    public int dateEventTextColor() {
        return dateEventTextColor;
    }

    /**
     * Set date event text color
     *
     * @param dateEventTextColor color
     * @return style
     */
    public CalendarStyle dateEventTextColor(int dateEventTextColor) {
        this.dateEventTextColor = dateEventTextColor;
        return this;
    }

    /**
     * @return date weekend text color
     */
    public int dateWeekendTextColor() {
        return dateWeekendTextColor;
    }

    /**
     * Set date weekend text color
     *
     * @param dateWeekendTextColor color
     * @return style
     */
    public CalendarStyle dateWeekendTextColor(int dateWeekendTextColor) {
        this.dateWeekendTextColor = dateWeekendTextColor;
        return this;
    }

    /**
     * @return today text color
     */
    public int dateTodayTextColor() {
        return dateTodayTextColor;
    }

    /**
     * Set today text color
     *
     * @param dateTodayTextColor color
     * @return style
     */
    public CalendarStyle dateTodayTextColor(int dateTodayTextColor) {
        this.dateTodayTextColor = dateTodayTextColor;
        return this;
    }

    /**
     * @return highlight text color
     */
    public int dateHighlightTextColor() {
        return dateHighlightTextColor;
    }

    /**
     * Set highlightT text color
     *
     * @param dateHighlightTextColor color
     * @return style
     */
    public CalendarStyle dateHighlightTextColor(int dateHighlightTextColor) {
        this.dateHighlightTextColor = dateHighlightTextColor;
        return this;
    }

    /**
     * @return highlight lunar text color
     */
    public int dateHighlightLunarTextColor() {
        return dateHighlightLunarTextColor;
    }

    /**
     * Set highlight lunar text color
     *
     * @param dateHighlightLunarTextColor color
     * @return style
     */
    public CalendarStyle dateHighlightLunarTextColor(int dateHighlightLunarTextColor) {
        this.dateHighlightLunarTextColor = dateHighlightLunarTextColor;
        return this;
    }

    /**
     * @return month panel view background
     */
    public int monthPanelViewBackground() {
        return monthPanelViewBackground;
    }

    /**
     * Set month panel view background
     *
     * @param monthPanelViewBackground background
     * @return style
     */
    public CalendarStyle monthPanelViewBackground(int monthPanelViewBackground) {
        this.monthPanelViewBackground = monthPanelViewBackground;
        return this;
    }

    /**
     * @return normal date cell background
     */
    public int dateCellBackground() {
        return dateCellBackground;
    }

    /**
     * Set date cell background
     *
     * @param dateCellBackground background
     * @return style
     */
    public CalendarStyle dateCellBackground(int dateCellBackground) {
        this.dateCellBackground = dateCellBackground;
        return this;
    }

    /**
     * @return date cell background when having event
     */
    public int dateEventBackground() {
        return dateEventBackground;
    }

    /**
     * Set background when cell has event
     *
     * @param dateEventBackground background
     * @return style
     */
    public CalendarStyle dateEventBackground(int dateEventBackground) {
        this.dateEventBackground = dateEventBackground;
        return this;
    }

    /**
     * @return date cell background when date is weekend
     */
    public int dateWeekendBackground() {
        return dateWeekendBackground;
    }

    /**
     * Set cell background when date is weekend
     *
     * @param dateWeekendBackground background
     * @return style
     */
    public CalendarStyle dateWeekendBackground(int dateWeekendBackground) {
        this.dateWeekendBackground = dateWeekendBackground;
        return this;
    }

    /**
     * @return date cell background when date is today
     */
    public int dateTodayBackground() {
        return dateTodayBackground;
    }

    /**
     * Set cell background when date is today
     *
     * @param dateTodayBackground background
     * @return style
     */
    public CalendarStyle dateTodayBackground(int dateTodayBackground) {
        this.dateTodayBackground = dateTodayBackground;
        return this;
    }

    /**
     * @return date cell background when date is highlight
     */
    public int dateHighlightBackground() {
        return dateHighlightBackground;
    }

    /**
     * Set cell background when date is highlight
     *
     * @param dateHighlightBackground background
     * @return style
     */
    public CalendarStyle dateHighlightBackground(int dateHighlightBackground) {
        this.dateHighlightBackground = dateHighlightBackground;
        return this;
    }
}
