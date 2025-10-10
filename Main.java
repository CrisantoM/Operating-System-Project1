import java.io.*;
import java.util.*;

public class Main {
    // Class to represent a process
    static class Process {
        int pid, arrival, burst, waiting, turnaround;

        Process(int pid, int arrival, int burst) {
            this.pid = pid;
            this.arrival = arrival;
            this.burst = burst;
        }
        
        // Create a copy of the process for separate algorithm runs
        Process copy() {
            return new Process(pid, arrival, burst);
        }
    }

    public static void main(String[] args) {
        ArrayList<Process> processes = readProcesses("processes.txt");
        if (processes.isEmpty()) {
            System.out.println("No processes found.");
            return;
        }

        // Make copies for each algorithm
        ArrayList<Process> fcfsList = new ArrayList<>();
        ArrayList<Process> sjfList = new ArrayList<>();
        for (Process p : processes) {
            fcfsList.add(p.copy());
            sjfList.add(p.copy());
        }

        System.out.println("First-Come, First-Served (FCFS)");
        fcfs(fcfsList);

        System.out.println("\nShortest Job First (SJF)");
        sjf(sjfList);
    }

    // Read Processes from file
    static ArrayList<Process> readProcesses(String filename) {
        ArrayList<Process> list = new ArrayList<>();
        try (Scanner sc = new Scanner(new File(filename))) {
            if (sc.hasNextLine()) sc.nextLine(); // skip header

            // retrieve process data from file
            while (sc.hasNext()) {
                int pid = sc.nextInt();
                int arrival = sc.nextInt();
                int burst = sc.nextInt();
                sc.nextInt(); // skip priority
                list.add(new Process(pid, arrival, burst));
            }
        } catch (Exception e) {
            // error handling for file reading
            System.out.println("Error reading file: " + e.getMessage());
        }
        return list;
    }

    // First Come First Served Algorithm
    static void fcfs(ArrayList<Process> list) {
        list.sort(Comparator.comparingInt(p -> p.arrival));
        int time = 0;

        System.out.println("\nGantt Chart:");
        // print process boxes
        for (Process p : list) {
            System.out.print(String.format("| %-3s", "P" + p.pid));
        }
        System.out.println("|");

        // calculate times and print time markers
        System.out.print(String.format("%-4d", 0));
        for (Process p : list) {
            if (time < p.arrival) time = p.arrival;
            p.waiting = time - p.arrival;
            time += p.burst;
            p.turnaround = p.waiting + p.burst;
            System.out.print(String.format("%-4d", time));
        }
        System.out.println();

        printResults(list);
    }

    // Shortest Job First Algorithm (Non-preemptive)
    static void sjf(ArrayList<Process> list) {
        ArrayList<Process> done = new ArrayList<>();
        int time = 0;

        System.out.println("\nGantt Chart:");
        ArrayList<Process> ganttOrder = new ArrayList<>();


        while (done.size() < list.size()) {
            ArrayList<Process> ready = new ArrayList<>();
            for (Process p : list) {
                if (!done.contains(p) && p.arrival <= time) ready.add(p);
            }
            
            // If no process is ready, increment time
            if (ready.isEmpty()) {
                time++;
                continue;
            }

            Process shortest = ready.get(0);
            for (Process p : ready) {
                if (p.burst < shortest.burst) shortest = p;
            }

            shortest.waiting = time - shortest.arrival;
            time += shortest.burst;
            shortest.turnaround = shortest.waiting + shortest.burst;
            done.add(shortest);
            ganttOrder.add(shortest);
        }

        // print process boxes
        for (Process p : ganttOrder) {
            System.out.print(String.format("| %-3s", "P" + p.pid));
        }
        System.out.println("|");

        // print time markers
        time = 0;
        System.out.print(String.format("%-4d", 0));
        for (Process p : ganttOrder) {
            if (time < p.arrival) time = p.arrival;
            time += p.burst;
            System.out.print(String.format("%-4d", time));
        }
        System.out.println();

        printResults(done);
    }

    // Print results
    static void printResults(ArrayList<Process> list) {
        // Print waiting and turnaround times
        double totalWT = 0, totalTAT = 0;
        System.out.println("\nPID\tWaiting\tTurnaround");
        for (Process p : list) {
            System.out.println(p.pid + "\t" + p.waiting + "\t" + p.turnaround);
            totalWT += p.waiting;
            totalTAT += p.turnaround;
        }
        System.out.printf("Average Waiting Time: %.2f\n", totalWT / list.size());
        System.out.printf("Average Turnaround Time: %.2f\n", totalTAT / list.size());
    }
}
