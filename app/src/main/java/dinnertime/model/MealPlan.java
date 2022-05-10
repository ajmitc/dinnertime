package dinnertime.model;

import java.time.ZonedDateTime;

/**
 * Represents a Meal Plan that is tied to a profile
 * @author aaron.mitchell
 */
public class MealPlan {
    public static final String PERMISSION_READ_ONLY  = "read_only";
    public static final String PERMISSION_READ_WRITE = "read_write";

    private String id;
    private String name;
    private String createdBy;
    private ZonedDateTime createdOn;
    private String profileId;

    // read_only, read_write
    private String permission;

    public MealPlan(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public ZonedDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(ZonedDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}
