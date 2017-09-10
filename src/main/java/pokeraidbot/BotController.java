package pokeraidbot;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BotController {
    @RequestMapping("/")
    public String index() {
        return "Pokeraidbot says hi! To add me to your server, please use this link: https://discordapp.com/oauth2/authorize?&client_id=354980212112621570&scope=bot&permissions=0 ..." +
                "\nUsage in chat: !raid usage";
    }
}
