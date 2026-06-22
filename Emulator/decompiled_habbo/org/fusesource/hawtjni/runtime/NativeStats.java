/*
 * Decompiled with CFR 0.152.
 */
package org.fusesource.hawtjni.runtime;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class NativeStats {
    private final HashMap<StatsInterface, ArrayList<NativeFunction>> snapshot;

    public NativeStats(StatsInterface ... classes) {
        this(Arrays.asList(classes));
    }

    public NativeStats(Collection<StatsInterface> classes) {
        this(NativeStats.snapshot(classes));
    }

    private NativeStats(HashMap<StatsInterface, ArrayList<NativeFunction>> snapshot) {
        this.snapshot = snapshot;
    }

    public void reset() {
        for (ArrayList<NativeFunction> functions : this.snapshot.values()) {
            for (NativeFunction function : functions) {
                function.reset();
            }
        }
    }

    public void update() {
        for (Map.Entry<StatsInterface, ArrayList<NativeFunction>> entry : this.snapshot.entrySet()) {
            StatsInterface si = entry.getKey();
            for (NativeFunction function : entry.getValue()) {
                function.setCounter(si.functionCounter(function.getOrdinal()));
            }
        }
    }

    public NativeStats snapshot() {
        NativeStats copy = this.copy();
        copy.update();
        return copy;
    }

    public NativeStats copy() {
        HashMap<StatsInterface, ArrayList<NativeFunction>> rc = new HashMap<StatsInterface, ArrayList<NativeFunction>>(this.snapshot.size() * 2);
        for (Map.Entry<StatsInterface, ArrayList<NativeFunction>> entry : this.snapshot.entrySet()) {
            ArrayList<NativeFunction> list = new ArrayList<NativeFunction>(entry.getValue().size());
            for (NativeFunction function : entry.getValue()) {
                list.add(function.copy());
            }
            rc.put(entry.getKey(), list);
        }
        return new NativeStats(rc);
    }

    public NativeStats diff() {
        HashMap<StatsInterface, ArrayList<NativeFunction>> rc = new HashMap<StatsInterface, ArrayList<NativeFunction>>(this.snapshot.size() * 2);
        for (Map.Entry<StatsInterface, ArrayList<NativeFunction>> entry : this.snapshot.entrySet()) {
            StatsInterface si = entry.getKey();
            ArrayList<NativeFunction> list = new ArrayList<NativeFunction>(entry.getValue().size());
            for (NativeFunction original : entry.getValue()) {
                NativeFunction copy = original.copy();
                copy.setCounter(si.functionCounter(copy.getOrdinal()));
                copy.subtract(original);
                list.add(copy);
            }
            rc.put(si, list);
        }
        return new NativeStats(rc);
    }

    public void dump(PrintStream ps) {
        boolean firstSI = true;
        for (Map.Entry<StatsInterface, ArrayList<NativeFunction>> entry : this.snapshot.entrySet()) {
            StatsInterface si = entry.getKey();
            ArrayList<NativeFunction> funcs = entry.getValue();
            int total = 0;
            for (NativeFunction func : funcs) {
                total += func.getCounter();
            }
            if (!firstSI) {
                ps.print(", ");
            }
            firstSI = false;
            ps.print("[");
            if (total > 0) {
                ps.println("{ ");
                ps.println("  \"class\": \"" + si.getNativeClass() + "\",");
                ps.println("  \"total\": " + total + ", ");
                ps.print("  \"functions\": {");
                boolean firstFunc = true;
                for (NativeFunction func : funcs) {
                    if (func.getCounter() <= 0) continue;
                    if (!firstFunc) {
                        ps.print(",");
                    }
                    firstFunc = false;
                    ps.println();
                    ps.print("    \"" + func.getName() + "\": " + func.getCounter());
                }
                ps.println();
                ps.println("  }");
                ps.print("}");
            }
            ps.print("]");
        }
    }

    private static HashMap<StatsInterface, ArrayList<NativeFunction>> snapshot(Collection<StatsInterface> classes) {
        HashMap<StatsInterface, ArrayList<NativeFunction>> rc = new HashMap<StatsInterface, ArrayList<NativeFunction>>();
        for (StatsInterface sc : classes) {
            int count = sc.functionCount();
            ArrayList<NativeFunction> functions = new ArrayList<NativeFunction>(count);
            for (int i = 0; i < count; ++i) {
                String name = sc.functionName(i);
                functions.add(new NativeFunction(i, name, 0));
            }
            Collections.sort(functions);
            rc.put(sc, functions);
        }
        return rc;
    }

    public static class NativeFunction
    implements Comparable<NativeFunction> {
        private final int ordinal;
        private final String name;
        private int counter;

        public NativeFunction(int ordinal, String name, int callCount) {
            this.ordinal = ordinal;
            this.name = name;
            this.counter = callCount;
        }

        void subtract(NativeFunction func) {
            this.counter -= func.counter;
        }

        public int getCounter() {
            return this.counter;
        }

        public void setCounter(int counter) {
            this.counter = counter;
        }

        public String getName() {
            return this.name;
        }

        public int getOrdinal() {
            return this.ordinal;
        }

        @Override
        public int compareTo(NativeFunction func) {
            return func.counter - this.counter;
        }

        public void reset() {
            this.counter = 0;
        }

        public NativeFunction copy() {
            return new NativeFunction(this.ordinal, this.name, this.counter);
        }
    }

    public static interface StatsInterface {
        public String getNativeClass();

        public int functionCount();

        public String functionName(int var1);

        public int functionCounter(int var1);
    }
}

