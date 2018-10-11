public class Main {
    static int run_times = 100;
    static int ENDSIMUL = 600;
    static int initialInfectedNum = 2;

    public static void main(String [] args) {
        Network email = new Network();
        email.initNetwork("AS.csv");
        email.initNodes("betweeness.csv", 0.05);
        //email.initNodes(0.05);
        email.propagation(run_times, ENDSIMUL, initialInfectedNum, new int [run_times + 1][ENDSIMUL + 1]);
    }
}
