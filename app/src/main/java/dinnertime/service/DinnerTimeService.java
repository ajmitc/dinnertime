package dinnertime.service;

import dinnertime.db.DinnerTimeDbAccessor;
import dinnertime.gdrive.GoogleDriveApi;
import dinnertime.model.Recipe;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 *
 * @author aaron.mitchell
 */
@Service
public class DinnerTimeService {
    private static final Logger logger = LoggerFactory.getLogger(DinnerTimeService.class);

    @Autowired
    private GoogleDriveApi googleDriveApi;

    @Autowired
    private DinnerTimeDbAccessor dbAccessor;

    @Scheduled(cron="${google-drive.sync.cron:0 0 * * * ?}") // Once a day at midnight
    public void timerSync(){
        syncGoogleDrive();
    }
    
    public void syncGoogleDrive(){
        logger.info("Syncing with Google Drive");

        List<Recipe> gRecipes = null;
        try {
            gRecipes = googleDriveApi.getRecipes();
        }
        catch(Exception e){
            logger.error("Caught exception while retrieving Google Drive recipes", e);
            return;
        }
        logger.info("Found {} recipes in Google Drive", gRecipes.size());

        // these recipes are not yet paired up with the recipes in the database
        // do that now
        List<Recipe> allRecipes = dbAccessor.getAllRecipes();
        Map<String, Recipe> dbRecipeMap = 
                allRecipes.stream()
                    .filter(r -> r.getDriveId() != null && !r.getDriveId().isEmpty())
                    .collect(Collectors.toMap(r -> r.getDriveId(), Function.identity()));
        logger.info("Found {} recipes in database", dbRecipeMap.size());

        List<Recipe> createGRecipes = new ArrayList<>(); // gd recipes to add to db
        List<Recipe> updateGRecipes = new ArrayList<>(); // gd recipes to update
        List<Recipe> removeGRecipes = new ArrayList<>(); // db recipes to remove from db
        Set<String> seenDriveIds = new HashSet<>();
        for (Recipe gRecipe: gRecipes){
            if (dbRecipeMap.containsKey(gRecipe.getDriveId())){
                // Recipe in both google drive and DB
                // TODO Check if recipe updated
            }
            else {
                // Recipe in google drive, but not DB
                createGRecipes.add(gRecipe);
            }
            seenDriveIds.add(gRecipe.getDriveId());
        }

        for (Recipe dbRecipe: dbRecipeMap.values()){
            if (!seenDriveIds.contains(dbRecipe.getDriveId())){
                removeGRecipes.add(dbRecipe);
            }
        }

        logger.info("Creating {} recipes in database", createGRecipes.size());
        for (Recipe gRecipe: createGRecipes){
            // Create a DB Recipe
            gRecipe.setId("" + UUID.randomUUID());
            dbAccessor.upsertRecipe(gRecipe, null);
        }

        logger.info("Updating {} recipes in database", updateGRecipes.size());
        for (Recipe gRecipe: updateGRecipes){
            // TODO Diff and Update this drive Recipe in the DB
        }

        logger.info("Removing {} recipes from database", removeGRecipes.size());
        for (Recipe dbRecipe: removeGRecipes){
            // Remove this Recipe from the DB
            dbAccessor.deleteRecipe(dbRecipe.getId());
        }

        logger.info("Sync Complete");
    }
}
