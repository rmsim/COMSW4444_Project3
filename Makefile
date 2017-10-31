compile:
	javac -cp . exchange/sim/*.java

clean:
	rm exchange/*/*.class

run:
	java -cp . exchange.sim.Simulator --players g1 g2 g3 g4 g5 g6 --gui --fps 1

