package controller;

import exceptions.MyException;
import model.state.ProgramState;
import repository.IRepository;
import model.value.Value;
import model.value.RefValue;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Controller {
    private final IRepository repo;

    //
    private ExecutorService executor;
    //a method that is used to run the one-step execution of multiple ProgramState in parallel (allows multi-threading in this program)
    //all programs advance one step at the same time

    public Controller(IRepository repo) {
        this.repo = repo;
    }

    //
    private List<ProgramState> removeCompletedPrg(List<ProgramState> inPrgList) {
        return inPrgList.stream()
                .filter(ProgramState::isNotCompleted)
                .collect(Collectors.toList());
    }
    /*
    * - this method only returns a list of the program states that are still active and not yet completed
     */


    //
    public void oneStepForAllPrg(List<ProgramState> prgList) throws InterruptedException {
        List<ProgramState> active = removeCompletedPrg(prgList);
        if (active.isEmpty()) {
            // nothing to run — do not overwrite the repo with an empty list here
            return;
        }

        for (ProgramState p : active) {
            try { repo.logProgramState(p); } catch (MyException ignored) {}
        }

        List<Callable<ProgramState>> callList = active.stream()
                .map((ProgramState p) -> (Callable<ProgramState>) () -> {
                    try {
                        return p.oneStep();
                    } catch (Exception e) {
                        String msg = (e.getMessage() != null) ? e.getMessage()
                                : (e.getCause() != null ? e.getCause().getMessage() : "null");
                        if (!"prgstate stack is empty".equalsIgnoreCase(msg)) {
                            System.err.println("Unexpected exception in oneStep: " + msg);
                            e.printStackTrace();
                        }
                        return null;
                    }
                })
                .collect(Collectors.toList());

        if (executor == null || executor.isShutdown()) {
            executor = Executors.newFixedThreadPool(Math.min(2, Math.max(1, active.size())));
        }

        List<ProgramState> newCreated = new ArrayList<>();
        var futures = executor.invokeAll(callList);
        for (var future : futures) {
            try {
                ProgramState result = future.get();
                if (result != null) newCreated.add(result);
            } catch (Exception e) {
                String causeMsg = (e.getCause() != null) ? e.getCause().getMessage() : e.getMessage();
                if (!"prgstate stack is empty".equalsIgnoreCase(causeMsg)) {
                    System.err.println("Exception in thread execution: " + causeMsg);
                    e.printStackTrace();
                }
            }
        }

        List<ProgramState> nextList = new ArrayList<>();
        for (ProgramState p : active) {
            if (p.isNotCompleted()) nextList.add(p);
        }

        for (ProgramState p : newCreated) {
            if (p != null && p.isNotCompleted()) {
                boolean present = false;
                for (ProgramState q : nextList) if (q == p) { present = true; break; }
                if (!present) nextList.add(p);
            }
        }

        for (ProgramState p : nextList) {
            try { repo.logProgramState(p); } catch (MyException ignored) {}
        }

        // keep the last executed program(s) in the repo when nextList would be empty
        repo.setPrgList(nextList.isEmpty() ? active : nextList);
    }

    /*
    *filter out finished ProgramState objects; if none, save and return
    * log each active ProgramState (ignore logging errors)
    * make a Callable per active ProgramState that calls p.oneStep() and returns a spawned state or null
    *   - catch exceptions; ignore the expected "prgstate stack is empty" message, print others
    * create a small ExecutorService if needed (1-2 threads)
    * invokeAll tasks, collect non-null results into newCreated
    * build nextList with active states that remain not completed
    * merge newCreated into nextList if not already present (reference equality)
    * log each ProgramState in nextList and update repository
     */

    //
    public void allStep() throws MyException, InterruptedException {
        executor = Executors.newFixedThreadPool(2);
        repo.clearLogFile();
        List<ProgramState> prgList = removeCompletedPrg(repo.getPrgList());

        while (!prgList.isEmpty()) {
            conservativeGarbageCollector(prgList);
            oneStepForAllPrg(prgList);
            prgList = removeCompletedPrg(repo.getPrgList());
        }

        if (executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
        }
        repo.setPrgList(prgList);
    }
    /*
     * -runs all ProgramState instances to completion.
     * - initializes a fixed-size thread pool (2 threads) used by one-step execution.
     * - clears the repository log file so this run starts fresh.
     * - on a loop
     *     - performs a conservative garbage collection based on reachable addresses.
     *     - executes one step for each active ProgramState (can run in parallel and can create new states).
     *     - reloads the active program list from the repository (to account for spawned states).
     * - after the loop, shuts down the executor and saves the final program list to the repository.
     *
     */

    //
    private void conservativeGarbageCollector(List<ProgramState> prgList) {
        if (prgList == null || prgList.isEmpty()) return;

        List<Value> allSymVals = new ArrayList<>();
        for (ProgramState p : prgList) {
            if (p == null || p.getSymTable() == null || p.getSymTable().getContent() == null) continue;
            for (Object o : p.getSymTable().getContent().values()) {
                if (o instanceof Value) {
                    allSymVals.add((Value) o);
                }
            }
        }

        Map<Integer, Value> heap = prgList.get(0).getHeap() == null ? null : prgList.get(0).getHeap().getContent();
        if (heap == null || heap.isEmpty()) {
            if (prgList.get(0).getHeap() != null) {
                prgList.get(0).getHeap().setContent(new HashMap<>());
            }
            return;
        }

        Map<Integer, Value> newHeap = safeGarbageCollector(allSymVals, heap);
        prgList.get(0).getHeap().setContent(newHeap);
    }
    /*
    if prgList is null or empty, do nothing.
    gather every value from every program state symbol table into a list allSymVals.
    read the shared heap map from prgList.get(0). If the map is null or empty, ensure an empty map and return.
    call safeGarbageCollector(allSymVals, heap) which keeps only heap entries whose addresses are reachable from the collected values.
    replace the shared heap content with the filtered map.
     */

    private Map<Integer, Value> safeGarbageCollector(Collection<Value> symTableValues, Map<Integer, Value> heap) {
        Set<Integer> reachable = getReachableAddresses(symTableValues, heap);
        return heap.entrySet().stream()
                .filter(e -> reachable.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Set<Integer> getReachableAddresses(Collection<Value> symTableValues, Map<Integer, Value> heap) {
        Set<Integer> reachable = new HashSet<>();
        Deque<Integer> stack = new ArrayDeque<>();

        for (Value v : symTableValues) {
            if (v instanceof RefValue) {
                int addr = ((RefValue) v).getAddress();
                if (heap.containsKey(addr) && reachable.add(addr)) {
                    stack.push(addr);
                }
            }
        }

        while (!stack.isEmpty()) {
            int addr = stack.pop();
            Value val = heap.get(addr);
            if (val instanceof RefValue) {
                int inner = ((RefValue) val).getAddress();
                if (heap.containsKey(inner) && reachable.add(inner)) {
                    stack.push(inner);
                }
            }
        }
        return reachable;
    }

    public List<ProgramState> getProgramStates() {
        return repo.getPrgList();
    }
}