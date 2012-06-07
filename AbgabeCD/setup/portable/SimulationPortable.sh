#!/bin/bash

# (AB)² Simulation launcher

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$DIR"

java -splash:splash.png -Xmx1024m -cp lib/*:Simulation.jar ch.zhaw.simulation.startup.SimulationMain $1


