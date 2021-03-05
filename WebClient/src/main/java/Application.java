import networking.WebClient;

import java.util.Arrays;
import java.util.List;

public class Application {
    private static final String WORKER_ADD_1 = "http://localhost:8081/task";
    private static final String WORKER_ADD_2 = "http://localhost:8082/task";

    public static void main(String[] args) {
        String task1 = "500,100";
        String task2 = "111,22,333";

        Aggregator aggregator = new Aggregator(new WebClient());
        List<String> strings = aggregator.sendTaskToWorkers(Arrays.asList(WORKER_ADD_1, WORKER_ADD_2),
                Arrays.asList(task1, task2));

        for (int i = 0; i < strings.size(); i++) {
            System.out.println(strings.get(i));
        }
    }

}
