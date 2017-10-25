package exchange.g3;


import java.util.*;
import exchange.sim.Sock;



public class SockCollection{

    ArrayList<Sock> collection;
    ArrayList<Sock> sortedCollection;
    double embarrassment;


    public SockCollection(){
        collection = new ArrayList<Sock>(); 
        sortedCollection = new ArrayList<Sock>();
        embarrassment = 0;
    }

    public SockCollection(ArrayList<Sock> collection){
        this.collection = collection;
        sortedCollection = collection;
        sortSocks(sortedCollection);
        embarrassment = 0;
    }


    public SockCollection(Sock[] socks){
        this.collection = new ArrayList<Sock>(Arrays.asList(socks));
        sortedCollection = collection;
        sortSocks(sortedCollection);
        embarrassment = 0;
    }

    public void addSock(Sock newSock){
        collection.add(newSock);
    }


    public void removePair(Sock unwantedSock){
        collection.remove(unwantedSock);
    }

    public void reset(Sock[] socks){
        collection.clear();
        collection.addAll(Arrays.asList(socks));
    }

    public void sortSocks(){
        Collections.sort(collection, new Comparator<Sock>(){
            @Override
            public int compare(Sock sock1, Sock sock2){
                //Descending Order
                return sock2.hashCode() - sock1.hashCode();             
            }
        });
    }    

    public void sortSocks(ArrayList<Sock> unsortedcollection){
        Collections.sort(unsortedcollection, new Comparator<Sock>(){
            @Override
            public int compare(Sock sock1, Sock sock2){
                //Descending Order
                return sock2.hashCode() - sock1.hashCode();             
            }
        });
    } 

    public void computeEmbarrassment(){
        sortedCollection = collection;
        sortSocks(sortedCollection);

        if(sortedCollection.size() % 2 != 0){
            embarrassment = -1;
            return;
        }                
               
        double result = 0;
        for(int i = 0; i < sortedCollection.size(); i = i + 2){
            Sock sock1 = (Sock) sortedCollection.get(i);
            Sock sock2 = (Sock) sortedCollection.get(i + 1);

            result += sock1.distance(sock2);
        }

        this.embarrassment = result;
        return ;
    }


    public double getEmbarrassment(){
        computeEmbarrassment();
        return this.embarrassment;
    }


    public Sock getSock(int index){
        return (Sock) collection.get(index);
    }

    public int getIndex(Sock sock){
        return collection.indexOf(sock);
    }

    public int[] getWorstPair(){
        //Sock[] result = new Sock[2];
        int[] result = new int[2];
        computeEmbarrassment();

        if (sortedCollection.size() < 2){
            //result[0] = (Sock) null;
            //result[1] = (Sock) null;
            result[1] = -1;
            result[0] = -1;
        }

        else{
            Sock sock1 = (Sock) sortedCollection.get(0);
            Sock sock2 = (Sock) sortedCollection.get(1);
            result[0] = getIndex(sock1);
            result[1] = getIndex(sock2);
        }
        
        return result;
    }
}