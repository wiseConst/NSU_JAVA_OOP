package Commands;


import Context.Context;
import Log.Log;
import StackCalculator.StackCalculatorContext;

public class PopCommand implements Command {

    public PopCommand(String... args) {

    }

    @Override
    public void Execute(Context context) {
        StackCalculatorContext stackCalculatorContext = (StackCalculatorContext) context;
        if (stackCalculatorContext == null) {
            Log.GetLogger().error("Failed to retrieve StackCalculatorContext!");
            throw new RuntimeException();
        }

        stackCalculatorContext.PopStack();
        Log.GetLogger().info("Performed stack pop");
    }
}
