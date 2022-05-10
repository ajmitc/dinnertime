package dinnertime.model;

import java.time.LocalDate;

/**
 *
 * @author aaron.mitchell
 */
public class MealPlanEntry {
    private String id;
    private String mealPlanId;
    private String type; // plain text, url, recipe id
    private String content; // text, url, recipe ID
    private String notes;
    private String category; // Breakfast, Lunch, Dinner, Snack
    private LocalDate date;

    public MealPlanEntry(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMealPlanId() {
        return mealPlanId;
    }

    public void setMealPlanId(String mealPlanId) {
        this.mealPlanId = mealPlanId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
