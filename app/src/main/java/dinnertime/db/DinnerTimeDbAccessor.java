package dinnertime.db;

import dinnertime.model.User;
import dinnertime.model.MealPlan;
import dinnertime.model.MealPlanEntry;
import dinnertime.model.Recipe;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author aaron.mitchell
 */
public class DinnerTimeDbAccessor {
    private JdbcTemplate jdbcTemplate;

    @Value("${access.token.expiration.days:7}")
    private int accessTokenExpirationDurationDays;

    public DinnerTimeDbAccessor(){

    }

    /**
     * RowMapper implementation to build User objects
     */
    private class UserRowMapper implements RowMapper<User>{
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getString("id"));
            user.setUsername(rs.getString("username"));
            user.setAccessToken(rs.getString("access_token"));
            user.setAccessTokenExpiration(ZonedDateTime.parse(rs.getString("access_token_expiration")));
            user.setLoggedIn(true);
            return user;
        }
    }

    /**
     * Attempt to login the user
     * 
     * @param username
     * @param password
     * @return User object.  Check isLoggedIn() and getLoginError to determine if login was successful.
     */
    public User login(String username, String password){
        List<User> users = 
            jdbcTemplate.query("select * from user_profile where username=? and password=?", new UserRowMapper(), username, password);
        if (users.isEmpty()){
            return User.getLoginFailure("Username or password is incorrect");
        }
        // Set access token
        users.get(0).setAccessToken("" + UUID.randomUUID());
        users.get(0).setAccessTokenExpiration(ZonedDateTime.now(ZoneId.systemDefault()).plus(accessTokenExpirationDurationDays, ChronoUnit.DAYS));
        upsertUser(users.get(0));
        return users.get(0);
    }

    /**
     * 
     * @param accessToken
     * @return 
     */
    public User validateToken(String accessToken){
        List<User> users = 
                jdbcTemplate.query("select * from user_profile where access_token=?", new UserRowMapper(), accessToken);
        if (users.isEmpty()){
            return User.getLoginFailure("User must login");
        }
        if (users.get(0).getAccessTokenExpiration().isAfter(ZonedDateTime.now(ZoneId.systemDefault()))){
            upsertUser(users.get(0));
            return User.getLoginFailure("User must re-authenticate");
        }
        return users.get(0);
    }

    /**
     * 
     * @param user 
     */
    public void upsertUser(User user){
        String sql = "insert into user_profile(id, username, password, profile_id, access_token, access_token_expiration) " +
                "values (?, ?, ?, ?, ?, ?) " +
                "on conflict (id) do update set " +
                    "username=EXCLUDED.username, " +
                    "password=EXCLUDED.password, " +
                    "access_token=EXCLUDED.access_token, " +
                    "access_token_expiration=EXCLUDED.access_token_expiration";
        Object[] args = new Object[]{
            user.getId(),
            user.getUsername(),
            user.getPassword(),
            user.getProfileId(),
            user.getAccessToken(),
            user.getAccessTokenExpiration()
        };
        int[] argTypes = new int[]{
            java.sql.Types.VARCHAR,
            java.sql.Types.VARCHAR,
            java.sql.Types.VARCHAR,
            java.sql.Types.VARCHAR,
            java.sql.Types.VARCHAR,
            java.sql.Types.TIMESTAMP_WITH_TIMEZONE
        };
        jdbcTemplate.update(sql, args, argTypes);
    }

    /**
     * RowMapper implementation to build MealPlan objects
     */
    private class MealPlanRowMapper implements RowMapper<MealPlan>{
        @Override
        public MealPlan mapRow(ResultSet rs, int rowNum) throws SQLException {
                MealPlan mealPlan = new MealPlan();
                mealPlan.setId(rs.getString("id"));
                mealPlan.setName(rs.getString("name"));
                mealPlan.setCreatedBy(rs.getString("created_by"));
                mealPlan.setCreatedOn(ZonedDateTime.parse(rs.getString("created_on")));
                mealPlan.setProfileId(rs.getString("profile_id"));
                return mealPlan;
        }
    }

    /**
     * Get the meal plans owned by this user and shared with this user
     * @param user
     * @return 
     */
    public List<MealPlan> getMealPlans(User user){
        // Get this user's meal plans
        String query = "select mp.id, mp.created_by, mp.created_on, mp.profile_id, mp.name, 'read_write' from mealplan mp where profile_id=?";
        MealPlanRowMapper mapper = new MealPlanRowMapper();
        List<MealPlan> mealPlans = 
                jdbcTemplate.query(query, mapper, user.getProfileId());

        // Add shared meal plans
        query = "select mp.id, mp.created_by, mp.created_on, mp.profile_id, mp.name, sp.permission " +
                "from mealplan mp, shared_profile sp " +
                "where sp.user_id = ? and mp.profile_id = sp.profile_id";
        mapper = new MealPlanRowMapper();
        mealPlans.addAll(jdbcTemplate.query(query, mapper, user.getId()));

        return mealPlans;
    }

    /**
     * Insert/update a MealPlan
     * @param mealPlan 
     */
    public void upsertMealPlan(MealPlan mealPlan){
        String sql = "insert into mealplan(id, created_by, created_on, profile_id, name) " +
                "values (?, ?, ?, ?, ?) " +
                "on conflict (id) do update set " +
                    "name=EXCLUDED.name";
        Object[] args = new Object[]{
            mealPlan.getId(),
            mealPlan.getCreatedBy(),
            mealPlan.getCreatedOn(),
            mealPlan.getProfileId(),
            mealPlan.getName()
        };
        int[] argTypes = new int[]{
            java.sql.Types.VARCHAR,
            java.sql.Types.VARCHAR,
            java.sql.Types.TIMESTAMP_WITH_TIMEZONE,
            java.sql.Types.VARCHAR,
            java.sql.Types.VARCHAR
        };
        jdbcTemplate.update(sql, args, argTypes);
    }

    /**
     * Get the meal plan entries for the given user, meal plan, and date
     * @param user
     * @param mealPlanId
     * @param date
     * @return 
     */
    public List<MealPlanEntry> getMealPlanEntries(User user, String mealPlanId, LocalDate date){
        return getMealPlanEntries(user, mealPlanId, date, date);
    }

    /**
     * Get the meal plan entries for the given user, meal plan, and between the given dates (inclusive)
     * @param user
     * @param mealPlanId
     * @param fromDate
     * @param toDate
     * @return 
     */
    public List<MealPlanEntry> getMealPlanEntries(User user, String mealPlanId, LocalDate fromDate, LocalDate toDate){
        List<MealPlanEntry> mealPlanEntries = 
            jdbcTemplate.query("select * from mealplanentry where mealplan_id=? and mpdate >= ? and mpdate <= ?", new RowMapper<MealPlanEntry>(){
                @Override
                public MealPlanEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
                    MealPlanEntry entry = new MealPlanEntry();
                    entry.setId(rs.getString("id"));
                    entry.setMealPlanId(mealPlanId);
                    entry.setType(rs.getString("type"));
                    entry.setContent(rs.getString("content"));
                    entry.setNotes(rs.getString("notes"));
                    entry.setCategory(rs.getString("category"));
                    entry.setDate(LocalDate.parse(rs.getString("mpdate")));
                    return entry;
                }
            }, mealPlanId, fromDate, toDate);
        return mealPlanEntries;
    }

    /**
     * 
     * @param mealPlanEntry 
     */
    public void upsertMealPlanEntry(MealPlanEntry mealPlanEntry){
        String sql = "insert into mealplanentry(id, mealplan_id, mpdate, type, content, notes, category) " +
                "values (?, ?, ?, ?, ?, ?, ?) " +
                "on conflict (id) do update set " +
                    "mealplan_id=EXCLUDED.mealplan_id, " +
                    "mpdate=EXCLUDED.mpdate, " +
                    "type=EXCLUDED.type, " +
                    "content=EXCLUDED.content, " +
                    "notes=EXCLUDED.notes, " +
                    "category=EXCLUDED.category";
        Object[] args = new Object[]{
            mealPlanEntry.getId(),
            mealPlanEntry.getMealPlanId(),
            mealPlanEntry.getDate(),
            mealPlanEntry.getType(),
            mealPlanEntry.getContent(),
            mealPlanEntry.getNotes(),
            mealPlanEntry.getCategory()
        };
        int[] argTypes = new int[]{
            java.sql.Types.VARCHAR,
            java.sql.Types.VARCHAR,
            java.sql.Types.TIMESTAMP_WITH_TIMEZONE,
            java.sql.Types.VARCHAR,
            java.sql.Types.VARCHAR,
            java.sql.Types.VARCHAR,
            java.sql.Types.VARCHAR
        };
        jdbcTemplate.update(sql, args, argTypes);
    }

    private class RecipeRowMapper implements RowMapper<Recipe>{
        @Override
        public Recipe mapRow(ResultSet rs, int rowNum) throws SQLException {
            Recipe recipe = new Recipe();
            recipe.setId(rs.getString("id"));
            recipe.setDriveId(rs.getString("drive_id"));
            recipe.setUrl(rs.getString("url"));
            recipe.setRating(rs.getDouble("rating"));
            return recipe;
        }
    }

    public List<Recipe> getAllRecipes(){
        List<Recipe> recipes = 
            jdbcTemplate.query("select * from recipe", new RecipeRowMapper());
        return recipes;
    }

    /**
     * Return the recipes identified by the recipe ids
     * @param user
     * @param profileId
     * @param recipeIds
     * @return 
     */
    public Collection<Recipe> getRecipes(User user, String profileId, List<String> recipeIds){
        Map<String, Recipe> recipes = new HashMap<>();
        jdbcTemplate.query("select * from recipe where id in (?)", new RowCallbackHandler(){
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Recipe recipe = new Recipe();
                recipe.setId(rs.getString("id"));
                recipe.setDriveId(rs.getString("drive_id"));
                recipe.setUrl(rs.getString("url"));
                recipe.setRating(rs.getDouble("rating"));
                recipes.put(recipe.getId(), recipe);
            }
        }, recipeIds);

        // Link labels
        jdbcTemplate.query("select * from recipe_label where recipe_id in (?)", new RowCallbackHandler(){
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Recipe recipe = recipes.get(rs.getString("recipe_id"));
                recipe.getLabels().add(rs.getString("label"));
            }
        }, recipeIds);
        
        // Link profile info
        jdbcTemplate.query("select * from profile_recipe where recipe_id in (?) and profile_id=?", new RowCallbackHandler(){
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Recipe recipe = recipes.get(rs.getString("recipe_id"));
                recipe.setFavorite(rs.getBoolean("favorite"));
                recipe.setNotes(rs.getString("notes"));
            }
        }, recipeIds, profileId);


        return recipes.values();
    }

    /**
     * Search for recipes by keyword
     * @param user
     * @param profileId
     * @param searchKeywords
     * @return 
     */
    public Collection<Recipe> searchRecipes(User user, String profileId, List<String> searchKeywords){
        List<String> recipeIds = 
            jdbcTemplate.query("select recipe_id from recipe_label where label LIKE (?)", new RowMapper<String>(){
                @Override
                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getString("recipe_id");
                }
            }, searchKeywords);

        return getRecipes(user, profileId, recipeIds);
    }

    public void upsertRecipe(Recipe recipe, String profileId){
        String sql = "insert into recipe(id, drive_id, url, rating) " +
                "values (?, ?, ?, ?) " +
                "on conflict (id) do update set " +
                    "drive_id=EXCLUDED.drive_id, " +
                    "url=EXCLUDED.url, " +
                    "rating=EXCLUDED.rating";
        Object[] args = new Object[]{
            recipe.getId(),
            recipe.getDriveId(),
            recipe.getUrl(),
            recipe.getRating()
        };
        int[] argTypes = new int[]{
            java.sql.Types.VARCHAR,
            java.sql.Types.VARCHAR,
            java.sql.Types.VARCHAR,
            java.sql.Types.NUMERIC
        };
        jdbcTemplate.update(sql, args, argTypes);


        List<Object[]> batchArgs = new ArrayList<>();
        sql = "insert into recipe_label(recipe_id, label) " +
                "values (?, ?) " +
                "on conflict (recipe_id, label) do nothing";
        for (String label: recipe.getLabels()){
            args = new Object[]{
                recipe.getId(),
                label
            };
            batchArgs.add(args);
        }
        argTypes = new int[]{
            java.sql.Types.VARCHAR,
            java.sql.Types.VARCHAR
        };
        jdbcTemplate.batchUpdate(sql, batchArgs, argTypes);


        if (profileId != null){
            sql = "insert into profile_recipe(recipe_id, profile_id, favorite, notes) " +
                    "values (?, ?, ?, ?) " +
                    "on conflict (recipe_id, profile_id) do update set " +
                    "favorite=EXCLUDED.favorite, " +
                    "notes=EXCLUDED.notes";
            args = new Object[]{
                recipe.getId(),
                profileId,
                recipe.isFavorite(),
                recipe.getNotes()
            };
            argTypes = new int[]{
                java.sql.Types.VARCHAR,
                java.sql.Types.VARCHAR,
                java.sql.Types.BOOLEAN,
                java.sql.Types.VARCHAR
            };
            jdbcTemplate.update(sql, args, argTypes);
        }
    }

    public void deleteRecipe(String recipeId){
        String sql = "delete from recipe where id=?";
        Object[] args = new Object[]{
            recipeId
        };
        int[] argTypes = new int[]{
            java.sql.Types.VARCHAR
        };
        jdbcTemplate.update(sql, args, argTypes);
    }




    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
