package dinnertime.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author aaron.mitchell
 */
public class Recipe {
    private String id;
    private String driveId;
    private String url;
    private Double rating;

    // RecipeLabel table
    private Set<String> labels = new HashSet<>();

    // ProfileRecipe table
    private boolean favorite;
    private String notes;

    public Recipe(){
    }

    public Recipe(String driveId, String title){
        this.driveId = driveId;
    }

    public void parseAndAddLabels(String text){
        final List<String> IGNORE_KEYWORDS = Arrays.asList(
                "of",
                "the", 
                "with" 
        );
        Set<String> labels = new HashSet<>();
        String[] parts = text.split("[ _-]");
        for (String part: parts){
            if (!IGNORE_KEYWORDS.contains(part.toLowerCase()))
                labels.add(part);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDriveId() {
        return driveId;
    }

    public void setDriveId(String driveId) {
        this.driveId = driveId;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Set<String> getLabels() {
        return labels;
    }

    public void setLabels(Set<String> labels) {
        this.labels = labels;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
