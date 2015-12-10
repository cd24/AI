Data about parameters for this application

Crossover Type:
If you want to modify the crossover the types are as follows
0 => full pull in from all edges connected to the hidden node
1 => single input-hidden edge chosen to be pulled in,  all hidden-output edges preserved

Program Flags:
-c <double> ; this represents the desired crossover rate.  Values larger than or equal to 1 will result in 100% crossover, those smaller than or equal to 0 will result in no crossover. The default is 0.1
-m <double> ; this represents the desired mutation rate.  Same value restrictions as crossover. The default is 0.4
-g <integer> ; this allows the user to specify the maximum number of generations allowed before exiting.  The default is 1000
-p <integer> ; this allows the user to specify the population of the environment.  The default value is 100.
-ct <integer> ; this integer must follow the flag specifications laid out in the crossover type section. Default value is 0.

example execution with 40% chance of crossover, 60% chance of mutation, 1000 individuals in the population and 40000 generations:
java -jar AI.jar -c 0.4 -m 0.6 -p 1000 -g 40000

Any attribute not included will roll over to its respective default.