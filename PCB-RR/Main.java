// Jett McHugh
// CISC 3320 HW 3: Create a Round Robin and a Shortest Job First CPU Scheduling simulation
// Modify previous assignment to switch from FIFO to RR / SJF.
// This file is RR
// Print average metrics of Turnaround time, Waiting Time, and Response time
// Read from File and populate the queue

import java.io.File;
import java.io.FileReader;
import java.util.Scanner;
import java.util.Queue;
import java.util.LinkedList;

class Process {
    //Class for the processes in the queue
    int pid, priority, burst, arrival, remainingBurst, start;
    boolean hasHadTurn;
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
        this.remainingBurst=burst;
        this.arrival = arrival;
        this.hasHadTurn = false;
    }

    //Overwrite print to string method for the sake of printing the queue
    public String toString() {
        return this.pid +  " " + this.burst + " " + this.arrival;
    }

    //Getters
    public int getPid() { return this.pid; }
    public int getBurst(){ return this.burst; }
    public int getArrival(){ return this.arrival; }
    public int getRemainingBurst() { return this.remainingBurst; }
    public int getStart() { return this.start; }
    public boolean hadTurn() { return this.hasHadTurn; }

    //Setters
    public void setRemainingBurst(int x) { this.remainingBurst = x; }
    public void setHasHadTurn(int firstStart) {
        this.hasHadTurn = true;
        this.start = firstStart;
    }
}

// Implement a Round Robin CPU Scheduling
// Time Slice = 2 Units
// Round Robin Scheduling: Each process gets a certain time slot to run before the next process is allowed to run
// Rotates through each of the processes until all are finished
// -> Each process runs for 2 time units before being kicked to the back of the queue
// Total waiting time = Completion time - Arrival Time - Burst
public class Main {
    public static void main(String[] args) throws Exception {
        //variables to declare
        int pid, prio, burst, arrival, time, startTime, completeTime, numProcesses, totalTT, totalWT, totalRT;
        String status;
        Process p;
        //Import from File
        File file = new File("data2");
        Scanner fscan = new Scanner(new FileReader(file));
        Queue<Process> pcb = new LinkedList<>();
        boolean finished = false;
        //ProcessControlBlock pcb = new ProcessControlBlock();
        totalTT=0;
        totalRT=0;
        totalWT=0;

        //File reads in format: PID STATUS PRIORITY BURST ARRIVAl
        while(fscan.hasNext()){
            pid = fscan.nextInt();
            status=fscan.next();
            prio= fscan.nextInt();
            burst = fscan.nextInt();
            arrival = fscan.nextInt();
            pcb.offer(new Process(pid, status, prio, burst, arrival));
        }
        fscan.close();
        // NOTE: The above queue can't be used for the actual CPU scheduling, can consider it just the arrival queue
        // There are significant gaps between arrival times of processes that cause logical errors in the queue running
        // Code will comb straight through Queue, ignoring that certain processes would get more turns while waiting for new processes to get added
        // Need to create a new queue, keep track of simulated time
        // Pop processes off of the initial queue as they "arrive", push them onto the CPU schedule
        numProcesses = pcb.size();

        Queue<Process> cpu = new LinkedList<>();
        System.out.println("PID   BURST ARRIVAL COMPLETION TURNAROUND WAITING RESPONSE");
        time = 0;
        while(!finished){
            // Preemptive Arrival Queue:
            // Check if the "arrival" queue is empty
            if(!pcb.isEmpty()){
                //If arrival queue not empty, check if next process has arrived
                if(pcb.peek().getArrival() <= time){
                    //If next process has arrived, pop off arrival queue, push onto cpu
                    cpu.offer(pcb.remove());
                }
            }
            if(!cpu.isEmpty()) {
                // Pop next process off CPU Queue
                p = cpu.remove();
                // Check if process already had a turn
                if (!p.hadTurn()) {
                    //If Process HAS NOT had turn, set "start" time to current time
                    p.setHasHadTurn(time);
                }
                if (p.getRemainingBurst() <= 2) { // If Process would complete in the time slice
                    // Increment Time, set completeTime (for readability)
                    time += p.getRemainingBurst();
                    completeTime = time;
                    // Outputting PID, Burst, Arrival Time, Completion Time, Turnaround time, Waiting Time, Response Time
                    System.out.printf("%-7d%-6d%-8d%-11d%-11d%-8d%-8d%n", p.getPid(), p.getBurst(), p.getArrival(), completeTime,
                            completeTime - p.getArrival(), completeTime - p.getArrival() - p.getBurst(),
                            p.getStart() - p.getArrival());
                    totalTT += completeTime - p.getArrival();
                    totalWT += completeTime - p.getArrival() - p.getBurst();
                    totalRT += p.getStart() - p.getArrival();
                } else { //If Process would NOT complete in time slice
                    // Increment time
                    time += 2;
                    // Decrement Remaining Burst time
                    p.setRemainingBurst(p.getRemainingBurst() - 2);
                    // Put process at back of queue
                    cpu.offer(p);
                }
            }else{ //if nothing is on CPU, but there is still stuff on arrival queue, increment time to try and catch a
                  // new process's arrival time
                time++;
            }
            if(pcb.isEmpty() && cpu.isEmpty()) {
                finished = true;
            }
        }

        // Print out averages
        System.out.println("Average Turnaround Time: " + totalTT/numProcesses);
        System.out.println("Average Waiting Time: " + totalWT/numProcesses);
        System.out.println("Average Response Time: " + totalRT/numProcesses);
    }
}
