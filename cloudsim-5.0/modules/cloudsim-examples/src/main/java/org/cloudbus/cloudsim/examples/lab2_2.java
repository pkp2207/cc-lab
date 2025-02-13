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
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
//import org.cloudbus.cloudsim.VmAllocationPolicySimpleLinear; // If using CloudSim Plus


/**
 * A simple example showing how to create a data center with one host and run one cloudlet on it.
 */
public class lab2_2 {
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
			// First step: Initialize the CloudSim package. It should be called before creating any entities.
			int num_user = 1; // number of cloud users
			Calendar calendar = Calendar.getInstance(); // Calendar whose fields have been initialized with the current date and time.
 			boolean trace_flag = false; // trace events

			/* Comment Start - Dinesh Bhagwat
			 * Initialize the CloudSim library.
			 * init() invokes initCommonVariable() which in turn calls initialize() (all these 3 methods are defined in CloudSim.java).
			 * initialize() creates two collections - an ArrayList of SimEntity Objects (named entities which denote the simulation entities) and
			 * a LinkedHashMap (named entitiesByName which denote the LinkedHashMap of the same simulation entities), with name of every SimEntity as the key.
			 * initialize() creates two queues - a Queue of SimEvents (future) and another Queue of SimEvents (deferred).
			 * initialize() creates a HashMap of of Predicates (with integers as keys) - these predicates are used to select a particular event from the deferred queue.
			 * initialize() sets the simulation clock to 0 and running (a boolean flag) to false.
			 * Once initialize() returns (note that we are in method initCommonVariable() now), a CloudSimShutDown (which is derived from SimEntity) instance is created
			 * (with numuser as 1, its name as CloudSimShutDown, id as -1, and state as RUNNABLE). Then this new entity is added to the simulation
			 * While being added to the simulation, its id changes to 0 (from the earlier -1). The two collections - entities and entitiesByName are updated with this SimEntity.
			 * the shutdownId (whose default value was -1) is 0
			 * Once initCommonVariable() returns (note that we are in method init() now), a CloudInformationService (which is also derived from SimEntity) instance is created
			 * (with its name as CloudInformatinService, id as -1, and state as RUNNABLE). Then this new entity is also added to the simulation.
			 * While being added to the simulation, the id of the SimEntitiy is changed to 1 (which is the next id) from its earlier value of -1.
			 * The two collections - entities and entitiesByName are updated with this SimEntity.
			 * the cisId(whose default value is -1) is 1
			 * Comment End - Dinesh Bhagwat
			 */
			CloudSim.init(num_user, calendar, trace_flag);

			// Second step: Create Datacenters
			// Datacenters are the resource providers in CloudSim. We need at
			// list one of them to run a CloudSim simulation
			Datacenter datacenter0 = createDatacenter("Datacenter_0");

			// Third step: Create Broker
			// Create Broker
						DatacenterBroker broker = createBroker();
						int brokerId = broker.getId();

						// Create Hosts
						List<Host> hostList = new ArrayList<>();
						List<Pe> peList1 = new ArrayList<>();
						for (int i = 0; i < 4; i++) peList1.add(new Pe(i, new PeProvisionerSimple(1000)));
						hostList.add(new Host(0, new RamProvisionerSimple(8192), new BwProvisionerSimple(10000), 100000, peList1, new VmSchedulerTimeShared(peList1)));

						List<Pe> peList2 = new ArrayList<>();
						for (int i = 0; i < 8; i++) peList2.add(new Pe(i, new PeProvisionerSimple(1000)));
						hostList.add(new Host(1, new RamProvisionerSimple(16384), new BwProvisionerSimple(20000), 200000, peList2, new VmSchedulerTimeShared(peList2)));

						// Create VMs
						List<Vm> vmlist = new ArrayList<>();
						for (int i = 0; i < 4; i++) {
						    vmlist.add(new Vm(i, brokerId, 1000, 1, 1024, 1000, 10000, "Xen", new CloudletSchedulerTimeShared()));
						}
						broker.submitVmList(vmlist);

						// Create Cloudlets
						List<Cloudlet> cloudletList = new ArrayList<>();
						for (int i = 0; i < 4; i++) {
						    Cloudlet cloudlet = new Cloudlet(i, 2000, 1, 300, 300, new UtilizationModelFull(), new UtilizationModelFull(), new UtilizationModelFull());
						    cloudlet.setUserId(brokerId);
						    cloudlet.setVmId(i); // Assign Cloudlet to a valid VM
						    cloudletList.add(cloudlet);
						}
						broker.submitCloudletList(cloudletList);

						// Start Simulation
						CloudSim.startSimulation();

			// Retrieve results after simulation ends
			List<Cloudlet> newList = broker.getCloudletReceivedList();
			for (Cloudlet cloudlet : newList) {
			    System.out.println("Cloudlet ID: " + cloudlet.getCloudletId() + " Status: " + cloudlet.getStatus());
			    System.out.println("Execution Time: " + cloudlet.getActualCPUTime());
			}
			CloudSim.stopSimulation();

//
//			List<Vm> vmlist = new ArrayList<>();
//			for (int i = 0; i < 4; i++) {
//			    vmlist.add(new Vm(i, brokerId, 1000, 1, 1024, 1000, 10000, "Xen", new CloudletSchedulerTimeShared()));
//			}
//
//			List<Cloudlet> cloudletList = new ArrayList<>();
//			for (int i = 0; i < 4; i++) {
//			    cloudletList.add(new Cloudlet(i, 2000, 1, 300, 300, new UtilizationModelFull(), new UtilizationModelFull(), new UtilizationModelFull()));
//			}
//
//			broker.submitVmList(vmlist);
//			broker.submitCloudletList(cloudletList);
//
//			CloudSim.startSimulation();
//
//			// Retrieve results after simulation ends
//			List<Cloudlet> newList = broker.getCloudletReceivedList();
//
//			// Print results
//			for (Cloudlet cloudlet : newList) {
//			    System.out.println("Cloudlet ID: " + cloudlet.getResourceId() + " Status: " + cloudlet.getStatus());
//			    System.out.println("Execution Time: " + cloudlet.getActualCPUTime());
//			    System.out.println("Total Length: " + cloudlet.getCloudletLength());
//			    System.out.println("Resource Usage: " + cloudlet.getResourceId());
//			    System.out.println();
//			}
//
//			CloudSim.stopSimulation();
//

//
//			// Fourth step: Create one virtual machine
//			vmlist = new ArrayList<Vm>();
//
//			// VM description
//			int vmid = 0;
//			int mips = 1000;
//			long size = 10000; // image size (MB)
//			int ram = 512; // vm memory (MB)
//			long bw = 1000;
//			int pesNumber = 1; // number of cpus
//			String vmm = "Xen"; // VMM name
//
//			// create VM
//			Vm vm = new Vm(vmid, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
//
//			// add the VM to the vmList
//			vmlist.add(vm);
//
//			// submit vm list to the broker
//			broker.submitVmList(vmlist);
//
//			// Fifth step: Create one Cloudlet
//			cloudletList = new ArrayList<Cloudlet>();
//
//			// Cloudlet properties
//			int id = 0;
//			long length = 400000;
//			long fileSize = 300;
//			long outputSize = 300;
//			UtilizationModel utilizationModel = new UtilizationModelFull();
//
//			Cloudlet cloudlet =
//                                new Cloudlet(id, length, pesNumber, fileSize,
//                                        outputSize, utilizationModel, utilizationModel,
//                                        utilizationModel);
//			cloudlet.setUserId(brokerId);
//			cloudlet.setVmId(vmid);
//
//			// add the cloudlet to the list
//			cloudletList.add(cloudlet);
//
//			// submit cloudlet list to the broker
//			broker.submitCloudletList(cloudletList);
//
//			// Sixth step: Starts the simulation
//			CloudSim.startSimulation();
//
//			CloudSim.stopSimulation();
//
//			//Final step: Print results when simulation is over
//			List<Cloudlet> newList = broker.getCloudletReceivedList();
//			printCloudletList(newList);
//
//			Log.printLine("CloudSimExample1 finished!");
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
		List<Host> hostList = new ArrayList<>();
		List<Pe> peList = new ArrayList<>();

		int mips = 1000;
		peList.add(new Pe(0, new PeProvisionerSimple(mips))); // Add processing elements

		int hostId = 0;
		int ram = 2048; // MB
		long storage = 1000000; // MB
		int bw = 10000; // Mbps

		// Create Host and add it to the list
		Host host = new Host(
				hostId,
				new RamProvisionerSimple(ram),
				new BwProvisionerSimple(bw),
				storage,
				peList,
				new VmSchedulerTimeShared(peList)
		);

		hostList.add(host);

		// Ensure the host is correctly assigned
		System.out.println("Created Host: " + host.getId());

		// Define Datacenter characteristics
		String arch = "x86"; // System architecture
		String os = "Linux"; // Operating system
		String vmm = "Xen";
		double timeZone = 10.0;
		double cost = 3.0;
		double costPerMem = 0.05;
		double costPerStorage = 0.001;
		double costPerBw = 0.0;
		LinkedList<Storage> storageList = new LinkedList<>();

		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
				arch, os, vmm, hostList, timeZone, cost, costPerMem, costPerStorage, costPerBw
		);

		Datacenter datacenter = null;
		try {
			datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
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
				+ "Start Time" + indent + "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
				Log.print("SUCCESS");

				Log.printLine(indent + indent + cloudlet.getResourceId()
						+ indent + indent + indent + cloudlet.getVmId()
						+ indent + indent
						+ dft.format(cloudlet.getActualCPUTime()) + indent
						+ indent + dft.format(cloudlet.getExecStartTime())
						+ indent + indent
						+ dft.format(cloudlet.getFinishTime()));
			}
		}
	}
}