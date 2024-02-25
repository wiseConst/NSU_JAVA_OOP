package Commands;

import Context.Context;
import Log.Log;
import StackCalculator.StackCalculatorContext;

public class SubtractCommand implements Command {
    public SubtractCommand(String... args) {
    }

    @Override
    public void Execute(Context context) {
        StackCalculatorContext stackCalculatorContext = (StackCalculatorContext) context;
        if (stackCalculatorContext == null) {
            Log.GetLogger().error("Failed to retrieve StackCalculatorContext!");
            throw new RuntimeException();
        }

        final var arg1 = stackCalculatorContext.PopStack();
        final var arg2 = stackCalculatorContext.PopStack();
        stackCalculatorContext.PushOntoStack(arg1 - arg2);
        Log.GetLogger().info("Performed subtract: " + arg1 + " - " + arg2);
    }

}
