package dinnertime.controller;

import dinnertime.db.DinnerTimeDbAccessor;
import dinnertime.model.MealPlan;
import dinnertime.model.MealPlanEntry;
import dinnertime.model.Recipe;
import dinnertime.model.User;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author aaron.mitchell
 */
@RestController
@RequestMapping("/api")
public class DinnerTimeController {
    private static final Logger logger = LoggerFactory.getLogger(DinnerTimeController.class);

    @Autowired
    private DinnerTimeDbAccessor dbAccessor;

    @PostMapping("/login")
    public ResponseEntity<String> login(
        @RequestParam(name="username") String username,
        @RequestParam(name="password") String password 
    ) throws Exception {
        User user = dbAccessor.login(username, password);
        if (user.isLoggedIn())
            return new ResponseEntity<>(user.getAccessToken(), HttpStatus.OK);
        return new ResponseEntity<>(user.getLoginError(), HttpStatus.FORBIDDEN);
    }


    /////////////////////////////////////////////////////////////////////////
    // Accessors
    /////////////////////////////////////////////////////////////////////////
    @GetMapping("/mealplans")
    public ResponseEntity<List<MealPlan>> getMealPlans(
        @RequestParam(name="profile_id") String profileId,
        @RequestParam(name="keywords") List<String> searchKeywords
    ) throws Exception {
        List<MealPlan> mealPlans = dbAccessor.getMealPlans(IdentifiedContext.get());
        return new ResponseEntity<>(mealPlans, HttpStatus.OK);
    }

    @GetMapping("/entries")
    public ResponseEntity<List<MealPlanEntry>> getMealPlanEntries(
        @RequestParam(name="mealplan_id") String mealPlanId,
        @RequestParam(name="fromDate") LocalDate fromDate, 
        @RequestParam(name="toDate") LocalDate toDate 
    ) throws Exception {
        List<MealPlanEntry> entries = dbAccessor.getMealPlanEntries(IdentifiedContext.get(), mealPlanId, fromDate, toDate);
        return new ResponseEntity<>(entries, HttpStatus.OK);
    }

    @GetMapping("/recipes")
    public ResponseEntity<List<Recipe>> searchRecipes(
        @RequestParam(name="profile_id") String profileId,
        @RequestParam(name="keywords") List<String> searchKeywords
    ) throws Exception {
        List<Recipe> recipes = new ArrayList<>(dbAccessor.searchRecipes(null, profileId, searchKeywords));
        return new ResponseEntity<>(recipes, HttpStatus.OK);
    }

    /////////////////////////////////////////////////////////////////////////
    // Mutators
    /////////////////////////////////////////////////////////////////////////

    @PostMapping("/mealplan")
    public ResponseEntity<MealPlan> upsertMealPlan(
        @RequestParam(name="name") String name
    ) throws Exception {
        MealPlan mealPlan = new MealPlan();
        mealPlan.setId("" + UUID.randomUUID());
        mealPlan.setName(name);
        mealPlan.setCreatedBy(IdentifiedContext.get().getId());
        mealPlan.setCreatedOn(ZonedDateTime.now(ZoneId.systemDefault()));
        mealPlan.setProfileId(IdentifiedContext.get().getProfileId());
        dbAccessor.upsertMealPlan(mealPlan);
        return new ResponseEntity<>(mealPlan, HttpStatus.OK);
    }

    @PostMapping("/entry")
    public ResponseEntity<MealPlanEntry> upsertMealPlanEntry(
        @RequestParam(name="mealplan_id") String mealPlanId,
        @RequestParam(name="date") LocalDate mpDate,
        @RequestParam(name="type") String type,
        @RequestParam(name="content") String content,
        @RequestParam(name="notes") String notes,
        @RequestParam(name="category") String category
    ) throws Exception {
        MealPlanEntry entry = new MealPlanEntry();
        entry.setId("" + UUID.randomUUID());
        entry.setMealPlanId(mealPlanId);
        entry.setDate(mpDate);
        entry.setType(type);
        entry.setContent(content);
        entry.setNotes(notes);
        entry.setCategory(category);
        dbAccessor.upsertMealPlanEntry(entry);
        return new ResponseEntity<>(entry, HttpStatus.OK);
    }
}
