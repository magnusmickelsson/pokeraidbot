package pokeraidbot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RaidOverviewCommandTest {
    @Test
    public void canSplitLongFieldMessages() {
        EmbedBuilder builder = new EmbedBuilder();
        String longMessage;
        String shortMessage;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= 10; i++) {
            sb.append("Raid ").append(i).append(": somethingsomething at some place - 10 people signed up").append("\n");
        }

        shortMessage = sb.toString();

        sb = new StringBuilder();
        for (int i = 0; i <= 100; i++) {
            sb.append("Raid ").append(i).append(": somethingsomething at some place - 10 people signed up").append("\n");
        }

        longMessage = sb.toString();

        RaidOverviewCommand.addFieldSplitMessageIfNeeded(builder, "Testboss", shortMessage);
        assertThat(builder.getFields().size(), is(1));

        builder = new EmbedBuilder();
        RaidOverviewCommand.addFieldSplitMessageIfNeeded(builder, "Testboss", longMessage);
        // Since message is too long according to discord embed message limit, size should be 5.
        assertThat(builder.getFields().size(), is(5));
        String firstField = builder.getFields().get(0).getValue();
        // Verify we strip the /n char when splitting fields
        assertThat(firstField.endsWith("\n"), is(false));
        String splitField = builder.getFields().get(1).getValue();
        assertThat(splitField.startsWith("\n"), is(false));
        // Uncomment to verify manually that it looks fine
//        for (MessageEmbed.Field field : builder.getFields()) {
//            System.out.println(field.getName());
//            System.out.println(field.getValue());
//            System.out.println("\n\n\n");
//        }
    }
}
