all:
	java exchange.sim.Simulator -p g1 g2 g3 g4 g5 g6 -t 100 -n 50 --gui --fps 5

compile:
	javac exchange/sim/*.java

clean:
	rm exchange/*/*.class

run:
	java exchange.sim.Simulator -n 100 -t 100 -p g1 g2 g3 g4 g5 g6 --gui --fps 5
