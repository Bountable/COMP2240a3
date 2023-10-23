
/**
 * @author Darcy 
 * c3404758
 * Date: 23/10/23
 * COMP2240 A3
 */


import java.util.ArrayList;

/**
 * Represents a task with its set of instructions and the capability to manage its local cache.
 */
public class ProcessTask {

    private String taskId;
    private ArrayList<Integer> instructions;
    private ArrayList<ProcessTask> localCache;
    private int cacheSize;
    private int currentInstructionIndex;
    private ArrayList<Integer> faultTimestamps;
    private boolean isBlocked;
    private int unblockTimestamp;
    private int completionTime;
    private boolean isFinished;

    private String associatedTaskId;
    private int usageCount;
    private int instructionPage;

    /**
     * Default constructor initializing an empty task.
     */
    public ProcessTask() {
        this.taskId = "";
        this.instructions = new ArrayList<>();
        this.currentInstructionIndex = 0;
        this.faultTimestamps = new ArrayList<>();
        this.isFinished = false;
        this.isBlocked = false;
        this.unblockTimestamp = 0;
        this.completionTime = 0;
    }

    /**
     * Constructor that sets task ID and the initial instruction page.
     * @param taskId The ID of the task.
     * @param instructionPage The initial instruction page
     * @
     */
    public ProcessTask(String taskId, int instructionPage) {
        this.associatedTaskId = taskId;
        this.usageCount = 1;
        this.instructionPage = instructionPage;
    }

    /**
     * Resets the task to its initial state.
     */
    public void resetTask() {
        this.currentInstructionIndex = 0;
        this.faultTimestamps = new ArrayList<>();
        this.isFinished = false;
        this.isBlocked = false;
        this.unblockTimestamp = 0;
        this.completionTime = 0;
    }

    /**
     * Adds an instruction to the local cache of the task.
     * @param instruction The instruction to add.
     * @param time The current time (not currently used in the method).
     */
    public void addInstructionToLocalCache(int instruction, int time) {
        if (localCache.size() == cacheSize) {
            boolean replaced = false;
            while (!replaced) {
                for (int i = 0; i < cacheSize; i++) {
                    ProcessTask cacheItem = localCache.get(i);
                    if (cacheItem.getUsageCount() == 0) {
                        localCache.remove(i);
                        localCache.add(new ProcessTask(taskId, instruction));
                        replaced = true;
                        break;
                    } else {
                        cacheItem.resetUsageCount();
                    }
                }
            }
        } else {
            localCache.add(new ProcessTask(taskId, instruction));
        }
    }

    /**
     * Checks if the local cache of the task contains a particular instruction.
     * @param instruction The instruction to check.
     * @return True if the instruction is in the local cache, false otherwise.
     */
    public boolean hasInstructionInLocalCache(int instruction) {
        for (ProcessTask cacheItem : localCache) {
            if (cacheItem.getAssociatedTaskId().equals(taskId) && cacheItem.getInstructionPage() == instruction) {
                return true;
            }
        }
        return false;
    }

    /**
     * Proceeds the task to its next instruction.
     */
    public void proceedToNextInstruction() {
        currentInstructionIndex++;
        if (currentInstructionIndex == instructions.size()) {
            isFinished = true;
        }
    }


    /**
     * Returns the task ID.
     * @return The ID of the task.
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * Sets the task ID.
     * @param taskId The ID to set for the task.
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    /**
     * Returns the list of instructions.
     * @return The list of instructions.
     */
    public ArrayList<Integer> getInstructions() {
        return instructions;
    }

    /**
     * Adds an instruction to the task's list of instructions.
     * @param instruction The instruction to add.
     */
    public void addInstruction(int instruction) {
        this.instructions.add(instruction);
    }

    /**
     * Returns the instruction page of the task.
     * @return The instruction page.
     */
    public int getInstructionPage() {
        return instructionPage;
    }

    /**
     * Sets the instruction page for the task.
     * @param instructionPage The instruction page to set.
     */
    public void setInstructionPage(int instructionPage) {
        this.instructionPage = instructionPage;
    }

    /**
     * Returns the associated task ID.
     * @return The associated task ID.
     */
    public String getAssociatedTaskId() {
        return associatedTaskId;
    }

    /**
     * Sets the associated task ID.
     * @param associatedTaskId The associated task ID to set.
     */
    public void setAssociatedTaskId(String associatedTaskId) {
        this.associatedTaskId = associatedTaskId;
    }

    /**
     * Returns the usage count.
     * @return The usage count.
     */
    public int getUsageCount() {
        return usageCount;
    }

    /**
     * Sets the usage count.
     * @param i The usage count to set.
     */
    public void setUsageCount(int i){
        this.usageCount = i;
    }

    /**
     * Resets the usage count to zero.
     */
    public void resetUsageCount() {
        this.usageCount = 0;
    }

    /**
     * Returns the local cache.
     * @return The local cache.
     */
    public ArrayList<ProcessTask> getLocalCache() {
        return localCache;
    }

    /**
     * Initializes the local cache with a specified size.
     * @param cacheSize The size of the local cache.
     */
    public void initializeLocalCache(int cacheSize) {
        this.cacheSize = cacheSize;
        this.localCache = new ArrayList<>();
    }

    /**
     * Returns the list of fault timestamps.
     * @return The list of fault timestamps.
     */
    public ArrayList<Integer> getFaultTimestamps() {
        return faultTimestamps;
    }

    /**
     * Adds a timestamp to the list of fault timestamps.
     * @param timestamp The timestamp to add.
     */
    public void addFaultTimestamp(int timestamp) {
        faultTimestamps.add(timestamp);
    }

    /**
     * Returns the current instruction.
     * @return The current instruction.
     */
    public int getCurrentInstruction() {
        return instructions.get(currentInstructionIndex);
    }

    /**
     * Checks if the task is finished.
     * @return True if the task is finished, false otherwise.
     */
    public boolean isFinished() {
        return isFinished;
    }

    /**
     * Returns the unblock timestamp.
     * @return The unblock timestamp.
     */
    public int getUnblockTimestamp() {
        return unblockTimestamp;
    }

    /**
     * Sets the unblock timestamp.
     * @param unblockTimestamp The unblock timestamp to set.
     */
    public void setUnblockTimestamp(int unblockTimestamp) {
        this.unblockTimestamp = unblockTimestamp;
    }

    /**
     * Checks if the task is blocked.
     * @return True if the task is blocked, false otherwise.
     */
    public boolean getIsBlocked() {
        return isBlocked;
    }

    /**
     * Sets the task's blocked status.
     * @param isBlocked True to set the task as blocked, false otherwise.
     */
    public void setIsBlocked(boolean isBlocked) {
        this.isBlocked = isBlocked;
    }

    /**
     * Returns the completion time of the task.
     * @return The completion time.
     */
    public int getCompletionTime() {
        return completionTime;
    }

    /**
     * Sets the completion time for the task.
     * @param completionTime The completion time to set.
     */
    public void setCompletionTime(int completionTime) {
        this.completionTime = completionTime;
    }
}



