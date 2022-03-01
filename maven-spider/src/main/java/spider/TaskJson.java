package spider;

import java.util.List;

public class TaskJson {
    private List<Task> rows;
    private Integer records;

    public TaskJson(List<Task> rows, Integer records) {
        this.rows = rows;
        this.records = records;
    }

    public Integer getRecords() {
        return records;
    }

    public void setRecords(Integer records) {
        this.records = records;
    }

    public List<Task> getRows() {
        return rows;
    }

    public void setRows(List<Task> rows) {
        this.rows = rows;
    }
}
