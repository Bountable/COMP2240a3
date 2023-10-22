import java.util.ArrayList;
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
        for (ProcessTask task : processTasks) {
            int size = frameCount / processTasks.size();
            task.initializeLocalCache(size);
        }
    }

    /**
     * Checks if a process task has been unblocked and unblocks the task if necessary.
     * 
     * @return True if a task has been unblocked, otherwise false.
     */
    private boolean tryUnblockingTasks() {
        boolean taskUnblocked = false;
        for (ProcessTask task : processTasks) {
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

        printResults();
    }

    /**
     * Prints the results of the scheduling process.
     */
    private void printResults() {
        System.out.println("Clock - Fixed-Local Replacement:");
        System.out.printf("%-5s %-15s %-15s %-15s %-15s%n", "PID", "Task-ID", "Completion-Time", "# Faults", "Fault-Timestamps");

        int maxLineLength = 0;

        for (int i = 0; i < processTasks.size(); i++) {
            ProcessTask task = processTasks.get(i);
            String line = String.format("%-5s %-15s %-15s %-15s %-15s%n", i + 1, task.getTaskId(), task.getCompletionTime(), task.getFaultTimestamps().size(), task.getFaultTimestamps());
            System.out.print(line);
            maxLineLength = Math.max(maxLineLength, line.length());
        }

        for (int i = 0; i < maxLineLength; i++) {
            System.out.print("-");
        }
        System.out.println("\n");
    }
}
