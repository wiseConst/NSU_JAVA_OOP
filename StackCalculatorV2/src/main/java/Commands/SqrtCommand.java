package Commands;

import Context.Context;
import Log.Log;
import StackCalculator.StackCalculatorContext;

public class SqrtCommand implements Command {

    public SqrtCommand(String... args) {
    }

    @Override
    public void Execute(Context context) {
        StackCalculatorContext stackCalculatorContext = (StackCalculatorContext) context;
        if (stackCalculatorContext == null) {
            Log.GetLogger().error("Failed to retrieve StackCalculatorContext!");
            throw new RuntimeException();
        }

        final var arg = stackCalculatorContext.PopStack();
        if (arg < 0.0) {
            Log.GetLogger().error("Square root of negative!");
            throw new RuntimeException();
        }

        final var sqRoot = Math.sqrt(arg);
        stackCalculatorContext.PushOntoStack(sqRoot);
        Log.GetLogger().info("Performed square root: " + arg + " = " + sqRoot);
    }

}
