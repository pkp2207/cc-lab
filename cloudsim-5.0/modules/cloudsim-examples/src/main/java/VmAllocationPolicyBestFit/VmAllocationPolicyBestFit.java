//package org.cloudbus.cloudsim.examples;
//
//import org.cloudbus.cloudsim.VmAllocationPolicy;
//import org.cloudbus.cloudsim.Vm;
//import org.cloudbus.cloudsim.Host;
//import java.util.List;
//
//public class VmAllocationPolicyBestFit extends VmAllocationPolicy {
//    public VmAllocationPolicyBestFit(List<? extends Host> hostList) {
//        super(hostList);
//    }
//
//    @Override
//    public boolean allocateHostForVm(Vm vm) {
//        Host bestHost = null;
//        double minUtilization = Double.MAX_VALUE;
//
//        for (Host host : getHostList()) {
//            double utilization = (double) host.getUtilizationOfCpu() / host.getTotalMips();
//            if (utilization < minUtilization && host.isSuitableForVm(vm)) {
//                bestHost = host;
//                minUtilization = utilization;
//            }
//        }
//
//        if (bestHost != null) {
//            return bestHost.vmCreate(vm);
//        }
//        return false;
//    }
//
//    @Override
//    public void deallocateHostForVm(Vm vm) {
//        if (vm.getHost() != null) {
//            vm.getHost().vmDestroy(vm);
//        }
//    }
//}
