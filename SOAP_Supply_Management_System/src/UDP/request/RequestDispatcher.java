package UDP.request;

import java.util.List;

public interface RequestDispatcher {
    List<String> broadcastCollect(List<String> args);
    List<String> unicast(List<String> args, String destinationName);
}
