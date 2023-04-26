
public class Configuration {
    private final int timeoutWord;
    private final int defaultPort;
    private final int RegistryPort;

    public Configuration() {
        this.timeoutWord = 2;       //  2 minuti
        this.defaultPort = 5000;
        this.RegistryPort = 1717;
    }

    public int getTimeoutWord() {
        return timeoutWord;
    }

    public int getDefaultPort() {
        return defaultPort;
    }

    public int getRegistryPort() {
        return RegistryPort;
    }
}