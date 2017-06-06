package hardware;

import java.util.*;

public class iSLIP implements IScheduler {

    private int numberOfOutputs;
    private int numberOfInputs;

    List<Set<Integer>> requests = new ArrayList<Set<Integer>>();

    public iSLIP(int numberOfInputs, int numberOfOutputs) {

        this.numberOfOutputs = numberOfOutputs;
        this.numberOfInputs = numberOfInputs;
    }

    @Override
    public Map<Integer, Integer> defineConnections(Switch _switch) {

        /* jakie wejscia są niepodłączone */
        Set<Integer> inputs = _switch.getDisconnectedInputs();
        /* i wyjscia */
        Set<Integer> outputs = _switch.getDisconnectedOutputs();

        /* dla kazdego wejscia, moge sie dowiedziec jakie pakiety czekaja na dane wyjscie */
        /* ale mam wiedziec, dla ktorego wyjscia jakie wejscia chca sie z nim polaczyc */

        /* inicjalizacja pustego zbioru requestów */
        for(int i=0; i < this.numberOfOutputs; i++)
        {
            requests.add(new HashSet<Integer>());
        }

        for (Integer input : inputs) {
            Set<Integer> outputsForInput = _switch.inputWaitsForOutputs(input);

            for (int output : outputsForInput) {
                requests.get(output).add(input);
            }
        }

        return new HashMap<Integer, Integer>();
    }
}
