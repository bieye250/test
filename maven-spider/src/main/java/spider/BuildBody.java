package spider;

public class BuildBody {
    private String taskId;

    private String taskStage;

    private String imageNamespace;

    private String envId;

    public BuildBody(String taskId, String taskStage, String imageNamespace, String envId) {
        this.taskId = taskId;
        this.taskStage = taskStage;
        this.imageNamespace = imageNamespace;
        this.envId = envId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void setEnvId(String envId) {
        this.envId = envId;
    }

    public void setImageNamespace(String imageNamespace) {
        this.imageNamespace = imageNamespace;
    }

    public void setTaskStage(String taskStage) {
        this.taskStage = taskStage;
    }

    public String getEnvId() {
        return envId;
    }

    public String getImageNamespace() {
        return imageNamespace;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getTaskStage() {
        return taskStage;
    }
}
