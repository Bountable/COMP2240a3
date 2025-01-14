
/**
 * @author Darcy 
 * c3404758
 * Date: 23/10/23
 * COMP2240 A3
 */


import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Represents a Fixed Local Clock Scheduler.
 */
public class FixedLocalClockScheduler {
    private int frameCount;
    private int quantum;
    private ArrayList<ProcessTask> processTasks;
    private int currentTimestamp;
    private Queue<ProcessTask> taskQueue;

    /**
     * Constructs a FixedLocalClockScheduler with given frames, quantum, and process tasks.
     * 
     * @param frameCount    The frame count.
     * @param quantum       The quantum.
     * @param processTasks  The list of process tasks.
     */
    public FixedLocalClockScheduler(int frameCount, int quantum, ArrayList<ProcessTask> processTasks) {
        this.frameCount = frameCount;
        this.quantum = quantum;
        this.processTasks = processTasks;
    
        Iterator<ProcessTask> iterator = processTasks.iterator();
    
        while (iterator.hasNext()) {
            ProcessTask task = iterator.next();
            
            int size = frameCount / processTasks.size();
            task.initializeLocalCache(size);
        }
    }

    /**
     * Checks if a process task has been unblocked and unblocks the task if necessary.
     * 
     * @return True if a task has been unblocked, otherwise false.
     * 
     */
    private boolean tryUnblockingTasks() {
        boolean taskUnblocked = false;
        Iterator<ProcessTask> iterator = processTasks.iterator();
        
        while (iterator.hasNext()) {
            ProcessTask task = iterator.next();
            
            if (task.getIsBlocked() && task.getUnblockTimestamp() == currentTimestamp) {
                task.setIsBlocked(false);
                taskQueue.add(task);
                taskUnblocked = true;
            }
        }
        
        return taskUnblocked;
    }

    /**
     * Runs the scheduling algorithm.
     */
    public void execute() {
        currentTimestamp = 0;
        int completedTasks = 0;

        taskQueue = new LinkedList<>();

        for (ProcessTask task : processTasks) {
            int nextInstruction = task.getCurrentInstruction();
            task.addFaultTimestamp(currentTimestamp);
            task.setIsBlocked(true);
            task.setUnblockTimestamp(currentTimestamp + 6);
            task.addInstructionToLocalCache(nextInstruction, currentTimestamp);
        }

        while (completedTasks < processTasks.size()) {
            if (taskQueue.isEmpty()) {
                if (!tryUnblockingTasks()) {
                    currentTimestamp++;
                }
            } else {
                ProcessTask task = taskQueue.poll();
                for (int i = 0; i < quantum; i++) {
                    int nextInstruction = task.getCurrentInstruction();
                    if (!task.hasInstructionInLocalCache(nextInstruction)) {
                        task.addFaultTimestamp(currentTimestamp);
                        task.setIsBlocked(true);
                        task.setUnblockTimestamp(currentTimestamp + 6);
                        task.addInstructionToLocalCache(nextInstruction, currentTimestamp);
                        break;
                    } else {
                        currentTimestamp++;
                        task.proceedToNextInstruction();
                        tryUnblockingTasks();
                        if (task.isFinished()) {
                            completedTasks++;
                            task.setCompletionTime(currentTimestamp);
                            break;
                        }
                    }
                }
                if (!task.isFinished() && !task.getIsBlocked()) {
                    taskQueue.add(task);
                }
            }
        }

    }

    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Clock - Variable-Global Replacement:\n");
        sb.append(padEnd("PID", 5)).append(padEnd("Task-ID", 15)).append(padEnd("Completion-Time", 15)).append(padEnd("# Faults", 15)).append("Fault-Timestamps\n");
        for (int i = 0; i < processTasks.size(); i++) {
            ProcessTask task = processTasks.get(i);
            sb.append(padEnd(String.valueOf(i + 1), 5))
            .append(padEnd(task.getTaskId(), 15))
            .append(padEnd(String.valueOf(task.getCompletionTime()), 15))
            .append(padEnd(String.valueOf(task.getFaultTimestamps().size()), 15))
            .append(task.getFaultTimestamps()).append("\n");
        }

        return sb.toString();
    }

    private String padEnd(String str, int length) {
        if (str.length() >= length) {
            return str;
        }
        return str + " ".repeat(length - str.length());
    }

}
