import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;

import java.lang.annotation.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Count implements BeforeTestExecutionCallback {
    private static int cnt;
    private static final int COUNT = 0;
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        cnt++;
        getStore(context).put(COUNT, cnt);
    }

    public Store getStore(ExtensionContext context) {
        return context.getStore(Namespace.create(getClass(), context.getRequiredTestMethod()));
    }

}

@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(PeriodicEnabling.class)
@Target(ElementType.TYPE)
@interface Periodic {
    int period() default 1;
}
class PeriodicEnabling implements ExecutionCondition {
    private int period;
    //private int count;


    private static final ConditionEvaluationResult ENABLED = ConditionEvaluationResult.enabled("Selected to test");
    private static final ConditionEvaluationResult DISABLED = ConditionEvaluationResult.disabled("No test");

    private Count count = new Count();
    public PeriodicEnabling() {
        System.out.println("Constructor!!");

        Class<TempTest> cls = TempTest.class;

        if(cls.isAnnotationPresent(Periodic.class)) {
            Annotation annotation = cls.getAnnotation(Periodic.class);
            this.period = ((Periodic) annotation).period();
        } else {
            this.period = 1;
        }

    }
    //@Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        System.out.println("current count is" + count.getStore(context));


        int result = count.getStore(context).get(COUNT) % this.period;

        if (result == 0) {
            System.out.println("after::enabled::currCount = " + currCount);
            return ENABLED;
        }

        System.out.println("after::disabled::currCount = " + currCount + ", result=" + result);
        return DISABLED;
    }

}

@ExtendWith(Count.class)
@Periodic(period = 2)
public class TempTest {

    @Test
    @ExtendWith(PeriodicEnabling.class)
    public void testTrue() {
        assertTrue(true);
    }

    @Test
    @ExtendWith(PeriodicEnabling.class)
    public void testZero() {
        int val = 0; assertEquals(0, val);
    }

    @Test
    @ExtendWith(PeriodicEnabling.class)
    public void testZero2() {
        assertEquals(0, "".length());
    }

    @Test
    @ExtendWith(PeriodicEnabling.class)
    public void testFalse() {
        assertTrue(!false);
    }
}
