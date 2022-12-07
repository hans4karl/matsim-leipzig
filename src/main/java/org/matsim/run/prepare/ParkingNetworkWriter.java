package org.matsim.run.prepare;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.utils.objectattributes.attributable.Attributes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ParkingNetworkWriter {

    private static final Logger log = LogManager.getLogger(ParkingNetworkWriter.class);

    Network network;
    String inputParkingCapacities;
    private static int adaptedLinksCount = 0;
    private static int networkLinksCount = 0;

    ParkingNetworkWriter(Network network, String inputParkingCapacities) {
        this.network = network;
        this.inputParkingCapacities = inputParkingCapacities;
    }

    public void addParkingCapacitiesToLinks() {
        Map<String, String> linkParkingCapacities = getLinkParkingCapacities();

        for(Link link : network.getLinks().values()) {
            if(link.getId().toString().contains("pt_")) {
                continue;
            }
            networkLinksCount++;

            if(linkParkingCapacities.get(link.getId().toString()) != null) {
                int parkingCapacity = Integer.parseInt(linkParkingCapacities.get(link.getId().toString()));

                Attributes linkAttributes = link.getAttributes();
                linkAttributes.putAttribute("parkingCapacity", parkingCapacity);
                adaptedLinksCount++;
            }
        }
        log.info(adaptedLinksCount + " / " + networkLinksCount + " were complemented with a parkingCapacity attribute.");
    }

    private Map<String, String> getLinkParkingCapacities() {
        Map<String, String> linkParkingCapacities = new HashMap<>();

        try(BufferedReader reader = new BufferedReader(new FileReader(inputParkingCapacities))) {
            String lineEntry;
            while((lineEntry = reader.readLine()) != null) {

                linkParkingCapacities.putIfAbsent(lineEntry.split("\t")[0], lineEntry.split("\t")[1]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return linkParkingCapacities;
    }
}
