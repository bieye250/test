package spider;

public class Task {
    private Integer taskId;
    private String taskName;

    public Task(Integer taskId, String taskName) {
        this.taskId = taskId;
        this.taskName = taskName;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
}
