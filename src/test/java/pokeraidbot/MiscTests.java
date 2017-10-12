package pokeraidbot;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static pokeraidbot.jda.SignupWithPlusCommandListener.plusXRegExp;

public class MiscTests {
    @Test
    public void testRegExpForPlusCommand() throws Exception {
        Assert.assertThat("+1 10:35 Hästen".matches(plusXRegExp),
                is(true));
    }
}
