
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class graphalgo {
    List<String> graph;
    static int[][] cap;

    public static void main(String args[]) throws FileNotFoundException {

        String source = "s",sink = "t";
        List<String> nodes = new LinkedList<>();
        graphalgo calculatemaxflow = new graphalgo();
/**
 *
 *CHANGED HERE TO 'random2.txt'
 **/
        File inputfile = new File(args[0]);
        Scanner sc =new Scanner(inputfile);
        //Calculating the numer of nodes
        nodes.add("s");

        while(sc.hasNext()){
            String u = sc.next();
            String v = sc.next();
            if (!nodes.contains(u)){
                nodes.add(u);
            }
            if (!nodes.contains(v)) {
                nodes.add(v);
            }
            sc.nextLine();
        }
        sc.close();
        int[][] capacity = new int[nodes.size()][nodes.size()];
/**
 *
 *CHANGED HERE TO 'random2.txt'
 **/
        // Creating the capacity 2D matrix to store the cap values
        Scanner sc1 = new Scanner(inputfile);
        while(sc1.hasNext()){
            String u = sc1.next();
            String v = sc1.next();
            int cap = sc1.nextInt();
            capacity[nodes.indexOf(u)][nodes.indexOf(v)] = cap;
        }
        sc1.close();
        System.out.println("Ford-Fulkerson Algorithm------");
        long intimeff = System.currentTimeMillis();
        /** Method Call to Ford Fulkerson algorithm*/
        System.out.println("\tMaximum Flow: " + calculatemaxflow.ffmaxFlow(nodes,capacity, nodes.indexOf(source), nodes.indexOf(sink)));
        System.out.println("\tRunning time: " + (System.currentTimeMillis()-intimeff));

        System.out.println("Scaling Ford-Fulkerson Algorithm------");
        long intimescale = System.currentTimeMillis();
        /** Method Call to Scaling Ford Fulkerson algorithm*/
        System.out.println("\tMaximum Flow: " + calculatemaxflow.scalingffmaxFlow(nodes,capacity, nodes.indexOf(source), nodes.indexOf(sink)));
        System.out.println("\tRunning time: " + (System.currentTimeMillis()-intimescale));

        System.out.println("Preflow-pish Algorithm------");
        long intimepfp = System.currentTimeMillis();
        /** Method Call to preflow push*/
        pushRelabelFlow(capacity);
        System.out.println("\tMaximum Flow:" + pushRelabelFlow(capacity));
        System.out.println("\tRunning time:" + (System.currentTimeMillis()-intimepfp));
    }

    public int scalingffmaxFlow(List<String> nodes, int[][] capacity, int source, int sink) {
        graph = nodes;
        //Creating residual capacity
        int resCap[][] = new int[capacity.length][capacity[0].length];
        for (int i = 0; i < capacity.length; i++) {
            for (int j = 0; j < capacity[0].length; j++) {
                resCap[i][j] = capacity[i][j];
            }
        }
        int delta;
        Map<Integer,Integer> parent = new HashMap<>();
        List<List<Integer>> augmentedPaths = new ArrayList<>();

        //Finding Delta value
        delta = getdelta(capacity);
        int maxFlow = 0;
        while(delta>=1) {
            while (BFS(delta, resCap, parent, source, sink))
            {
                List<Integer> augmentedPath = new ArrayList<>();
                int flow = Integer.MAX_VALUE;
                int v = sink;
                while (v != source)
                {
                    augmentedPath.add(v);
                    int u = parent.get(v);
                    //Checking the value of the edge capacity with the flow
                    if (flow > resCap[u][v])
                    {
                        flow = resCap[u][v];
                    }
                    v = u;
                }
                augmentedPath.add(source);
                Collections.reverse(augmentedPath);
                augmentedPaths.add(augmentedPath);

                maxFlow += flow;

                v = sink;
                //updating the residual capacity matrix
                while (v != source) {
                    int u = parent.get(v);
                    resCap[u][v] -= flow;
                    resCap[v][u] += flow;
                    v = u;
                }
            }
            delta = delta/2;
        }
        return maxFlow;
    }

    public int ffmaxFlow(List<String> nodes, int[][] capacity, int source, int sink) {
        graph = nodes;
        int resCap[][] = new int[capacity.length][capacity[0].length];
        for (int i = 0; i < capacity.length; i++) {
            for (int j = 0; j < capacity[0].length; j++) {
                resCap[i][j] = capacity[i][j];
            }
        }
        Map<Integer,Integer> parentmap= new HashMap<>();
        List<List<Integer>> augmentedPaths = new ArrayList<>();
        int maxFlow = 0;
        int compare = 0;
        while(BFS(compare, resCap, parentmap, source, sink))
        {
            List<Integer> augmentedPath = new ArrayList<>();
            int flow = Integer.MAX_VALUE;
            int v = sink;
            while(v != source){
                augmentedPath.add(v);
                int u = parentmap.get(v);
                if (flow > resCap[u][v]) {
                    flow = resCap[u][v];
                }
                v = u;
            }
            augmentedPath.add(source);
            Collections.reverse(augmentedPath);
            augmentedPaths.add(augmentedPath);
            maxFlow += flow;
            v = sink;
            while(v != source){
                int u = parentmap.get(v);
                resCap[u][v] -= flow;
                resCap[v][u] += flow;
                v = u;
            }
        }
        //AugmentedPaths(augmentedPaths);
        return maxFlow;
    }

    /**
     * Push Relabel*/
    public static int pushRelabelFlow(int[][] capacity){
        int n = capacity.length;
        init(n);
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (capacity[i][j] != 0)
                    addEdge(i, j, capacity[i][j]);
        return maxFlow(0, capacity.length - 1);
    }

    /**INIT method*/
    public static void init(int nodes) {
        cap = new int[nodes][nodes];
    }

    /** Add Edge method */
    public static void addEdge(int s, int t, int capacity) {
        cap[s][t] = capacity;
    }

    /**max flow calculating method*/
    public static int maxFlow(int s, int t) {
        int n = cap.length;

        int[] h = new int[n];
        h[s] = n - 1;

        int[] maxh = new int[n];

        int[][] f = new int[n][n];
        int[] e = new int[n];

        for (int i = 0; i < n; ++i) {
            f[s][i] = cap[s][i];
            f[i][s] = -f[s][i];
            e[i] = cap[s][i];
        }

        for (int sz = 0;;) {
            if (sz == 0) {
                for (int i = 0; i < n; ++i)
                    if (i != s && i != t && e[i] > 0) {
                        if (sz != 0 && h[i] > h[maxh[0]])
                            sz = 0;
                        maxh[sz++] = i;
                    }
            }
            if (sz == 0)
                break;
            while (sz != 0) {
                int i = maxh[sz - 1];
                boolean pushed = false;
                for (int j = 0; j < n && e[i] != 0; ++j) {
                    if (h[i] == h[j] + 1 && cap[i][j] - f[i][j] > 0) {
                        int df = Math.min(cap[i][j] - f[i][j], e[i]);
                        f[i][j] += df;
                        f[j][i] -= df;
                        e[i] -= df;
                        e[j] += df;
                        if (e[i] == 0)
                            --sz;
                        pushed = true;
                    }
                }
                if (!pushed) {
                    h[i] = Integer.MAX_VALUE;
                    for (int j = 0; j < n; ++j)
                        if (h[i] > h[j] + 1 && cap[i][j] - f[i][j] > 0)
                            h[i] = h[j] + 1;
                    if (h[i] > h[maxh[0]]) {
                        sz = 0;
                        break;
                    }
                }
            }
        }

        int flow = 0;
        for (int i = 0; i < n; i++)
            flow += f[s][i];
        return flow;
    }


    private boolean BFS(int delta, int[][] resCap, Map<Integer, Integer> parentmap, int source, int sink) {
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        queue.add(source);
        visited.add(source);
        boolean foundAugmentedPath = false;
        while(!queue.isEmpty()){
            int u = queue.poll();
            for(int v = 0; v < resCap.length; v++){
                if(delta == 0){
                    if(!visited.contains(v) &&  resCap[u][v] > 0){
                        parentmap.put(v, u);
                        visited.add(v);
                        queue.add(v);
                        if ( v == sink) {
                            foundAugmentedPath = true;
                            break;
                        }
                    }
                }
                else{
                    if(!visited.contains(v) &&  resCap[u][v] >= delta){
                        parentmap.put(v, u);
                        visited.add(v);
                        queue.add(v);
                        if ( v == sink) {
                            foundAugmentedPath = true;
                            break;
                        }
                    }
                }
            }
        }
        return foundAugmentedPath;
    }

    private int getdelta(int[][] capacity) {
        int maxC=0;
        int delta =0;

        int maxvalue =0;
        for (int i = 0; i < capacity.length; i++) {
            for (int j = 0; j < capacity[i].length; j++) {
                if (capacity[i][j] > maxvalue) {
                    maxvalue = capacity[i][j];
                }
            }
            maxC = Math.max(maxC,maxvalue);
        }
        while((delta*=2)<=maxC) {
            return maxC;
        }
        return delta/2;
    }
}
