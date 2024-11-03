/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package watersort_solver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author Karina
 */
public class WaterSort_Solver {
public int numVials;
    public List<State> queue = new ArrayList<>();
    public List<State> visited = new ArrayList<>();
    public List<String> output = new ArrayList<>();
    public List<String> colors = new ArrayList<>();
    
    State starting;
    
    public static void main(String[] args) throws IOException {
        WaterSort_Solver training = new WaterSort_Solver();
        training.readInput();
        
        long startTime = System.currentTimeMillis();
        
        training.execute();
        
        long endTime = System.currentTimeMillis();
        
        System.out.println("Time: "+(endTime - startTime));
        
        training.printOutput();
    }
    
    public void execute()
    {
        queue.add(starting);
        while (!queue.isEmpty())
        {
            State current = queue.get(0);
            visited.add(current);
            queue.remove(0);
            if (isGoalState(current))
            {
                // return the moves needed
                int cost = current.cost;
                int[][] print = new int[cost][2];
                for (int i = cost-1; i > -1; i--)
                {
                    print[i][0] = current.step[0]+1;
                    print[i][1] = current.step[1]+1;
                    current = current.previous;
                }
                
                // print moves
                addToOutput(Integer.toString(cost));
                for (int i = 0; i < cost; i++)
                {
                    addToOutput(Integer.toString(print[i][0])+" -> "+Integer.toString(print[i][1]));
                }
                break;
            }
            else
            {
                addNewStates(current);
                orginizeQueue();
            }
        }
        if (queue.isEmpty())
        {
            addToOutput("No Solution");
        }
        
        addToOutput("");
        addToOutput("Visited "+visited.size()+" States");
    }
    
    public List<Integer> tokenizeInteger(String s)
    {
        StringTokenizer st = new StringTokenizer(s);
        List<Integer> list = new ArrayList<>();
        while (st.hasMoreTokens())
        {
            list.add(Integer.parseInt(st.nextToken()));
        }
        return list;
    }
    
    public List<String> tokenizeString(String s)
    {
        StringTokenizer st = new StringTokenizer(s);
        List<String> list = new ArrayList<>();
        while (st.hasMoreTokens())
        {
            list.add(st.nextToken());
        }
        return list;
    }
    
    // Read test.in into a list of String
    public void readInput() throws IOException
    {
        BufferedReader reader;
        try
        {
            reader = new BufferedReader(new FileReader("WaterFill.in"));
            String line = reader.readLine();
            List<Integer> ints = tokenizeInteger(line);
            numVials = ints.get(0);
            int empty = ints.get(1);
            
            starting = new State();
            for (int i = 0; i < numVials-empty; i++)
            {
                // read next line
                line = reader.readLine();
                List<String> vial = tokenizeString(line);
                
                for (int j = 3; j > -1; j--)
                {
                    if (!colors.contains(vial.get(j)))
                    {
                        colors.add(vial.get(j));
                    }
                    starting.vials[i].push(colors.indexOf(vial.get(j)));
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printOutput() 
    {
        try
        {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("WaterFill.out")));
            for (String s : output)
            {
                System.out.println(s);
                out.println(s);
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void addToOutput(String s)
    {
        output.add(s);
    }
    
    public class Stack {
        Stack() {}
        
        boolean isEmpty()
        {
            return top == 0;
        }
        boolean isComplete()
        {
            if (top == size)
            {
                int temp = items[0];
                for (int i = 1; i < size; i++)
                {
                    if (items[i] != temp)
                    {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
        int allOneColor()
        {
            if (top == 0)
            {
                return 0;
            }
            int color = items[0];
            for (int i = 1; i < top; i++)
            {
                if (items[i] != color)
                {
                    return -1;
                }
            }
            return top;
        }
        
        int push(int n)
        {
            if (top == size)
            {
                // Cannot push
                return -1;
            }
            
            items[top] = n;
            top++;
            return 0;
        }
        int pop()
        {
            if (top == 0)
            {
                return -1;
            }
            top--;
            return items[top];
        }
        int peek()
        {
            if (top == 0)
            {
                return -1;
            }
            return items[top-1];
        }
        boolean isFull()
        {
            return top == size;
        }
        
        boolean equals(Stack compare)
        {
            if (top == compare.top)
            {
                for (int i = 0; i < top; i++)
                {
                    if (items[i] != compare.items[i])
                    {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
        
        void printStack()
        {
            String s = "";
            for (int i = 0; i < top; i++)
            {
                s = s + items[i]+" ";
            }
            System.out.println(s);
        }
        
        void copyStack(Stack c)
        {
            top = c.top;
            for (int i = 0; i < top; i++)
            {
                items[i] = c.items[i];
            }
        }
        
        int[] items = new int[4];
        int size = 4;
        int top = 0; // location of where the next element would go
    }
    
    public class State
    {
        // for the first state
        State ()
        {
            cost = 0;
            vials = new Stack[numVials];
            for (int i = 0; i < numVials; i++)
            {
                vials[i] = new Stack();
            }
        }
        
        // for all other states
        State(int out, int in, State parent)
        {
            step[0] = out;
            step[1] = in;
            previous = parent;
            
            // copy parent
            vials = new Stack[numVials];
            for (int i = 0; i < numVials; i++)
            {
                vials[i] = new Stack();
            }
            for (int i = 0; i < numVials; i++)
            {
                vials[i].copyStack(parent.vials[i]);
            }
            cost = parent.cost + 1;
            
            // make the change in state
            int temp = vials[out].peek();
            
            while (vials[out].peek() == temp && !vials[in].isFull())
            {
                temp = vials[out].pop();
                vials[in].push(temp);
            }
            
            for (int i = 0; i < numVials; i++)
            {
                if (vials[i].isComplete())
                {
                    completement += 10;
                }
                if (vials[i].isEmpty())
                {
                    completement += 5;
                }
                if (vials[i].allOneColor() > 0)
                {
                    completement += vials[i].allOneColor();
                }
            }
            completement = cost - completement;
        }
        
        boolean equals(State compare)
        {
            boolean[] checked = new boolean[numVials];
            for (int i = 0; i < numVials; i++)
            {
                // doesn't matter order, so compare every vial
                int j;
                for (j = 0; j < numVials; j++)
                {
                    if (vials[i].equals(compare.vials[j]) && !checked[j])
                    {
                         checked[j] = true;
                         j = numVials+1;
                    }
                }
                if (j == numVials)
                {
                    return false;
                }
            }
            //System.out.println("States are same");
            return true;
        }
        
        void printState()
        {
            System.out.println("Printing Stack");
            for (int i = 0; i < numVials; i++)
            {
                vials[i].printStack();
            }
        }
        
        Stack[] vials;
        State previous; // the parent state
        int[] step = new int[2]; // [0] is the vial to take from, [1] is the one to put into (how to get from parent to this one)
        int cost; // the number of steps needed
        int completement = 0; // estimate of how complete this state is
    }
    
    boolean isGoalState (State check)
    {
        for (int i = 0; i < numVials; i++)
        {
            if (!check.vials[i].isComplete() && !check.vials[i].isEmpty())
            {
                return false;
            }
        }
        
        return true;
    }
    
    void addNewStates (State current)
    {
        for (int i = 0; i < numVials; i++)
        {
            for (int j = i+1; j < numVials; j++)
            {
                if (current.vials[i].isEmpty())
                {
                    if (!current.vials[j].isEmpty())
                    {
                        //System.out.println("possible "+j+" -> "+i);
                        checkIfAlreadyExplored(new State(j, i, current));
                    }
                }
                else if (current.vials[j].isEmpty())
                {
                    //System.out.println("possible "+i+" -> "+j);
                    checkIfAlreadyExplored(new State(i, j, current));
                }
                else if (current.vials[i].peek() == current.vials[j].peek())
                {
                    if (!current.vials[i].isFull())
                    {
                        //System.out.println("possible "+j+" -> "+i);
                        checkIfAlreadyExplored(new State(j, i, current));
                    }
                    if (!current.vials[j].isFull())
                    {
                        //System.out.println("possible "+i+" -> "+j);
                        checkIfAlreadyExplored(new State(i, j, current));
                    }
                }
            }
        }
    }
    
    int checkIfAlreadyExplored(State check)
    {
        for (int i = 0; i < visited.size(); i++)
        {
            if (check.equals(visited.get(i)))
            {
                // check if the new path to said state is faster, if so, replace the one in the array
                if (check.cost < visited.get(i).cost)
                {
                    visited.get(i).cost = check.cost;
                    visited.get(i).previous = check.previous;
                }
                return 0;
            }
        }
        queue.add(0, check);
        return 1;
    }
    
    void orginizeQueue()
    {
        for (int i = 0; i < queue.size(); i++)
        {
            int maxPos = i;
            for (int j = i+1; j < queue.size(); j++)
            {
                if (queue.get(j).completement < queue.get(maxPos).completement)
                {
                    maxPos = j;
                }
            }
            
            State temp = queue.get(maxPos);
            queue.remove(maxPos);
            queue.add(i, temp);
        }
    }
}
