package exchange.g3;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import exchange.sim.Offer;
import exchange.sim.Sock;

public class SockCollection{

    ArrayList<Sock> collection;
    int embarrassment;
    double maxDist = 442.0;
    int id; // Player Id.

    public SockCollection(){
        collection = new ArrayList<Sock>(); 
        embarrassment = 0;
    }

    public SockCollection(ArrayList<Sock> collection){
        this.collection = collection;
        embarrassment = 0;
    }

    public SockCollection(Sock[] socks, int id){
        this.id = id;
        this.collection = new ArrayList<Sock>(Arrays.asList(socks));
        embarrassment = 0;
    }

    public void addSock(Sock newSock){
        collection.add(newSock);
    }

    public void removeSock(Sock unwantedSock){
        collection.remove(unwantedSock);
    }

    // Change the order of socks in your collection using some algorithm.
    private void preprocessSockCollection() {
        ArrayList<Sock> processedSocks = new ArrayList<Sock>();
        while (!collection.isEmpty()) {
            Sock s = collection.get(0);
            collection.remove(s);

            double smallest_dist = 442.0; // > maximum distance socks can be
            Sock bestSock = null;

            for (Sock s2 : collection) {
                double d = s.distance(s2);
                if (d < smallest_dist) {
                    smallest_dist = d;
                    bestSock = s2;
                }
            }

            processedSocks.add(s);
            processedSocks.add(bestSock);
            collection.remove(bestSock);
        }

        collection.addAll(processedSocks);
        sortByDistance();
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
        // Optimize and return the top 2 socks.


        int w1 = -1;
        int w2 = -1;

        maxDist = -1.0;

        for (int i = 0; i < collection.size(); i += 2) {
            Sock sock1 = collection.get(i);
            Sock sock2 = collection.get(i + 1);

            if (sock1.distance(sock2) > maxDist) {
                w1 = i;
                w2 = i+1;
                maxDist = sock1.distance(sock2);
            }
        }

        return new int[] { w1, w2 };
    }


    public void computeEmbarrassment(){
        preprocessSockCollection();

        if(collection.size() % 2 != 0){
            embarrassment = -1;
            return;
        }                
               
        int result = 0;
        for(int i = 0; i < collection.size(); i += 2){
            Sock sock1 = collection.get(i);
            Sock sock2 = collection.get(i + 1);

            result += sock1.distance(sock2);
        }

        this.embarrassment = result;
    }

    public int getEmbarrassment(){
        computeEmbarrassment();
        return this.embarrassment;
    }

    public int[] getWorstPairIds(){
        computeEmbarrassment();
        
        if (collection.size() < 2)
            return new int[]{-1, -1};

        return getWorstPairingSockIds();
    }

    public Sock getSock(int id) {
        return collection.get(id);
    }

    public void putSock(int id, Sock s) {
        collection.set(id, s);
    }

    public List<Offer> getBestOffer(List<Offer> offers) {

        Offer myOffer = offers.get(id);
        collection.remove(myOffer.getFirst());
        collection.remove(myOffer.getSecond());

        for (int i = 0; i < offers.size(); ++i) {
            if (i == id) {
                continue;
            }

            // if
        }

        return null;
    }

    public ArrayList<Sock> getCollection() {
        return collection;
    }
}