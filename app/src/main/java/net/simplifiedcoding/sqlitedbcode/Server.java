package net.simplifiedcoding.sqlitedbcode;

public class Server {
    private String ip;
    private String port;
    private String name;
    private int status;

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

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public int getStatus(){
        return status;
    }

    public void setStatus(int status){
        this.status = status;
    }

    public Server(String name, String ip, String port, int status) {
        super();
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.status = status;
    }

}
