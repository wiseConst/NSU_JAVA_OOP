package Commands;


import Context.Context;
import Log.Log;
import StackCalculator.StackCalculatorContext;

public class PrintCommand implements Command {


    public PrintCommand(String... args) {
    }

    @Override
    public void Execute(Context context) {
        StackCalculatorContext stackCalculatorContext = (StackCalculatorContext) context;
        if (stackCalculatorContext == null) {
            Log.GetLogger().error("Failed to retrieve StackCalculatorContext!");
            throw new RuntimeException();
        }

        stackCalculatorContext.PrintPeek();
        Log.GetLogger().info("Performed print peek");
    }

}
