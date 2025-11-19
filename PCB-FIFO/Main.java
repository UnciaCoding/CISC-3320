// Jett McHugh
// CISC 3320 HW 2: Create a FIFO CPU Scheduling simulation
// Modify previous assignment to include arrival time and burst time
// Print average metrics of Turnaround time, Waiting Time, and Response time
// Read from File and populate the queue

//Need to add time functionality
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;
import java.util.Queue;
import java.util.LinkedList;

class Process {
    //Class for the processes in the queue
    int pid, priority, burst, arrival, start;
    String processStatus;
    Process next;

    //Need to add functionality that differentiates time in vs time started
        // Tracks wait time, end time,  turnaround, and response time
    //Constructor, the only type we'll need
    public Process(int pid, String processStatus, int priority, int burst, int arrival) {
        this.pid = pid;
        this.processStatus = processStatus;
        this.priority = priority;
        this.burst= burst;
        this.arrival = arrival;
        this.next = null;
    }

    //Overwrite print to string method for the sake of printing the queue
    public String toString() {
        return this.pid +  " " + this.burst + " " + this.arrival;
    }

    //Getters
    public int getPid() { return this.pid; }
    public int getBurst(){ return this.burst; }
    public int getArrival(){ return this.arrival; }

}

//Add functionality that prints out pid, burst, arrival, completion, turnaround, waiting, and repsonse time
//Add functionality to calculate turnaround, waiting, and response time
public class Main {
    public static void main(String[] args) throws Exception {
        //Code below left from testing purposes
            //Process p1 = new Process(123, "HELD", 0);
            //System.out.println(p1);
        //variables to declare
        int pid, prio, burst, arrival, time, startTime, completeTime, numProcesses, totalTT, totalWT, totalRT;
        String status;
        Process p;
        //Import from File
        File file = new File("data2");
        Scanner fscan = new Scanner(new FileReader(file));
        Queue<Process> pcb = new LinkedList<>();
        //ProcessControlBlock pcb = new ProcessControlBlock();
        totalTT=0;
        totalRT=0;
        totalWT=0;

        while(fscan.hasNext()){
            pid = fscan.nextInt();
            status=fscan.next();
            prio= fscan.nextInt();
            burst = fscan.nextInt();
            arrival = fscan.nextInt();
            pcb.offer(new Process(pid, status,prio, burst, arrival));
        }
        fscan.close();
        numProcesses = pcb.size();
        System.out.println("PID   BURST ARRIVAL COMPLETION TURNAROUND WAITING RESPONSE");
        time = 0;
        while(!pcb.isEmpty()){
            startTime=time;
            p = pcb.remove();
            if(time<p.getArrival()){ time = p.getArrival(); }
            time += p.getBurst();
            completeTime = time;
            totalWT += startTime - p.getArrival();
            totalTT += completeTime - p.getArrival();
            totalRT += startTime - p.getArrival();
            System.out.printf("%-7d%-6d%-8d%-11d%-11d%-8d%-8d%n", p.getPid(),p.getBurst(),p.getArrival(),completeTime, completeTime - p.getArrival(),startTime - p.getArrival(),startTime - p.getArrival());
        }
        System.out.println("Average Turnaround Time: " + totalTT/numProcesses);
        System.out.println("Average Waiting Time: " + totalWT/numProcesses);
        System.out.println("Average Response Time: " + totalRT/numProcesses);
    }
}
