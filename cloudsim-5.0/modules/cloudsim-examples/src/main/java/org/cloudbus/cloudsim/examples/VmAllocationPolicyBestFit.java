package org.cloudbus.cloudsim.examples;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VmAllocationPolicyBestFit extends VmAllocationPolicy {
    private final Map<Vm, Host> vmTable; // Store allocated VMs and their Hosts

    public VmAllocationPolicyBestFit(List<? extends Host> hostList) {
        super(hostList);
        this.vmTable = new HashMap<>();
    }

    @Override
    public boolean allocateHostForVm(Vm vm) {
        Host bestHost = findBestFitHost(vm);
        if (bestHost != null) {
            return allocateHostForVm(vm, bestHost);
        }
        return false;
    }

    @Override
    public boolean allocateHostForVm(Vm vm, Host host) {
        if (host != null && host.vmCreate(vm)) {
            vmTable.put(vm, host); // Store allocation
            return true;
        }
        return false;
    }

    @Override
    public void deallocateHostForVm(Vm vm) {
        Host host = vmTable.remove(vm);
        if (host != null) {
            host.vmDestroy(vm);
        }
    }

    @Override
    public Host getHost(Vm vm) {
        return vmTable.get(vm);
    }

    @Override
    public Host getHost(int vmId, int userId) {
        for (Host host : getHostList()) {
            for (Vm vm : host.getVmList()) {
                if (vm.getId() == vmId && vm.getUserId() == userId) {
                    return host;
                }
            }
        }
        return null;
    }

    @Override
    public List<Map<String, Object>> optimizeAllocation(List<? extends Vm> vmList) {
        return null; // No optimization for now
    }

    private Host findBestFitHost(Vm vm) {
        Host bestHost = null;
        double minUtilization = Double.MAX_VALUE;

        for (Host host : getHostList()) {
            double utilization = (host.getTotalMips() - host.getAvailableMips()) / host.getTotalMips();
            if (utilization < minUtilization && host.isSuitableForVm(vm)) {
                bestHost = host;
                minUtilization = utilization;
            }
        }
        return bestHost;
    }
}
