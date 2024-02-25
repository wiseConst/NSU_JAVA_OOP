import Commands.CommandFactory;
import Commands.CommandReader;

import Log.Log;
import StackCalculator.StackCalculator;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculatorTests {

    private static final String COMMAND_MAP_PATH = "/command_map.properties";
    private static final String OUTPUT_PATH = "calc_out.txt";

    @Test
    public void TestSqrt() {
        try {
            var stackCalculator = new StackCalculator(OUTPUT_PATH);
            CommandReader commandReader;

            commandReader = new CommandReader("src/test/resources/test1.txt");

            CommandFactory.Init(COMMAND_MAP_PATH);
            stackCalculator.ProcessCommands(commandReader);
        } catch (Exception e) {
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(OUTPUT_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    double extractedDouble = Double.parseDouble(line);
                    assertEquals(2.0, extractedDouble, 0.000001);
                } catch (NumberFormatException e) {
                    Log.GetLogger().error("Failed read data from output file!" + e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            Log.GetLogger().error("Failed read data from output file!" + e.getMessage());
            throw new RuntimeException(e);
        }

        try (FileWriter writer = new FileWriter(OUTPUT_PATH)) {
            writer.write("");
        } catch (IOException e) {
            Log.GetLogger().error("Failed to clear file!" + e.getMessage());
        }
    }

    @Test
    public void TestMultiplyAdd() {
        try {
            var stackCalculator = new StackCalculator(OUTPUT_PATH);
            CommandReader commandReader;

            commandReader = new CommandReader("src/test/resources/test2.txt");

            CommandFactory.Init(COMMAND_MAP_PATH);
            stackCalculator.ProcessCommands(commandReader);
        } catch (Exception e) {
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(OUTPUT_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    double extractedDouble = Double.parseDouble(line);
                    assertEquals(6.0, extractedDouble, 0.000001);
                } catch (NumberFormatException e) {
                    Log.GetLogger().error("Failed read data from output file!" + e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            Log.GetLogger().error("Failed read data from output file!" + e.getMessage());
            throw new RuntimeException(e);
        }

        try (FileWriter writer = new FileWriter(OUTPUT_PATH)) {
            writer.write("");
        } catch (IOException e) {
            Log.GetLogger().error("Failed to clear file!" + e.getMessage());
        }
    }

    @Test
    public void TestComplexCombined() {
        try {
            var stackCalculator = new StackCalculator(OUTPUT_PATH);
            CommandReader commandReader;

            commandReader = new CommandReader("src/test/resources/test3.txt");

            CommandFactory.Init(COMMAND_MAP_PATH);
            stackCalculator.ProcessCommands(commandReader);
        } catch (Exception e) {
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(OUTPUT_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    double extractedDouble = Double.parseDouble(line);
                    assertEquals(2_592_000.0, extractedDouble, 0.000001);
                } catch (NumberFormatException e) {
                    Log.GetLogger().error("Failed read data from output file!" + e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            Log.GetLogger().error("Failed read data from output file!" + e.getMessage());
            throw new RuntimeException(e);
        }

        try (FileWriter writer = new FileWriter(OUTPUT_PATH)) {
            writer.write("");
        } catch (IOException e) {
            Log.GetLogger().error("Failed to clear file!" + e.getMessage());
        }
    }

}
