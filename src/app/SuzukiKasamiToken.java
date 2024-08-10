package app;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SuzukiKasamiToken implements Serializable {

    private Map<Integer, Integer> tokenMap;

    private List<Integer> requestList;

    public SuzukiKasamiToken (Map<Integer, Integer> tokenMap, List<Integer> requestList) {
        this.tokenMap = tokenMap;
        this.requestList = requestList;
    }

    public List<Integer> getRequestList() {
        return requestList;
    }

    public Map<Integer, Integer> getTokenMap() {
        return tokenMap;
    }
}
