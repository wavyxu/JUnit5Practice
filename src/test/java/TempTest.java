import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;

import java.lang.annotation.*;
import static org.junit.platform.commons.util.AnnotationUtils.findAnnotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(PeriodicEnabling.class)
@Target(ElementType.TYPE)
@Tag("Periodic")
@interface Periodic {
    int period() default 1;
}



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

class PeriodicEnabling implements ExecutionCondition, BeforeTestExecutionCallback {
    private int period;
    private static int cnt = 1;

    private static final ConditionEvaluationResult ENABLED = ConditionEvaluationResult.enabled("Selected to test");
    private static final ConditionEvaluationResult DISABLED = ConditionEvaluationResult.disabled("No test");

    public PeriodicEnabling() {

        Class<TempTest> cls = TempTest.class;

        if(cls.isAnnotationPresent(Periodic.class)) {
            Annotation annotation = cls.getAnnotation(Periodic.class);
            this.period = ((Periodic) annotation).period();
        } else {
            this.period = 1;
        }

    }
    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {

        if ((int)context.getStore(Namespace.GLOBAL).get("COUNT") % period != 0) {
                System.out.println("Test case " + cnt + " is skipped");
                return DISABLED;
            }

            System.out.println("Test case " + cnt + " is running ");
            return ENABLED;
    }



    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        cnt++;
        context.getStore(Namespace.GLOBAL).put("COUNT", cnt);


        System.out.println(context.getStore(Namespace.GLOBAL).get("COUNT"));
    }

}