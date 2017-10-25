package exchange.g3_sa;

import exchange.sim.Sock;


public class SockPair{

    Sock sock1;
    Sock sock2;

    public SockPair(Sock sock1, Sock sock2){
        this.sock1 = sock1;
        this.sock2 = sock2;
    }

    public double getDistance(){
        return sock1.distance(sock2);
    }

}