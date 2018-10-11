import java.util.ArrayList;

public class Vertex {

    int index, degree, virusNum, nextVirusNum, currentCheckTime, currentInterval;
    double newCentrality;  //heterogeneity-based centrality
    //double mean;
    double beta; //the rate of clicking the emails with virus
    ArrayList<Integer> timeInterval;
    String currentState;
    ArrayList<Integer> neighbors;

    public Vertex() {
        virusNum = 0;
        nextVirusNum = 0;
        beta = betaGenerator();
        timeInterval = timeGenerator(new ArrayList<>(), 600); //ENDSIMUL needs to be edited
        currentCheckTime = timeInterval.get(0);
        currentInterval = 0;
        currentState = "healthy";
        neighbors = new ArrayList<>();
    }


    private static double normalGenerator(double average, double variance){ // normal distribution generator.
        double temp, x = 0;
        double [] Table = new double[]{0.5000, 0.5398, 0.5793, 0.6179, 0.6554, 0.6915,
                0.7257, 0.7580, 0.7881, 0.8159, 0.8413, 0.8643,
                0.8849, 0.9032, 0.9192, 0.9332, 0.9452, 0.9554,
                0.9641, 0.9713, 0.9773, 0.9822, 0.9861, 0.9893,
                0.9918, 0.9938, 0.9954, 0.9965, 0.9975, 1.0000 };
        temp = Math.random();
        long sign = 1;
        if ( temp <0.5) {
            sign = -1;
            temp = 1 - temp;
        }
        for (int i=0;i< 30; i++) {
            if ( temp <= Table[i] ) {
                x = (double)i / 10.0;
                break;
            }
        }
        if ( sign == -1 ) {
            x = 0 - x;
        }
        temp = average + x * variance; // variance is the standard deviation.
        // experiment require that normal distribution number to be non-negative.
        if ( temp <=0.00001) {
            temp = 0.01;
        }

        return temp;
    }

    private static int expGenerator( double rate) { // exponential distribution generator.
        return (int)Math.floor( 0.5 - Math.log( 1 - Math.random() ) / rate );
    }

    private static double betaGenerator() {
        double temp = normalGenerator(0.5, 0.3);
        if (temp >= 1.0) {
            temp = 0.9999;
        }

        return temp;
    }

    private static ArrayList<Integer> timeGenerator (ArrayList<Integer> timeInterval, int ENDSIMUL) {
        double temp = normalGenerator(40, 20),checkRate;
        int total = 0;

        if (temp < 1) {
            temp = 1; // fastest check time is 1
        }
        checkRate = 1.0 / temp;
        while (total < ENDSIMUL) {
            int checkTime = expGenerator(checkRate);
            if (checkTime < 1) {
                checkTime = 1; //minimum checkTime is 1
            }
            timeInterval.add(checkTime);
            total += checkTime;
        }

        return timeInterval;
    }

    public void getDegree() {
        this.degree = neighbors.size();
    }

    public double getMean() {
        int sum = 0;
        for (int a : timeInterval) {
            sum += a;
        }

        return (double)sum / (double)timeInterval.size();
    }
}
