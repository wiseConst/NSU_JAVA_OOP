package StackCalculator;

import Context.Context;
import Log.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Stack;

public class StackCalculatorContext implements AutoCloseable, Context {

    private HashMap<String, Double> m_DefinedVariables;
    private Stack<Double> m_Stack;

    private FileWriter m_OutputFileWriter;

    public StackCalculatorContext(String outputPath) {
        try {
            File file = new File(outputPath);
            if (!file.exists()) {
                file.createNewFile();
            }

            m_OutputFileWriter = new FileWriter(outputPath);
        } catch (IOException e) {
            Log.GetLogger().error("Failed to open " + outputPath);
            throw new RuntimeException(e);
        }

        m_DefinedVariables = new HashMap<>();
        m_Stack = new Stack<Double>();
    }


    public StackCalculatorContext() {
        m_DefinedVariables = new HashMap<>();
        m_Stack = new Stack<Double>();
    }

    public Double PeekStack() {
        if (m_Stack.empty()) {
            Log.GetLogger().error("Stack is empty!");
            throw new RuntimeException();
        }

        return m_Stack.peek();
    }

    public Double PopStack() {
        if (m_Stack.empty()) {
            Log.GetLogger().error("Stack is empty!");
            throw new RuntimeException();
        }

        return m_Stack.pop();
    }

    public void PushOntoStack(Double value) {
        m_Stack.push(value);
    }

    public Double GetVariable(String variableName) {
        if (!m_DefinedVariables.containsKey(variableName)) {
            Log.GetLogger().error("Not found " + variableName);
            throw new RuntimeException();
        }

        return m_DefinedVariables.get(variableName);
    }


    public void DefineVariable(String variableName, Double value) {
        final String regex = "[0-9]+[.]?[0-9]*";
        if (variableName.matches(regex)) {
            Log.GetLogger().error("Variable name is numeric.");
            throw new RuntimeException();
        }

        if (m_DefinedVariables.containsKey(variableName)) {
            Log.GetLogger().error("Redefinition of " + variableName);
            throw new RuntimeException();
        }

        m_DefinedVariables.put(variableName, value);
    }

    public void PrintPeek() {
        if (m_OutputFileWriter == null) {
            Log.GetLogger().info("Stack peek: " + PeekStack());
        } else {

            try {
                BufferedWriter bufferedWriter = new BufferedWriter(m_OutputFileWriter);
                bufferedWriter.write(PeekStack().toString());
                bufferedWriter.close();
            } catch (IOException e) {

                Log.GetLogger().error("Failed to write something into file!");
                throw new RuntimeException(e);
            }
        }

    }

    @Override
    public void close() throws Exception {
        if (m_OutputFileWriter != null) m_OutputFileWriter.close();

        Log.GetLogger().info("StackCalculatorContext closed!");
    }
}
