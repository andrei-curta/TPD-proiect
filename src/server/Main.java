package server;

public class Main {
    private static int port = 9000;

    public static void main(String[] args) {
        TpdHttpsServer tpdHttpsServer = new TpdHttpsServer();

        try {
            tpdHttpsServer.start(port);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Server could not start" );
        }
    }
}
