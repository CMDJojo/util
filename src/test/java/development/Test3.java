package development;

import com.cmdjojo.util.RootLogger;
import com.cmdjojo.util.SubLogger;

public class Test3 {
    public static void main(String[] args) {
        RootLogger rl = new RootLogger("root", System.out);
        SubLogger sl = rl.createChild("child1");
        SubLogger sl2 = rl.createChild("child2");
        SubLogger sll = sl.createChild("childyy");

        rl.println("Hello darkness my old friend");
        sl.println("HEllo 2");
        sl2.println("child 2 am i");
        sll.println("im smallest childy");
        sll.flush();
    }
}
