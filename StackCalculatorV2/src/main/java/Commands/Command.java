package Commands;


import Context.Context;

public interface Command {

    public default void Execute(Context context) {
    }
}
