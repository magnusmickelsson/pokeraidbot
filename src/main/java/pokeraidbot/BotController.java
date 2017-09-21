package pokeraidbot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BotController {
//    @Value("${ownerId}")
    private String ownerId = "360502451574538260";

    @RequestMapping(value = "/", produces = "text/html")
    public String index() {
        String link = "https://discordapp.com/oauth2/authorize?&client_id=" + ownerId + "&scope=bot&permissions=0";
        return "<html><body><h2>Pokeraidbot says <strong>hi</strong>!</h2> " +
                "<p>To add me to your server, please use this link:</p>\n" +
                "<p><a href=\"" + link + "\">" + link + "</a> ...</p>\n" +
                "\n<br/><p>Usage in chat: !raid usage</p>\n" +
                "<p>Source code available at <a href=\"https://github.com/magnusmickelsson/pokeraidbot\">Github</a>.</p>\n" +
                "</body></html>";
    }
}
