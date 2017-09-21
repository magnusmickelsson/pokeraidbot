package pokeraidbot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class BotController {
    @Value("${ownerId}")
    private String ownerId;

    @RequestMapping(value = "/")
    public String index(Model model) {
        String link = "https://discordapp.com/oauth2/authorize?&client_id=" + ownerId + "&scope=bot&permissions=0";
        model.addAttribute("link", link);
        return "index";
    }
}
