package net.simplifiedcoding.sqlitedbcode;

public class Server {
    String ip;
    String port;
    String name;

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

    public void setPort(String location) {
        this.port = location;
    }

    public Server(String ip, String port, String name) {
        super();
        this.name = ip;
        this.ip = port;
        this.port = name;
    }

}
