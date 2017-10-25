package exchange.g3;

import exchange.sim.Sock;


public class SockPair{

    Sock sock1;
    Sock sock2;
    int distance = 0;


    public SockPair(Sock sock1, Sock sock2){
        this.sock1 = sock1;
        this.sock2 = sock2;

        distance = sock1.distance(sock2);
    }


    public int getDistance(){
        return distance;
    }



}