import networking.WebClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Aggregator {
    private WebClient webClient;

    public Aggregator(WebClient webClient) {
        this.webClient = webClient;
    }

    public List<String> sendTaskToWorkers(List<String> workerAddresses, List<String> tasks){
        CompletableFuture<String>[] completableFutures = new CompletableFuture[tasks.size()];

        for (int i = 0; i < tasks.size(); i++) {
            String workerAddress = workerAddresses.get(i);
            String task = tasks.get(i);

            byte[] requestPayload = task.getBytes();

            completableFutures[i] = webClient.sendTask(workerAddress,requestPayload);
        }

        List<String> collect = Stream.of(completableFutures).map(CompletableFuture::join).collect(Collectors.toList());

        return collect;
    }
}
