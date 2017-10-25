package exchange.g3;


import java.util.ArrayList;
import exchange.sim.Sock;



public class SockCollection{

    ArrayList<Sock> collection;
    int embarrassment;


    public SockCollection(){
        collection = new ArrayList<Sock>(); 
        embarrassment = 0;
    }

    public SockCollection(ArrayList<Sock> collection){
        this.collection = collection;
        embarrassment = 0;
    }


    public SockCollection(Sock[] socks){
        this.collection = new ArrayList<Sock>(Arrays.asList(socks));
        embarrassment = 0;
    }

    public void addSock(Sock newSock){
        collection.add(newSock);
    }


    public void removePair(Sock unwantedSock){
        collection.remove(unwantedSock);
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


    public void computeEmbarrassment(){
        sortSocks();

        if(collection.size() % 2 != 0){
            embarrassment = -1;
            return;
        }                
               
        int result = 0;
        for(int i = 0; i < collection.size(); i = i + 2){
            Sock sock1 = (Sock) collection.get(i);
            Sock sock2 = (Sock) collection.get(i + 1);

            result += sock1.distance(sock2);
        }

        this.embarrassment = result;
        return ;
    }


    public int getEmbarrassment(){
        computeEmbarrassment();
        return this.embarrassment;
    }



    public Sock[] getWorstPair(){
        computeEmbarrassment();
        
        if (collection.size() < 2)
            return [null, null];

        Sock sock1 = (Sock) collection.get(0);
        Sock sock2 = (Sock) collection.get(0);

        return [sock1, sock2];
    }
}