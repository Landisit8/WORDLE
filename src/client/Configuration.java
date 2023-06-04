package client;

public class Configuration {
    private final int defaultPort;
    private final int registryPort;
    private final String hostname;

    public Configuration() {
        this.defaultPort = 5000;
        this.registryPort = 1717;
        this.hostname = "localhost";
    }

    public int getDefaultPort() {
        return defaultPort;
    }
    public int getRegistryPort() {
        return registryPort;
    }
    public String getHostname() {
        return hostname;
    }

    //  metodo per settare il file separator
    public String setFileSeparator(String fileName){
        String os = System.getProperty("os.name").toLowerCase();
        String workingDir = System.getProperty("user.dir");
        String absolutePath;
        if (os.contains("win")) {
            absolutePath = workingDir + "\\src\\client\\" + fileName;
        } else if (os.contains("mac")) {
            absolutePath = workingDir + "/src/client/" + fileName;
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            absolutePath = workingDir + "/src/client/" + fileName;
        } else if (os.contains("sunos")) {
            absolutePath = workingDir + "/src/client/" + fileName;
        } else {
            System.out.println("Sistema operativo non riconosciuto");
            absolutePath = workingDir + "/src/client/" + fileName;
        }
        return absolutePath;
    }
}
