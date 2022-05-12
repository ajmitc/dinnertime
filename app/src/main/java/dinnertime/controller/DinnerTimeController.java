package dinnertime.controller;

import dinnertime.db.DinnerTimeDbAccessor;
import dinnertime.model.MealPlan;
import dinnertime.model.MealPlanEntry;
import dinnertime.model.Recipe;
import dinnertime.model.User;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
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
    ) throws Exception {
        List<MealPlan> mealPlans = dbAccessor.getMealPlans(IdentifiedContext.get());
        mealPlans.sort(new Comparator<MealPlan>(){
            @Override
            public int compare(MealPlan t, MealPlan t1) {
                return t.getCreatedOn().compareTo(t1.getCreatedOn());
            }
        });
        return new ResponseEntity<>(mealPlans, HttpStatus.OK);
    }

    @GetMapping("/mealplan-dates")
    public ResponseEntity<List<String>> getMealPlansDates(
        @RequestParam(name="mealplan_id") String mealPlanId
    ) throws Exception {
        DayOfWeek weekStart = DayOfWeek.MONDAY;
        List<LocalDate> mealPlanDates = dbAccessor.getDistinctMealPlanEntryDates(IdentifiedContext.get(), mealPlanId);
        Collections.reverse(mealPlanDates);
        // TODO If first and last dates don't end on weekStart, add weekStart dates
        List<String> dates = mealPlanDates.stream()
                .filter(ld -> ld.getDayOfWeek() == weekStart)
                .map(ld -> ld.format(DateTimeFormatter.ISO_DATE))
                .collect(Collectors.toList());
        return new ResponseEntity<>(dates, HttpStatus.OK);
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

    /**
     * Return the MealPlanEntries in this format:
     * {
     *      days: [
     *          {
     *              date: "2022-05-09",
     *              dayOfWeek: "Monday",
     *              entries: {
     *                  "default": [  // category
     *                      {
     *                          id: "1234567890",
     *                          mealPlanId: "1",
     *                          type: "text",
     *                          content: "Leftovers",
     *                          notes: "This is a note",
     *                          category: "",
     *                          date: "2022-05-09"
     *                     }
     *                  ]
     *              }
     *          }
     *      ]
     * }
     * @param mealPlanId
     * @param fromDate
     * @param toDate
     * @return
     * @throws Exception 
     */
    @GetMapping("/weekly-entries")
    public ResponseEntity<WeeklyMealPlanDays> getWeeklyMealPlanEntries(
        @RequestParam(name="mealplan_id") String mealPlanId,
        @RequestParam(name="date") LocalDate date 
    ) throws Exception {
        List<MealPlanEntry> entries = dbAccessor.getMealPlanEntries(IdentifiedContext.get(), mealPlanId, date, date.plus(6, ChronoUnit.DAYS));
        WeeklyMealPlanDays ret = new WeeklyMealPlanDays();
        entries.stream().forEach(entry -> {
            WeeklyMealPlanDay day = null;
            for (WeeklyMealPlanDay wmpd: ret.getDays()){
                if (wmpd.getDate().isEqual(entry.getDate())){
                    day = wmpd;
                    break;
                }
            }
            if (day == null){
                day = new WeeklyMealPlanDay(entry.getDate(), entry.getDate().getDayOfWeek().toString());
            }
            day.getEntries().computeIfAbsent(entry.getCategory(), new ArrayList<>());
            day.getEntries().get(entry.getCategory()).add(entry);
        });
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    private class WeeklyMealPlanDays{
        private List<WeeklyMealPlanDay> days = new ArrayList<>();

        public List<WeeklyMealPlanDay> getDays() {
            return days;
        }
    }

    private class WeeklyMealPlanDay{
        private LocalDate date;
        private String dayOfWeek;
        private Map<String, List<MealPlanEntry>> entries = new HashMap<>();

        public WeeklyMealPlanDay(LocalDate date, String dayOfWeek){
            this.date = date;
            this.dayOfWeek = dayOfWeek;
        }

        public LocalDate getDate() {
            return date;
        }

        public String getDayOfWeek() {
            return dayOfWeek;
        }

        public Map<String, List<MealPlanEntry>> getEntries() {
            return entries;
        }
    }

    /**
     * Get recipes from the database
     * @param profileId
     * @param searchKeywords
     * @return
     * @throws Exception 
     */
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
