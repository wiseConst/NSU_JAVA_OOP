package Commands;


import Context.Context;
import Log.Log;
import StackCalculator.StackCalculatorContext;

public class PushCommand implements Command {

    private String[] m_Args;

    public PushCommand(String... args) {
        m_Args = args;
    }

    @Override
    public void Execute(Context context) {
        if (m_Args.length != 1) {
            Log.GetLogger().error("Push command requires only 1 argument! [Variable name]. ");
            throw new RuntimeException();
        }

        StackCalculatorContext stackCalculatorContext = (StackCalculatorContext) context;
        if (stackCalculatorContext == null) {
            Log.GetLogger().error("Failed to retrieve StackCalculatorContext!");
            throw new RuntimeException();
        }

        final var retrievedVar=stackCalculatorContext.GetVariable(m_Args[0]);
        stackCalculatorContext.PushOntoStack(retrievedVar);
        Log.GetLogger().info("Performed push on stack: " + retrievedVar);
    }

}
