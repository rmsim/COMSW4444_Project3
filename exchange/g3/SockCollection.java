package exchange.g3;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Random;

import exchange.sim.Offer;
import exchange.sim.Request;
import exchange.sim.Sock;


public class SockCollection{

    ArrayList<Sock> collection;
    double maxDist = 442.0;
    int id; // Player Id.

    // Keeps track of whether a transaction has occurred to
    //  optimize against performing greedy pair.
    boolean transactionOccurred = false;

    int K; // for clustering
    ArrayList<ArrayList<Sock>> clusters;
    ArrayList<Sock> clusterCenters;
    ArrayList<Sock> extras;

    int p; //pivot number
    double t = 50; //threshold distance
    int round; //keep track of turns

    public SockCollection(List<Sock> socks, int id, int turn){
        this.id = id;
        this.collection = new ArrayList<>(socks);

        this.K = (int) 3; //Math.ceil(socks.size()/17);
        this.clusters = new ArrayList();

        for (int i = 0; i < K; i++) {
            clusters.add(new ArrayList<Sock>());
        }

        this.clusterCenters = new ArrayList<>();
        this.extras = new ArrayList<>();

        //design a pivot
        this.p = 0;

        //setting global turn to round
        this.round = turn;

        preprocessSockCollection();
    }

    public void addSock(Sock newSock){
        collection.add(newSock);
    }

    public void removeSock(Sock unwantedSock){
        collection.remove(unwantedSock);
    }

    public ArrayList<Sock> findInitialClusterCenters() {
    // Change the order of socks in your collection using some algorithm.
        Random rand = new Random();
        ArrayList<Sock> ret = new ArrayList<>();

        ret.add(new Sock(0, 127, 127));
        ret.add(new Sock(127, 0, 127));
        ret.add(new Sock(127, 127, 0));

        /*
        for (int k = 0; k < K; k++) {
            ret.add(new Sock(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)));
        }*/

        return ret;
    }

    private void cluster(int numIterations) {
        // adjusts clusters and clusterCenters based on collection

        if (clusterCenters.size() == 0)
            clusterCenters = findInitialClusterCenters();

        int bestClusterNumber;
        double closestDistance;
        double d;
        for (int iteration = 0; iteration < numIterations; iteration++) {
            // use centers to place socks into clusters
            for (ArrayList<Sock> c : clusters) {
                c.clear();
            }
            for (Sock s : collection) {
                bestClusterNumber = -1;
                closestDistance = maxDist;
                for (int k = 0; k < K; k++) {
                    d = clusterCenters.get(k).distance(s);
                    if (d < closestDistance) {
                        closestDistance = d;
                        bestClusterNumber = k;
                    }
                }

                clusters.get(bestClusterNumber).add(s);
            }

            // use clusters to adjust centers
            for (int k = 0; k < K; k++) {
                clusterCenters.set(k, meanLoc(clusters.get(k)));
            }
        }
    }

    public static Sock meanLoc(ArrayList<Sock> cluster) {
        // returns the average location of a cluster (the "true" center)
        int clusterSize = cluster.size();
        double x = 0.0;
        double y = 0.0;
        double z = 0.0;
        for (Sock s : cluster) {
            x += (double) s.R;
            y += (double) s.G;
            z += (double) s.B;
        }
        x /= clusterSize;
        y /= clusterSize;
        z /= clusterSize;
        return new Sock((int)x, (int)y, (int)z);
    }

    public void greedilyProcessSockList(ArrayList<Sock> socks) {
        ArrayList<Sock> processedSocks = new ArrayList<Sock>();
        while (socks.size() >= 2) {
            Sock s = socks.get(0);
            socks.remove(0);

            double smallest_dist = maxDist;
            Sock bestSock = null;

            for (Sock s2 : socks) {
                double d = s.distance(s2);
                if (d < smallest_dist) {
                    smallest_dist = d;
                    bestSock = s2;
                }
            }

            processedSocks.add(s);
            processedSocks.add(bestSock);
            socks.remove(bestSock);
        }

        socks.addAll(processedSocks);
    }

    //from team 2
    public void pairBlossom() {
        int[] match = new Blossom(getCostMatrix(), true).maxWeightMatching();
        ArrayList<Sock> result = new ArrayList<Sock>();
        for (int i=0; i<match.length; i++) {
            if (match[i] < i) continue;
            result.add(collection.get(i));
            result.add(collection.get(match[i]));
        }
        collection = result;
    }

    private float[][] getCostMatrix() {
        int n = collection.size();
        float[][] matrix = new float[n*(n-1)/2][3];
        int idx = 0;

        for (int i = 0; i < n; i++) {
            for (int j=i+1; j< n; j++) {
                matrix[idx] = new float[]{i, j, (float)(-collection.get(i).distance(collection.get(j)))};
                idx ++;
            }
        }
        return matrix;
    }

    // Change the order of socks in your collection using some algorithm.
    private void preprocessSockCollection() {
        //runs Blossom at the start
        //CORRECT SYNTAX

        Good[];
        Bad[];
        pairBlossom();
        for (p < collection.size()) {
            if (collection.get(i) < t) {
                Good.append(i);
            } else {
                Bad.append(i);
            }
            return Good + Bad;
            p = Bad[0];
        }
        //arranging the pairs
        cluster(30);

        for (ArrayList<Sock> c : clusters) {
            greedilyProcessSockList(c);
        }

        extras.clear();
        collection.clear();
        for (ArrayList<Sock> c : clusters) {
            if (c.size() % 2 == 0) {
                collection.addAll(c);
            } else {
                for (int i = 1; i < c.size(); i++) {
                    collection.add(c.get(i));
                }

                extras.add(c.get(0));
            }
        }

        collection.addAll(extras);
    }

    private int[] getWorstPairingSockIds() {
        //now consider from p onwards not the whole collection

        if (extras.size() != 0) {
            // We know that the extras are at the end of the collection.
            return new int[] {collection.size()-1, collection.size()-2};
        }

        int w1 = -1;
        int w2 = -1;

        double maxDistance = -1.0;

        for (int i = p; i < collection.size(); i += 2) {
            Sock sock1 = collection.get(i);
            Sock sock2 = collection.get(i + 1);

            if (sock1.distance(sock2) > maxDistance) {
                w1 = i;
                w2 = i+1;
                maxDistance = sock1.distance(sock2);
            }
        }

        return new int[] { w1, w2 };
    }

    // Gets the shortest distance of a sock to the rest of our collection.
    private double getShortestDistance(Sock sock) {
        double minDistance = maxDist;
        for (int i = 0; i < collection.size(); ++i) {
            if (sock.distance(collection.get(i)) < minDistance) {
                minDistance = sock.distance(collection.get(i));
            }
        }

        return minDistance;
    }

    // Check if a new sock will give us a better pairing than an existing pair.
    private boolean checkNewSockCompatibility(Sock sock) {
        //check if new sock with existing sock in last 50percent to give threshold value
        //if better than threshold value, improve our score and take it
        //if worse, ignore it

        for (int i = 0; i < collection.size(); i += 2) {
            // int j = i % 2 == 0? i + 1: i - 1;
            int j = i + 1;
            if (sock.distance(collection.get(i)) < t){
                return true;
            }
        }

        return false;
    }

    public double computeEmbarrassment(){
        if (transactionOccurred)
            preprocessSockCollection();

        if(collection.size() % 2 != 0){
            return -1.0;
        }

        double result = 0;
        for(int i = 0; i < collection.size(); i += 2){
            Sock sock1 = collection.get(i);
            Sock sock2 = collection.get(i + 1);

            result += sock1.distance(sock2);
        }

        return result;
    }

    public int[] getWorstPairIds(){

        // only run preprocess in beginning init constructor & before last turn
        if (round ==1) {
            preprocessSockCollection();
        }

//        if (transactionOccurred) {
//            //preprocessSockCollection();
//        }
//
//        if (collection.size() < 2) {
//            return new int[]{-1, -1};
//        }

        // Reset the transaction flag.
        transactionOccurred = false;

        --round; //keep track of rounds
        return getWorstPairingSockIds();

    }

    public Sock getSock(int id) {
        return collection.get(id);
    }

    public void putSock(int id, Sock s) {
        collection.set(id, s);
        transactionOccurred = true;
    }

    public Request requestBestOffer(List<Offer> offers) {

        Offer myOffer = offers.get(id);

        // Removing socks from our collection and adding them back in later.
        int s1Index = collection.indexOf(myOffer.getFirst());
        collection.remove(myOffer.getFirst());
        int s2Index = collection.indexOf(myOffer.getSecond());
        collection.remove(myOffer.getSecond());

        List<Integer> toPickOffer = new ArrayList<>(Arrays.asList(-1, -1));
        List<Integer> toPickRank = new ArrayList<>(Arrays.asList(-1, -1));

        for (int i = 0; i < offers.size(); ++i) {
            if (toPickOffer.get(0) != -1 && toPickOffer.get(1) != -1) {
                // We have already selected socks.
                // We're not going to look at the rest of the offers.
                break;
            }

            // Skip our socks.
            if (i == id) {
                continue;
            }

            for (int j = 1; j < 3; ++j) {
                if (offers.get(i).getSock(j) == null) {
                    // I'm not sure what condition to check for if the player has not offered
                    //  a sock.
                    continue;
                }

                //only need to compare it with p to the end
                if (checkNewSockCompatibility(offers.get(i).getSock(j))){
                    if (toPickOffer.get(0) == -1) {
                        toPickOffer.set(0, i);
                        toPickRank.set(0, j);
                    }
                    else if (toPickOffer.get(1) == -1) {
                        toPickOffer.set(1, i);
                        toPickRank.set(1, j);
                    }
                }
            }
        }

        collection.add(s2Index, myOffer.getSecond());
        collection.add(s1Index, myOffer.getFirst());

        return new Request(toPickOffer.get(0), toPickRank.get(0), toPickOffer.get(1), toPickRank.get(1));
    }

    public ArrayList<Sock> getCollection() {
        return collection;
    }

    public void removeSock(int sockid, Sock s) {
        Collection.remove(sockid);
    }

    public void putPairedSock(Sock s) {
        for (sock:Bad) {
            //get the match for the Sock s
            //add into good array
            p += 2;
        }
    }
}
