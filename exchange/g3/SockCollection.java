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
    boolean transactionOccurred = true;

    int K; // for clustering
    ArrayList<ArrayList<Sock>> clusters;
    double[][] clusterCenters;

    public SockCollection(Sock[] socks, int id){
        this.id = id;
        this.collection = new ArrayList<>(Arrays.asList(socks));

        this.K = 8;
        this.clusters = new ArrayList();
        for (int i = 0; i < K; i++) {
            clusters.add(new ArrayList<Sock>());
        }

        this.clusterCenters = new double[K][3];
    }

    public void addSock(Sock newSock){
        collection.add(newSock);
    }

    public void removeSock(Sock unwantedSock){
        collection.remove(unwantedSock);
    }

    public static double findDist(Sock s, double[] p) {
        double x1 = (double)s.R;
        double y1 = (double)s.G;
        double z1 = (double)s.B;
        double x2 = p[0];
        double y2 = p[1];
        double z2 = p[2];
        return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2) + (z1-z2)*(z1-z2));
    }

    public double[][] findInitialClusterCenters() {
    // Change the order of socks in your collection using some algorithm.
        Random rand = new Random();
        double[][] ret = new double[K][3];
        for (int k = 0; k < K; k++) {

            ret[k][0] = 255*rand.nextDouble();
            ret[k][1] = 255*rand.nextDouble();
            ret[k][2] = 255*rand.nextDouble();
        }
        return ret;
    }

    private void cluster(int numIterations) {
        // adjusts clusters and clusterCenters based on collection
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
                    d = findDist(s, clusterCenters[k]);
                    if (d < closestDistance) {
                        closestDistance = d;
                        bestClusterNumber = k;
                    }
                }
                clusters.get(bestClusterNumber).add(s);
            }
            // use clusters to adjust centers
            for (int k = 0; k < K; k++) {
                clusterCenters[k] = meanLoc(clusters.get(k));
            }
        }
    }

    public static double[] meanLoc(ArrayList<Sock> cluster) {
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
        return new double[]{x, y, z};
    }

    public void greedilyProcessSockList(ArrayList<Sock> socks) {
        ArrayList<Sock> processedSocks = new ArrayList<Sock>();
        while (socks.size() >= 2) {
            Sock s = socks.get(0);
            socks.remove(s);

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

    // Change the order of socks in your collection using some algorithm.
    private void preprocessSockCollection() {
        cluster(30);
        
        // System.out.println("PRINTING CLUSTERS:");
        // for (ArrayList<Sock> c : clusters) {
        //     for (Sock s : c) {
        //         System.out.println(s);
        //     }
        //     System.out.println();
        // }


        for (ArrayList<Sock> c : clusters) {
            greedilyProcessSockList(c);
        }

        ArrayList<Sock> extras = new ArrayList<Sock>();

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



        
        // greedilyProcessSockList(collection);

    }

    private void sortByDistance() {
        for (int i = 0; i < collection.size()-2; i+=2) {
            int j = i+2;
            Sock s2 = collection.get(j+1);
            Sock s1 = collection.get(j);
            double currentDist = s1.distance(s2);

            j-=2;
            while (j >= 0 && currentDist > collection.get(j).distance(collection.get(j+1))) {
                collection.set(j+3, collection.get(j+1));
                collection.set(j+2, collection.get(j));
                j-=2;
            }

            collection.set(j+2, s1);
            collection.set(j+3, s2);
        }
    }

    private int[] getWorstPairingSockIds() {
        // int w1 = -1;
        // int w2 = -1;

        // double maxDistance = -1.0;

        // for (int i = 0; i < collection.size(); i += 2) {
        //     Sock sock1 = collection.get(i);
        //     Sock sock2 = collection.get(i + 1);

        //     if (sock1.distance(sock2) > maxDistance) {
        //         w1 = i;
        //         w2 = i+1;
        //         maxDistance = sock1.distance(sock2);
        //     }
        // }

        // return new int[] { w1, w2 };

        return new int[] {collection.size()-1, collection.size()-2};
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
        for (int i = 0; i < collection.size(); i += 2) {
            // int j = i % 2 == 0? i + 1: i - 1;
            int j = i + 1;
            if (sock.distance(collection.get(i)) <
                    collection.get(i).distance(collection.get(j))) {
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
        if (transactionOccurred) {
            preprocessSockCollection();
        }
        
        if (collection.size() < 2) {
            return new int[]{-1, -1};
        }

        // Reset the transaction flag.
        transactionOccurred = false;

        return getWorstPairingSockIds();
    }

    public Sock getSock(int id) {
        return collection.get(id);
    }

    public void putSock(int id, Sock s) {
        collection.set(id, s);
        transactionOccurred = true;
    }

    // Shuffle socks around to get another sock to trade next time.
    public void shuffle() {
        int n = collection.size();

        int i = 2 * collection.size() / 3;
        int j = i%2 == 0? i + 1: i - 1;

        Sock temp1 = collection.get(n-1);
        Sock temp2 = collection.get(n-2);

        collection.set(n-1, collection.get(i));
        collection.set(n-2, collection.get(j));

        collection.set(i, temp1);
        collection.set(j, temp2);

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
}