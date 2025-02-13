package org.cloudbus.cloudsim.examples;

/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 */

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

/**
 * A simple example showing how to create a data center with one host and run
 * one cloudlet on it.
 */
public class lab3 {
	/** The cloudlet list. */
	private static List<Cloudlet> cloudletList;
	/** The vmlist. */
	private static List<Vm> vmlist;

	/**
	 * Creates main() to run this example.
	 *
	 * @param args the args
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Log.printLine("Starting CloudSimExample1...");

		try {
			// First step: Initialize the CloudSim package. It should be called before
			// creating any entities.
			int num_user = 1; // number of cloud users
			Calendar calendar = Calendar.getInstance(); // Calendar whose fields have been initialized with the current
			// date and time.
			boolean trace_flag = false; // trace events

			/*
			 * Comment Start - Dinesh Bhagwat
			 * Initialize the CloudSim library.
			 * init() invokes initCommonVariable() which in turn calls initialize() (all
			 * these 3 methods are defined in CloudSim.java).
			 * initialize() creates two collections - an ArrayList of SimEntity Objects
			 * (named entities which denote the simulation entities) and
			 * a LinkedHashMap (named entitiesByName which denote the LinkedHashMap of the
			 * same simulation entities), with name of every SimEntity as the key.
			 * initialize() creates two queues - a Queue of SimEvents (future) and another
			 * Queue of SimEvents (deferred).
			 * initialize() creates a HashMap of of Predicates (with integers as keys) -
			 * these predicates are used to select a particular event from the deferred
			 * queue.
			 * initialize() sets the simulation clock to 0 and running (a boolean flag) to
			 * false.
			 * Once initialize() returns (note that we are in method initCommonVariable()
			 * now), a CloudSimShutDown (which is derived from SimEntity) instance is
			 * created
			 * (with numuser as 1, its name as CloudSimShutDown, id as -1, and state as
			 * RUNNABLE). Then this new entity is added to the simulation
			 * While being added to the simulation, its id changes to 0 (from the earlier
			 * -1). The two collections - entities and entitiesByName are updated with this
			 * SimEntity.
			 * the shutdownId (whose default value was -1) is 0
			 * Once initCommonVariable() returns (note that we are in method init() now), a
			 * CloudInformationService (which is also derived from SimEntity) instance is
			 * created
			 * (with its name as CloudInformatinService, id as -1, and state as RUNNABLE).
			 * Then this new entity is also added to the simulation.
			 * While being added to the simulation, the id of the SimEntitiy is changed to 1
			 * (which is the next id) from its earlier value of -1.
			 * The two collections - entities and entitiesByName are updated with this
			 * SimEntity.
			 * the cisId(whose default value is -1) is 1
			 * Comment End - Dinesh Bhagwat
			 */
			CloudSim.init(num_user, calendar, trace_flag);

			// Second step: Create Datacenters
			// Datacenters are the resource providers in CloudSim. We need at
			// list one of them to run a CloudSim simulation
			Datacenter datacenter0 = createDatacenter("Datacenter_0");

			// Third step: Create Broker
			DatacenterBroker broker = createBroker();
			int brokerId = broker.getId();

			// Fourth step: Create VMs
			vmlist = new ArrayList<Vm>();

			// VM description
			int mips = 1000;
			long size = 10000; // image size (MB)
			String vmm = "Xen"; // VMM name

			// Create VMs 1-3: 2 CPU cores, 2GB RAM, 500MB bandwidth
			for (int i = 0; i < 3; i++) {
				int ram = 2048; // vm memory (MB)
				long bw = 500;
				int pesNumber = 2; // number of cpus
				Vm vm = new Vm(i, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
				vmlist.add(vm);
			}

			// Create VMs 4-6: 1 CPU core, 4GB RAM, 1000MB bandwidth
			for (int i = 3; i < 6; i++) {
				int ram = 4096; // vm memory (MB)
				long bw = 1000;
				int pesNumber = 1; // number of cpus
				Vm vm = new Vm(i, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
				vmlist.add(vm);
			}

			// submit vm list to the broker
			broker.submitVmList(vmlist);

			// Fifth step: Create Cloudlets
			cloudletList = new ArrayList<Cloudlet>();

			// Create Cloudlets 1-3: Length 3000, file size 500MB
			for (int i = 0; i < 3; i++) {
				Cloudlet cloudlet = new Cloudlet(i, 3000, 1, 500, 500,
						new UtilizationModelFull(), new UtilizationModelFull(),
						new UtilizationModelFull());
				cloudlet.setUserId(brokerId);
				cloudlet.setVmId(i); // Assign to first 3 VMs
				cloudletList.add(cloudlet);
			}

			// Create Cloudlets 4-6: Length 1000, file size 200MB
			for (int i = 3; i < 6; i++) {
				Cloudlet cloudlet = new Cloudlet(i, 1000, 1, 200, 200,
						new UtilizationModelFull(), new UtilizationModelFull(),
						new UtilizationModelFull());
				cloudlet.setUserId(brokerId);
				cloudlet.setVmId(i); // Assign to last 3 VMs
				cloudletList.add(cloudlet);
			}

			// submit cloudlet list to the broker
			broker.submitCloudletList(cloudletList);

			// Sixth step: Starts the simulation
			CloudSim.startSimulation();

			CloudSim.stopSimulation();

			// Final step: Print results when simulation is over
			List<Cloudlet> newList = broker.getCloudletReceivedList();
			printCloudletList(newList);

			Log.printLine("CloudSimExample1 finished!");
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("Unwanted errors happen");
		}
	}

	/**
	 * Creates the datacenter.
	 *
	 * @param name the name
	 *
	 * @return the datacenter
	 */
	private static Datacenter createDatacenter(String name) {
	    List<Host> hostList = new ArrayList<Host>();

	    // Define Hosts
	    List<Pe> peList1 = new ArrayList<Pe>();
	    for (int i = 0; i < 8; i++) {
	        peList1.add(new Pe(i, new PeProvisionerSimple(1000))); // 8-core host
	    }

	    int ram1 = 16384; // 16GB RAM
	    long storage1 = 100000; // 100GB Storage
	    int bw1 = 10000;
	    hostList.add(new Host(0, new RamProvisionerSimple(ram1), new BwProvisionerSimple(bw1), storage1, peList1, new VmSchedulerTimeShared(peList1)));

	    List<Pe> peList2 = new ArrayList<Pe>();
	    for (int i = 0; i < 4; i++) {
	        peList2.add(new Pe(i, new PeProvisionerSimple(1000))); // 4-core host
	    }

	    int ram2 = 8192; // 8GB RAM
	    long storage2 = 50000; // 50GB Storage
	    int bw2 = 10000;
	    hostList.add(new Host(1, new RamProvisionerSimple(ram2), new BwProvisionerSimple(bw2), storage2, peList2, new VmSchedulerTimeShared(peList2)));

	    // Datacenter Characteristics
	    String arch = "x86";
	    String os = "Linux";
	    String vmm = "Xen";
	    double time_zone = 10.0;
	    double cost = 3.0;
	    double costPerMem = 0.05;
	    double costPerStorage = 0.001;
	    double costPerBw = 0.0;
	    LinkedList<Storage> storageList = new LinkedList<Storage>();

	    DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
	        arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);

	    // Use Custom VM Allocation Policy
	    Datacenter datacenter = null;
	    try {
	        datacenter = new Datacenter(name, characteristics, new VmAllocationPolicyBestFit(hostList), storageList, 0);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return datacenter;
	}


	// We strongly encourage users to develop their own broker policies, to
	// submit vms and cloudlets according
	// to the specific rules of the simulated scenario
	/**
	 * Creates the broker.
	 *
	 * @return the datacenter broker
	 */
	private static DatacenterBroker createBroker() {
		DatacenterBroker broker = null;
		try {
			broker = new DatacenterBroker("Broker");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}

	/**
	 * Prints the Cloudlet objects.
	 *
	 * @param list list of Cloudlets
	 */
	private static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent
				+ "Data center ID" + indent + "VM ID" + indent + "Time" + indent
				+ "Start Time" + indent + "Finish Time" + indent + "Host ID");

		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
				Log.print("SUCCESS");
				Log.printLine(indent + indent + cloudlet.getResourceId()
						+ indent + indent + indent + cloudlet.getVmId()
						+ indent + indent + dft.format(cloudlet.getActualCPUTime())
						+ indent + indent + dft.format(cloudlet.getExecStartTime())
						+ indent + indent + dft.format(cloudlet.getFinishTime())
						+ indent + indent + cloudlet.getVmId() % 2); // Host ID (simple allocation)
			}
		}
	}
}