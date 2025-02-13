package org.cloudbus.cloudsim.examples;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.CloudletScheduler;
import java.util.List;

public class CustomBroker extends DatacenterBroker {

    public CustomBroker(CloudSim simulation, String name) throws Exception {
        super(name);  // Pass the broker name as a string
    }

    // Override bindCloudletToVm to define custom behavior for binding
//    @Override
    protected void bindCloudletToVm(Cloudlet cloudlet, Vm vm) {
        // Set the VM for the cloudlet, so the scheduler can handle it
        cloudlet.setVmId(vm.getId());
        CloudletScheduler scheduler = vm.getCloudletScheduler();
        if (scheduler != null) {
            // CloudSim automatically handles cloudlet execution once bound to a VM
            scheduler.cloudletSubmit(cloudlet);  // This is the correct method to submit a cloudlet
        } else {
            System.out.println("CloudletScheduler is not available for VM " + vm.getId());
        }
    }

    // Override processCloudletSubmit to assign cloudlets to VMs
//    @Override
    public void processCloudletSubmit(Cloudlet cloudlet) {
        List<Vm> vmList = getVmList();
        Vm selectedVm = selectVmForCloudlet(cloudlet, vmList);
        if (selectedVm != null) {
            bindCloudletToVm(cloudlet, selectedVm);
        } else {
            // Handle the case where no VM is available (e.g., queuing or printing an error)
            System.out.println("No suitable VM found for Cloudlet " + cloudlet.getCloudletId());
        }
    }

    // First-Fit Allocation Logic: Assign cloudlet to the first available VM
    private Vm selectVmForCloudlet(Cloudlet cloudlet, List<Vm> vmList) {
        for (Vm vm : vmList) {
            CloudletScheduler scheduler = vm.getCloudletScheduler();
            // Check the number of cloudlets the VM is already processing (adjust according to your implementation)
            if (scheduler.getCloudletExecList().size() < 10) { // Assuming max cloudlets for a VM is 10
                // Found the first VM that has capacity for the cloudlet
                return vm;
            }
        }
        // If no VM found, return null (or implement further handling)
        return null;
    }
}