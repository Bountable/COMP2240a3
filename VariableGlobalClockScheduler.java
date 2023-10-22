import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Represents a Variable Global Clock Scheduler.
 */
public class VariableGlobalClockScheduler {
    private int frameCount;
    private int quantum;
    private ArrayList<ProcessTask> processTasks;
    private int currentTimestamp;
    private Queue<ProcessTask> taskQueue;
    private ArrayList<ProcessTask> globalCache;

    /**
     * Constructs a VariableGlobalClockScheduler with given frames, quantum, and process tasks.
     * 
     * @param frameCount    The frame count.
     * @param quantum       The quantum.
     * @param processTasks  The list of process tasks.
     */
    public VariableGlobalClockScheduler(int frameCount, int quantum, ArrayList<ProcessTask> processTasks) {
        this.frameCount = frameCount;
        this.quantum = quantum;
        this.processTasks = processTasks;
        this.globalCache = new ArrayList<>();
        for (ProcessTask task : processTasks) {
            task.resetTask();
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
            addInstructionToGlobalCache(task.getTaskId(), nextInstruction);
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
                    if (!isInstructionInGlobalCache(task.getTaskId(), nextInstruction)) {
                        task.addFaultTimestamp(currentTimestamp);
                        task.setIsBlocked(true);
                        task.setUnblockTimestamp(currentTimestamp + 6);
                        addInstructionToGlobalCache(task.getTaskId(), nextInstruction);
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
        System.out.println("Clock - Variable-Global Replacement:");
        System.out.printf("%-5s %-15s %-15s %-15s %-15s%n", "PID", "Task-ID", "Completion-Time", "# Faults", "Fault-Timestamps");
        for (int i = 0; i < processTasks.size(); i++) {
            ProcessTask task = processTasks.get(i);
            System.out.printf("%-5s %-15s %-15s %-15s %-15s%n", i + 1, task.getTaskId(), task.getCompletionTime(), task.getFaultTimestamps().size(), task.getFaultTimestamps());
        }
    }

    /**
     * Adds a specific instruction to the global cache for a given task.
     * 
     * @param taskId       The ID of the task.
     * @param instruction  The instruction to add.
     */
    private void addInstructionToGlobalCache(String taskId, int instruction) {
        if (globalCache.size() == frameCount) {
            boolean replaced = false;
            while (!replaced) {
                for (int i = 0; i < frameCount; i++) {
                    ProcessTask frameTask = globalCache.get(i);
                    if (frameTask.getUsageCount() == 0) {
                        globalCache.remove(i);
                        ProcessTask newTask = new ProcessTask();
                        newTask.setTaskId(taskId);
                        newTask.setInstructionPage(instruction);
                        globalCache.add(newTask);
                        replaced = true;
                        break;
                    } else {
                        frameTask.setUsageCount(0);
                    }
                }
            }
        } else {
            ProcessTask newTask = new ProcessTask();
            newTask.setTaskId(taskId);
            newTask.setInstructionPage(instruction);
            globalCache.add(newTask);
        }
    }

    /**
     * Checks if a specific instruction is present in the global cache for a given task.
     * 
     * @param taskId       The ID of the task.
     * @param instruction  The instruction to check.
     * @return True if the instruction is present, otherwise false.
     */
    private boolean isInstructionInGlobalCache(String taskId, int instruction) {
        for (ProcessTask frameTask : globalCache) {
            if (frameTask.getTaskId().equals(taskId) && frameTask.getInstructionPage() == instruction) {
                return true;
            }
        }
        return false;
    }
}