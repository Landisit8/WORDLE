package server;

public class Configuration {
    private final long timeExchange;
    private final long timeBackup;
    private final int defaultPort;
    private final int RegistryPort;
    private final int UDP_PORT;
    private final String multicastAddress;

    public Configuration() {
        this.timeExchange = 5;       // minuti
        this.timeBackup = 5;     // secondi
        this.defaultPort = 5000;
        this.RegistryPort = 1717;
        this.UDP_PORT = 5001;
        this.multicastAddress = "226.226.226.226";
    }

    public long getTimeExchange() {
        return timeExchange;
    }
    public long getTimeBackup() {
        return timeBackup;
    }

    public int getDefaultPort() {
        return defaultPort;
    }

    public int getRegistryPort() {
        return RegistryPort;
    }

    public int getUDP_PORT() {
        return UDP_PORT;
    }

    public String getMulticastAddress() {
        return multicastAddress;
    }

    //  metodo per settare il file separator
    public String setFileSeparator(String fileName){
        String os = System.getProperty("os.name").toLowerCase();
        String workingDir = System.getProperty("user.dir");
        String absolutePath;
        if (os.contains("win")) {
            absolutePath = workingDir + "\\src\\server\\" + fileName;
        } else if (os.contains("mac")) {
            absolutePath = workingDir + "/src/server/" + fileName;
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            absolutePath = workingDir + "/src/server/" + fileName;
        } else if (os.contains("sunos")) {
            absolutePath = workingDir + "/src/server/" + fileName;
        } else {
            System.out.println("Sistema operativo non riconosciuto");
            absolutePath = workingDir + "/src/server/" + fileName;
        }
        return absolutePath;
    }
}