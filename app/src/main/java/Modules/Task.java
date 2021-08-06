package Modules;

public class Task {
    public String task, url, name;

    public  Task(){};



    public Task(String task, String url){
        this.task = task;
        this.url = url;
        this.name = name;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTask() {
        return task;
    }

    public String getUrl() {
        return url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
