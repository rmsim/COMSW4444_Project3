package exchange.g3;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

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

    int p; //pivot number
    double t = 45; //threshold distance

    public SockCollection(List<Sock> socks, int id){
        this.id = id;
        this.collection = new ArrayList<>(socks);

        //design a pivot
        this.p = 0;

        preprocessSockCollection();
    }

    public void addSock(Sock newSock){
        collection.add(newSock);
    }

    public void removeSock(int sockId){
        collection.remove(sockId);
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
        pairBlossom();

        //System.out.println(collection);

        this.p = 0;

        for (int i = 0; i < collection.size(); i+=2) {
            if (collection.get(p).distance(collection.get(p+1)) > t) {
                Sock s1 = collection.get(p);
                Sock s2 = collection.get(p+1);
                collection.remove(p);
                collection.remove(p);
                collection.add(s1);
                collection.add(s2);
            } else {
                p += 2;
            }
        }

        //System.out.println(collection);

        System.out.println("Pivot: " + p);
    }

    // TODO: Move the worst pairing to the end.
    private int[] getWorstPairingSockIds() {
        //now consider from p onwards. not the whole collection
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

    // Check if a new sock will give us a better pairing than an existing pair.
    private boolean checkNewSockCompatibility(Sock sock) {
        //check if new sock with existing sock in last 50percent to give threshold value
        //if better than threshold value, improve our score and take it
        //if worse, ignore it

        for (int i = p; i < collection.size(); i += 2) {
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

        System.out.println("Pivot: " + p);

        // Preprocess if our pivot doesn't separate the list well.
        if (p >= collection.size() - 4) {
            this.t -= 5;
            preprocessSockCollection();
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

                // Only need to compare it with p to the end
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

    public ArrayList<Sock> getCollection(boolean rePair) {
        if (rePair) {
            preprocessSockCollection();
        }

        return collection;
    }

    public void putPairedSock(int oldId, Sock s) {
        removeSock(oldId);

        double maxD = maxDist;
        int s2 = 0;
        for (int i = p; i < collection.size(); ++i) {
            //get the match for the Sock s
            //add into good array
            if (collection.get(i).distance(s) < maxD) {
                s2 = i;
                maxD = collection.get(i).distance(s);
            }
        }

        collection.add(p, collection.get(s2));
        collection.add(p, s);

        collection.remove(collection.get(s2+2));

        p += 2;
    }
}
