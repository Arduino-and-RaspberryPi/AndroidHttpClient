package net.simplifiedcoding.sqlitedbcode;

public class Server {
    private String ip;
    private int port;
    private String name;
    private int status;
    private String command;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getStatus(){
        return status;
    }

    public void setStatus(int status){
        this.status = status;
    }

    public String getCommand(){
        return command;
    }

    public void setCommand(String command){
        this.command = command;
    }

    public Server(String name, String ip, int port, int status, String command) {
        super();
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.status = status;
        this.command = command;
    }

}
