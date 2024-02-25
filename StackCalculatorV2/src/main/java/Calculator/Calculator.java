package Calculator;

import Commands.CommandReader;

public interface Calculator {


    public default void ProcessCommands(CommandReader commandReader) {
    }

}
