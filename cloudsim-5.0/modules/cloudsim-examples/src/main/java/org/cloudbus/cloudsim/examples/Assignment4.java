package org.cloudbus.cloudsim.examples;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.network.datacenter.NetworkDatacenter;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import java.util.ArrayList;
import java.util.List;

public class Assignment4 {

    public static void main(String[] args) {
        try {
            // Step 1: Initialize CloudSim simulation
            int numUsers = 1; // Number of users (brokers)
            CloudSim.init(numUsers, null, false);

            // Step 2: Create the Datacenter
            Datacenter datacenter = createDatacenter();

            // Step 3: Create the CustomBroker
            CustomBroker broker = new CustomBroker(new CloudSim(), "CustomBroker");

            // Step 4: Create a list of VMs
            List<Vm> vmList = createVms();
            broker.submitVmList(vmList);

            // Step 5: Create Cloudlets (tasks to run on the VMs)
            List<Cloudlet> cloudletList = createCloudlets();
            broker.submitCloudletList(cloudletList);

            // Step 6: Start the simulation
            CloudSim.startSimulation();

            // Step 7: Retrieve results after simulation
            List<Cloudlet> finishedCloudlets = broker.getCloudletReceivedList();
            for (Cloudlet cloudlet : finishedCloudlets) {
                System.out.println("Cloudlet " + cloudlet.getCloudletId() + " finished with status: " + cloudlet.getStatus());
            }

            // Step 8: Stop the simulation
            CloudSim.stopSimulation();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Create Datacenter with a simple configuration
    // Create Datacenter with a simple configuration
    private static Datacenter createDatacenter() throws Exception {
        // Create a list of hosts
        List<Host> hostList = new ArrayList<>();

        // Define a single host with its characteristics
        int hostId = 0;
        int ram = 10000; // Host RAM (MB)
        long storage = 1000000; // Storage (MB)
        int bw = 10000; // Bandwidth
        int peCount = 4; // Number of processing elements (cores)

        List<Pe> peList = new ArrayList<>();
        for (int i = 0; i < peCount; i++) {
            peList.add(new Pe(i, new PeProvisionerSimple(1000))); // 1000 MIPS per core
        }

        Host host = new Host(
                hostId, new RamProvisionerSimple(ram),
                new BwProvisionerSimple(bw), storage,
                peList, new VmSchedulerTimeShared(peList)
        );

        hostList.add(host); // Add the host to the list

        // Define Datacenter Characteristics with the correct parameters
        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                "x86", "Linux", "Xen",
                hostList, 10.0,  // Time zone
                3.0, 0.05, 0.001, 0.0 // Cost parameters (can be adjusted)
        );

        // Define VM Allocation Policy
        VmAllocationPolicy vmPolicy = new VmAllocationPolicySimple(hostList);

        // Create empty storage list (since we're not using storage in this case)
        List<Storage> storageList = new ArrayList<>();

        // Create the Datacenter and return it
        return new NetworkDatacenter(
                "Datacenter",
                characteristics,
                vmPolicy,
                storageList,
                0.0 // Cost per bandwidth (0 for simplicity)
        );
    }



    // Create a list of VMs with CloudletScheduler
    private static List<Vm> createVms() {
        List<Vm> vms = new ArrayList<>();

        // Create 3 VMs
        for (int i = 0; i < 3; i++) {
            Vm vm = createVm(i);
            vms.add(vm);
        }

        return vms;
    }

    // Create a VM with a CloudletScheduler
    private static Vm createVm(int id) {
        // Parameters: id, userId, mips, number of pes (processors), ram, bw, size, architecture, os, and CloudletScheduler
        CloudletScheduler scheduler = new CloudletSchedulerTimeShared();
        Vm vm = new Vm(id, 0, 1000, 1, 1024, 1000, 10000, "x86", scheduler);
        return vm;
    }

    // Create a list of cloudlets
    // Create a list of cloudlets
    private static List<Cloudlet> createCloudlets() {
        List<Cloudlet> cloudlets = new ArrayList<>();

        // Create 5 cloudlets
        for (int i = 0; i < 5; i++) {
            // Use a valid CloudletScheduler
            CloudletScheduler scheduler = new CloudletSchedulerTimeShared(); // ✅ Fixed

            Cloudlet cloudlet = new Cloudlet(
                    i,                   // Cloudlet ID
                    10000,               // Cloudlet length (instructions)
                    1,                   // Number of PEs (Processing Elements)
                    1000,                // File size (input file size)
                    1000,                // Output size (output file size)
                    new UtilizationModelFull(),  // CPU Utilization Model
                    new UtilizationModelFull(),  // RAM Utilization Model
                    new UtilizationModelFull()   // Bandwidth Utilization Model
            );

            cloudlets.add(cloudlet);
        }

        return cloudlets;
    }

}