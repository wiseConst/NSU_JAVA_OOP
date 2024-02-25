package Commands;

import Context.Context;
import Log.Log;
import StackCalculator.StackCalculatorContext;

public class DefineCommand implements Command {

    private String[] m_Args;

    public DefineCommand(String... args) {
        m_Args = args;
    }

    @Override
    public void Execute(Context context) {
        if (m_Args.length != 2) {
            Log.GetLogger().error("Define command requires only 2 arguments! [Variable name], [value]. ");
            throw new RuntimeException();
        }

        StackCalculatorContext stackCalculatorContext = (StackCalculatorContext) context;
        if (stackCalculatorContext == null) {
            Log.GetLogger().error("Failed to retrieve StackCalculatorContext!");
            throw new RuntimeException();
        }

        final var parsedDouble = Double.parseDouble(m_Args[1]);
        stackCalculatorContext.DefineVariable(m_Args[0], parsedDouble);
        Log.GetLogger().info("Performed definition: " + m_Args[0] + " = " + parsedDouble);
    }
}
