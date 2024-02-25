package ru.nsu.fit.galkin;

import Commands.CommandFactory;
import Commands.CommandReader;
import Log.Log;
import StackCalculator.StackCalculator;

public class Main {

    private static final String COMMAND_MAP_PATH = "/command_map.properties";
    private static final String OUTPUT_PATH = "calc_out.txt";

    public static void main(String[] args) {

        try {
            var stackCalculator = new StackCalculator(OUTPUT_PATH);
            CommandReader commandReader;

            if (args.length > 0) commandReader = new CommandReader(args[0]);
            else commandReader = new CommandReader();

            CommandFactory.Init(COMMAND_MAP_PATH);
            stackCalculator.ProcessCommands(commandReader);
        } catch (Exception e) {
        }


    }

}
