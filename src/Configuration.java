
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
        return "Configuration{" +
                "timeoutWord=" + timeoutWord +
                ", defaultPort=" + defaultPort +
                ", RegistryPort=" + RegistryPort +
                '}';
    }
}