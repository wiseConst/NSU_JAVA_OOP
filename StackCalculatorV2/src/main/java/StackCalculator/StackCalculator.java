package StackCalculator;

import Calculator.Calculator;
import Commands.CommandReader;
import Log.Log;

public class StackCalculator implements Calculator, AutoCloseable {

    private StackCalculatorContext m_Context = null;

    public StackCalculator() {
        m_Context = new StackCalculatorContext();
    }

    public StackCalculator(String outputPath) {
        m_Context = new StackCalculatorContext(outputPath);
        Log.GetLogger().info("");
        Log.GetLogger().info("StackCalculator created.");
    }

    @Override
    public void ProcessCommands(CommandReader commandReader) {
        var cmd = commandReader.GetNextCommand();

        while (cmd != null) {
            cmd.Execute(m_Context);

            cmd = commandReader.GetNextCommand();
        }

    }

    @Override
    public void close() throws Exception {
        m_Context.close();
        Log.GetLogger().info("StackCalculator closed!");
    }
}

