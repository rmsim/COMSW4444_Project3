compile:
	javac -cp . exchange/sim/*.java

clean:
	rm exchange/*/*.class

run:
	java -cp . exchange.sim.Simulator --players g3_sa g3_sa g3_sa g3_sa g3_sa g3_sa --gui --fps 1

