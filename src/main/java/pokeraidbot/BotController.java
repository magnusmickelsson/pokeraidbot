package pokeraidbot;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BotController {
    @RequestMapping(value = "/", produces = "text/html")
    public String index() {
        return "<html><body>Pokeraidbot says hi! To add me to your server, please use this link: https://discordapp.com/oauth2/authorize?&client_id=356483458316632074&scope=bot&permissions=0 ..." +
                "\nUsage in chat: !raid usage</body></html>";
    }
}
