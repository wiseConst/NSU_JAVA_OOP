package StackCalculator;

import Calculator.Calculator;
import Commands.CommandReader;

public class StackCalculator implements Calculator {

    private StackCalculatorContext m_Context;

    public StackCalculator() {
        m_Context = new StackCalculatorContext();
    }

    public StackCalculator(String outputPath) {
        m_Context = new StackCalculatorContext(outputPath);
    }

    @Override
    public void ProcessCommands(CommandReader commandReader) {
        var cmd = commandReader.GetNextCommand();

        while (cmd != null) {
            cmd.Execute(m_Context);

            cmd = commandReader.GetNextCommand();
        }

    }

}

