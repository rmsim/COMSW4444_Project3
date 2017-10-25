compile:
	javac -cp . exchange/sim/*.java

clean:
	rm exchange/*/*.class

	
run:
	java -cp . exchange.sim.Simulator -p g0 g0 g0 --gui --fps 1
