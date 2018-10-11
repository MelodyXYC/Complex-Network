import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class Network {

    HashMap<Integer, Vertex> nodes;
    int nodesNum; //the number of nodes in this network

    public Network() {
        nodes = new HashMap<>();
    }

    public void initNetwork(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename)); //open a csv file
            String line = null;
            while((line = reader.readLine()) != null){
                String item[] = line.split(","); //split a line with ","
                int nodeIndex = Integer.parseInt(item[0]);
                int neighborIndex = Integer.parseInt(item[1]);
                if (!nodes.containsKey(nodeIndex)) {
                    Vertex node = new Vertex();
                    node.index = nodeIndex;
                    node.neighbors.add(neighborIndex);
                    nodes.put(nodeIndex, node);
                } else {
                    nodes.get(nodeIndex).neighbors.add(neighborIndex);
                }
                if (!nodes.containsKey(neighborIndex)) {
                    Vertex neighbor = new Vertex();
                    neighbor.index = neighborIndex;
                    nodes.put(neighborIndex, neighbor);
                }
                nodes.get(neighborIndex).neighbors.add(nodeIndex);
            } //create the network by file
        } catch (Exception e) {
            e.printStackTrace();
        }

        nodesNum = nodes.size();
        //System.out.println("size:" + nodesNum);
        nodes.values().stream().forEach(vertex -> vertex.getDegree()); //initialize every vertex's degree
    }

    public void initNodes(double rate) { //set the selected immunized vertices by newCentrality
        double sum = 0;
        for (Vertex a : nodes.values()) {
            sum += a.getMean();
        }
        for (Vertex a : nodes.values()) {
            a.newCentrality = a.degree * (-Math.log(a.getMean() / sum)) * a.beta;
        } //calculate the newCentrality

        ArrayList<Vertex> temp = new ArrayList<>();
        temp.addAll(nodes.values());
        temp.sort(new Comparator<Vertex>() {
            @Override
            public int compare(Vertex o1, Vertex o2) {
                if (o1.newCentrality - o2.newCentrality < 0) {
                    return 1;
                } else if (o1.newCentrality - o2.newCentrality > 0) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        for (int i = 0; i < nodes.size() * rate; i++) {
            int index = temp.get(i).index;
            nodes.get(index).currentState = "immunized";
        }
    }

    public void initNodes(String filename, double ratio) { //set the selected immunized vertices by file
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line = null;
            int num = 0;
            while ((line = reader.readLine()) != null) {
                String item[] = line.split(",");
                int immunizedNodeIndex = Integer.parseInt(item[0]);
                nodes.get(immunizedNodeIndex).currentState = "immunized";
                num++;
                if (num >= ratio * nodesNum) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void infection(Vertex infectedNode) {
        for (Integer a : infectedNode.neighbors) {
            if (!nodes.get(a).currentState.equals("immunized")) {
                nodes.get(a).nextVirusNum++;
            }
        }
    }

    public void propagation(int run_times, int ENDSIMUL, int initialInfectedNum, int [][] record) {
        int infectedNum, timeTick;
        double clickProb;


        for (int k = 1; k <= run_times; k++) { //simulation begins
            infectedNum = 0;
            for (int i = 0; i < initialInfectedNum; i++) {
                int index;
                while (true) {
                    index = (int)Math.round(Math.random()* (nodesNum + 1));
                    if (!nodes.get(index).currentState.equals("immunized")) {
                        break;
                    }
                }
                Vertex temp = nodes.get(index);
                temp.currentState = "infected";
                temp.virusNum = 1;
                for (Integer a : temp.neighbors) {
                    if (!nodes.get(a).currentState.equals("immunized")) {
                        nodes.get(a).virusNum++;
                        nodes.get(a).currentState = "danger";
                    }
                }
                infectedNum++;
            } //initialize infected nodes

            timeTick = 0;

            while (timeTick < ENDSIMUL) {
                timeTick++;
                for (Vertex a : nodes.values()) {
                    a.currentCheckTime--;
                    if (a.currentState.equals("infected") || a.currentState.equals("danger")) {
                        if (a.currentCheckTime == 0) {
                            clickProb = 1.0 - Math.pow(1.0 - a.beta, a.virusNum); //multiClickProb
                            a.virusNum = 0;
                            if (Math.random() < clickProb) {
                                if (a.currentState.equals("danger")) {
                                    a.currentState = "infected";
                                    infectedNum++;
                                }
                                infection(a);
                            }
                        }
                    }
                }

                for (Vertex a : nodes.values()) { //update current status
                    if (a.currentCheckTime == 0) {
                        a.currentCheckTime = a.timeInterval.get(a.currentInterval++); //get next checkTime
                    }
                    if (a.nextVirusNum != 0 && a.currentState.equals("healthy")) {
                        a.currentState = "danger";
                    }
                    a.virusNum += a.nextVirusNum;
                    a.nextVirusNum = 0;
                }

                record[k][timeTick] = infectedNum;
            }

         for (Vertex a : nodes.values()) {
                if (!a.currentState.equals("immunized")) {
                    a.currentState = "healthy";
                }
                a.currentInterval = 0;
                a.virusNum = 0;
                a.nextVirusNum = 0;
                a.currentCheckTime = a.timeInterval.get(0);
         } //reset vertices

        }

        for (timeTick = 1; timeTick <= ENDSIMUL; timeTick++) {
            int sum = 0;
            for (int k = 1; k <= run_times; k++) {
                sum += record[k][timeTick];
            }
            System.out.println("Timetick" + timeTick + ": " + sum / run_times);
        }
    }

}
