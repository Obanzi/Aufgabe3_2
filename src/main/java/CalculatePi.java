import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;


//Here we create the result class
class ResultWorkerPackages {
    private final int dartCount;
    private final int hits;

    public ResultWorkerPackages(int dartCount, int hits) {
        this.dartCount = dartCount;
        this.hits = hits;
    }

    public int getDartCount() {
        return dartCount;
    }

    public int getHits() {
        return hits;
    }

    /*public void setDartCount(int dartCount) {
        this.dartCount = dartCount;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }*/

} // end ResultWorkerPackages


class ResultWorker implements Callable<ResultWorkerPackages> {
    private final int dartCount;

    public ResultWorker(int dartCount, int i) {
        this.dartCount = dartCount;
    }

    public ResultWorkerPackages call() throws IllegalCallerException {
        int hits = 0;

        //Here we calculate the darts per worker and the darts left
        for (int i = 0; i < dartCount; i++) {
            double x = ThreadLocalRandom.current().nextDouble(0, 1);
            double y = ThreadLocalRandom.current().nextDouble(0, 1);
            if (x * x + y * y <= 1) {
                hits++;
            }
        }
        //Here we return the result
        return new ResultWorkerPackages(dartCount, hits);
    } // end call
} // end ResultWorker

class CalculatePi {

    public void calculate() throws InterruptedException, ExecutionException {

        //Here we calculate the darts per worker and the darts left
        int dartCount1 = 100000000;
        int workerCount = 30;
        long dartsperworker = dartCount1 / workerCount;
        long dartsleft = dartCount1 % workerCount;

        //Here we create the executor
        int threadCount = 30;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        // Here we create the list to hold the Future object associated with Callable
        List<Future<ResultWorkerPackages>> futures = new ArrayList<>();

        // Here we create the workers and submit them to the executor
        for (int i = 0; i < workerCount; i++) {
            ResultWorker worker = new ResultWorker((int) dartsperworker, 0);
            Future<ResultWorkerPackages> future = executor.submit(worker);
            futures.add(future);
        } // end for

        int hits = 0;
        int dartCount = 0;

        //Here we get the results from the workers
        for (Future<ResultWorkerPackages> future : futures) {
            try {
                ResultWorkerPackages result = future.get();
                hits += result.getHits();
                dartCount += result.getDartCount();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } // end try
        }   // end for

        //Here we calculate the pi value
        float pi = 4.0f * hits / dartCount;
        System.out.println("Pi: " + pi);
        System.out.println("Darts: " + dartCount1);
        System.out.println("Hits: " + hits);
        System.out.println("Workers: " + workerCount);
        System.out.println("Threads: " + threadCount);

        //Here we shutdown the executor
        executor.shutdown();

    } // end calculate

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        CalculatePi calculatePi = new CalculatePi();
        long startTime = System.currentTimeMillis();
        calculatePi.calculate();
        long endTime = System.currentTimeMillis();
        System.out.println("Time: " + (endTime - startTime) + "ms");
    } // end main
} // end CalculatePi