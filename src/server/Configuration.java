package server;

public class Configuration {
    private final long timeoutWord;
    private final int defaultPort;
    private final int RegistryPort;

    public Configuration() {
        this.timeoutWord = 5;       // minuti
        this.defaultPort = 5000;
        this.RegistryPort = 1717;
    }

    public long getTimeoutWord() {
        return timeoutWord;
    }

    public int getDefaultPort() {
        return defaultPort;
    }

    public int getRegistryPort() {
        return RegistryPort;
    }

    public String toString() {
        return "server.Configuration{" +
                "timeoutWord=" + timeoutWord +
                ", defaultPort=" + defaultPort +
                ", RegistryPort=" + RegistryPort +
                '}';
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