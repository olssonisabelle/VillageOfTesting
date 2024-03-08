package org.example;

import org.example.objects.Building;
import org.example.objects.Project;
import org.example.objects.Worker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class VillageTest {

    Village village;

    @BeforeEach
    public void beforeEach() {
        village = new Village();
    }

    // Test: Add one worker with valid occupation.
    @ParameterizedTest
    @CsvSource(value = {"worker1, farmer", "worker2, lumberjack", "worker3, miner", "worker4, builder"})
    public void testAddWorker_validOccupation_Success(String name, String occupation) {
        ArrayList<Worker> workers = village.getWorkers();

        boolean result = village.AddWorker(name, occupation);

        assertTrue(result, "Worker should be added successfully.");
        assertEquals(1, workers.size(), "Worker size should be 1.");
        assertEquals(name, workers.get(workers.size() - 1).getName(), "Name should match with added worker.");
        assertEquals(occupation, workers.get(workers.size() - 1).getOccupation(), "Occupation should match with added worker.");
    }

    // Test: Add one worker with invalid occupation.
    @Test
    public void testAddWorker_InvalidOccupation_Fail() {
        boolean result = village.AddWorker("Worker", "Invalid occupation");
        ArrayList<Worker> workers = village.getWorkers();

        assertFalse(result, "Worker should not be added");
        assertEquals(0, workers.size(), "Worker size should be 0.");
    }

    //Test: Add multiple workers.
    @Test
    public void testAddWorker_Multiple_Success() {
        boolean result1 = village.AddWorker("Worker1", "lumberjack");
        boolean result2 = village.AddWorker("Worker2", "farmer");
        boolean result3 = village.AddWorker("Worker3", "miner");
        boolean result4 = village.AddWorker("Worker4", "builder");
        ArrayList<Worker> workers = village.getWorkers();

        assertTrue(result1, "Worker1 should be added successfully");
        assertTrue(result2, "Worker2 should be added successfully");
        assertTrue(result3, "Worker3 should be added successfully");
        assertTrue(result4, "Worker4 should be added successfully");
        assertEquals(4, workers.size(), "Workers size should be 4.");
        assertEquals("Worker4", workers.get(workers.size() - 1).getName(), "Name should match with the last added worker.");
        assertEquals("builder", workers.get(workers.size() - 1).getOccupation(), "Occupation should match with the last added worker.");
    }

    // Test: Add worker when village is full.
    @Test
    public void testAddWorkers_VillageIsFull_Fail() {
        village.AddWorker("Worker1", "farmer");
        village.AddWorker("Worker2", "builder");
        village.AddWorker("Worker3", "lumberjack");
        village.AddWorker("Worker4", "farmer");
        village.AddWorker("Worker5", "miner");
        village.AddWorker("Worker6", "builder");

        boolean extraWorker = village.AddWorker("Worker7", "builder");
        ArrayList<Worker> workers = village.getWorkers();

        assertFalse(extraWorker, "Worker7 should not be added since village is full.");
        assertTrue(village.isFull(), "Village should be full.");
        assertEquals(6, workers.size(), "Expected size should be 6");
        assertEquals("Worker6", workers.get(workers.size() - 1).getName(), "Name should match with the last added worker.");
        assertEquals("builder", workers.get(workers.size() - 1).getOccupation(), "Occupation should match with the last added worker.");
    }

    // Test: Add valid project with enough resources.
    @ParameterizedTest
    @CsvSource(value = {"House, 5, 0", "Woodmill, 5, 1", "Quarry, 3, 5", "Farm, 5, 2", "Castle, 50, 50"})
    public void testAddProject_SufficientResources_Success(String name, int woodCost, int metalCost) {
        village.setWood(woodCost);
        village.setMetal(metalCost);

        boolean result = village.AddProject(name);
        ArrayList<Project> projects = village.getProjects();

        assertTrue(result, "Project should be added successfully.");
        assertEquals(1, projects.size(), "Project size should be 1.");
        assertEquals(name, projects.get(projects.size() - 1).getName(), "Name should match with added project.");
    }

    // Test: Add valid project with not enough resources.
    @ParameterizedTest
    @CsvSource({"House, 4, 0", "Woodmill, 4, 1", "Quarry, 2, 5", "Farm, 5, 1", "Castle, 40, 10"})
    public void testAddProject_InsufficientResources_Fail(String name, int woodCost, int metalCost) {
        village.setWood(woodCost);
        village.setMetal(metalCost);

        boolean result = village.AddProject(name);
        ArrayList<Project> projects = village.getProjects();

        assertFalse(result, "Project should not be added due to insufficient resources.");
        assertEquals(0, projects.size(), "Project size should be 0.");
    }

    // Test: Adding a project in the beginning of the application with no workers and not enough resources.
    @Test
    public void testAddProject_WithoutResources_Fail() {
        boolean result = village.AddProject("House");
        ArrayList<Project> projects = village.getProjects();

        assertFalse(result, "Project should not be added due to insufficient resources");
        assertEquals(0, projects.size(), "Project size should be 0.");
    }

    // Test: Add a non-existing project
    @Test
    public void testAddProject_NonExisting_Fail() {
        boolean result = village.AddProject("Non-existing project");
        ArrayList<Project> projects = village.getProjects();

        assertFalse(result, "Project do not exist and should not be added.");
        assertEquals(0, projects.size(), "Project size should be 0.");
    }

    // Test: Day progression without workers, should leave the food supply unchanged.
    @Test
    public void testDayProgression_NoWorkers_FoodSupplyUnchanged() {
        village.Day();

        assertEquals(10, village.getFood(), "Food should remain unchanged after one day with no workers.");
    }

    // Test: Day progression without workers, should leave the metal supply unchanged.
    @Test
    public void testDayProgression_NoWorkers_MetalSupplyUnchanged() {
        village.Day();

        assertEquals(0, village.getMetal(), "Metal should remain unchanged after one day with no workers.");
    }

    // Test: Day progression without workers, should leave the wood supply unchanged.
    @Test
    public void testDayProgression_NoWorkers_WoodSupplyUnchanged() {
        village.Day();

        assertEquals(0, village.getWood(), "Wood should remain unchanged after one day with no workers.");
    }

    // Test: Day progression with workers and no food, worker should be hungry.
    @Test
    public void testDayProgression_WorkersWithNoFood_ShouldBeHungry() {
        village.AddWorker("Worker", "farmer");
        ArrayList<Worker> workers = village.getWorkers();

        village.setFood(0);
        village.Day();

        assertTrue(workers.get(0).isHungry(), "Worker should be hungry when there is no food.");
    }

    // Test: Day progression with workers and no food for too long, should be game over.
    @Test
    public void testDayProgression_WorkersWithNoFood_ShouldBeGameOver() {
        village.AddWorker("Worker1", "builder");
        village.AddWorker("Worker2", "miner");
        village.AddWorker("Worker3", "farmer");
        village.AddWorker("Worker4", "lumberjack");
        ArrayList<Worker> workers = village.getWorkers();

        village.setFood(0);
        village.setFoodPerDay(0);
        village.Day();
        village.Day();
        village.Day();
        village.Day();
        village.Day();
        village.Day();

        assertFalse(workers.get(0).isAlive(), "Worker should be ded.");
        assertFalse(workers.get(1).isAlive(), "Worker should be ded.");
        assertFalse(workers.get(2).isAlive(), "Worker should be ded.");
        assertFalse(workers.get(3).isAlive(), "Worker should be ded.");
        assertTrue(village.isGameOver(), "Game should be over since all workers are dead");
    }

    // Test: Day progression with workers and enough food.
    @Test
    public void testDayProgression_WorkerWithFood_FoodShouldBe14() {
        village.AddWorker("Worker1", "farmer");

        village.Day();

        assertEquals(14, village.getFood(), "Food should be 14 after one day with one worker.");
    }

    // Test: Finish project House and make sure we have 2 extra workers.
    @Test
    public void testNewHouse_ProjectFinished() {
        village.AddWorker("Lumberjack", "lumberjack");
        village.AddWorker("Builder", "builder");
        village.AddWorker("Farmer", "farmer");

        village.setWood(5);

        village.AddProject("House");
        village.Day();
        village.Day();
        village.Day();

        ArrayList<Building> buildings = village.getBuildings();

        assertEquals("House", buildings.get(3).getName(), "Assuming house is the 4th building finished.");
        assertEquals(8, village.getMaxWorkers(), "Should have 2 extra workers slots after building a new house.");
    }

    //Test: Finish project Woodmill and make sure wood per day per worker increase.
    @Test
    public void testNewWoodmill_ProjectFinished() {
        village.AddWorker("Lumberjack", "lumberjack");
        village.AddWorker("Builder", "builder");

        village.setWood(5);
        village.setMetal(1);

        village.AddProject("Woodmill");

        village.Day();
        village.Day();
        village.Day();
        village.Day();
        village.Day();

        ArrayList<Building> buildings = village.getBuildings();

        assertEquals("Woodmill", buildings.get(3).getName(), "Assuming woodmill is the 4th building finished.");
        assertEquals(2, village.getWoodPerDay(), "Should increase wood per day per worker after building a new woodmill");
    }

    //Test: Finish project Quarry and make sure metal per day per worker increase.
    @Test
    public void testNewQuarry_BuildingFinished() {
        village.AddWorker("Miner", "miner");
        village.AddWorker("Builder", "builder");
        village.AddWorker("Farmer", "farmer");

        village.setWood(3);
        village.setMetal(5);

        village.AddProject("Quarry");

        village.Day();
        village.Day();
        village.Day();
        village.Day();
        village.Day();
        village.Day();
        village.Day();

        ArrayList<Building> buildings = village.getBuildings();

        assertEquals("Quarry", buildings.get(3).getName(), "Assuming Quarry is the 4th building finished.");
        assertEquals(2, village.getMetalPerDay(), "Should increase metal per day per worker after building a new quarry");
    }

    // Test: Finish project Farm and make sure food per day increase.
    @Test
    public void testNewFarm_BuildingFinished() {
        village.AddWorker("Farmer", "farmer");
        village.AddWorker("Builder", "builder");

        village.setWood(5);
        village.setMetal(2);

        village.AddProject("Farm");

        village.Day();
        village.Day();
        village.Day();
        village.Day();
        village.Day();

        ArrayList<Building> buildings = village.getBuildings();

        assertEquals("Farm", buildings.get(3).getName(), "Assuming farm is the 4th building finished.");
        assertEquals(10, village.getFoodPerDay(), "Should increase food per day per worker after building a new farm");
    }

    // Test: Full game scenario, complete project Castle.
    @Test
    public void testFullGameScenario_CastleComplete() {
        village.AddWorker("Worker1", "builder");
        village.AddWorker("Worker2", "farmer");
        village.AddWorker("Worker3", "miner");
        village.AddWorker("Worker4", "lumberjack");
        village.AddWorker("Worker5", "builder");
        village.setWood(50);
        village.setMetal(50);
        village.AddProject("Castle");

        while (!village.isGameOver()) {
            village.Day();
        }

        ArrayList<Building> buildings = village.getBuildings();

        assertTrue(village.isGameOver());
        assertEquals("Castle", buildings.get(3).getName(), "Assuming Castle is the 4th building finished.");
    }

    // Test: Make sure PrintInfo method working correctly after adding worker and project.
    @Test
    public void testPrintInfo_WithWorkersAndBuildings() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outContent);
        System.setOut(printStream);

        village.AddWorker("Worker1", "builder");
        village.AddWorker("Worker2", "lumberjack");
        village.setWood(5);
        village.AddProject("House");

        village.PrintInfo();

        String expectedOutput =
                "Worker1 was successfully added." + System.lineSeparator() +
                        "Worker2 was successfully added." + System.lineSeparator() +
                        "House added to the project queue!" + System.lineSeparator() +
                        "You have 2 workers. They are: " + System.lineSeparator() +
                        "Worker1, builder." + System.lineSeparator() +
                        "Worker2, lumberjack." + System.lineSeparator() +
                        "Your current buildings are: " + System.lineSeparator() +
                        "House House House " + System.lineSeparator() +
                        "You can have 6 workers." + System.lineSeparator() +
                        "Your current projects are: " + System.lineSeparator() +
                        "House, 3 points left until completion." + System.lineSeparator() +
                        "Current Food:  10" + System.lineSeparator() +
                        "Current Wood:  0" + System.lineSeparator() +
                        "Current Metal: 0" + System.lineSeparator() +
                        "Generating 5 food per day per worker." + System.lineSeparator() +
                        "Generating 1 wood per day per worker." + System.lineSeparator() +
                        "Generating 1 metal per day per worker." + System.lineSeparator();

        assertEquals(expectedOutput, outContent.toString());

        System.setOut(System.out);
    }
}