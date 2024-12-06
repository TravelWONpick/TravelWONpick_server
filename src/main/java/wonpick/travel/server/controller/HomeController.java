package wonpick.travel.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class HomeController {

    private static final Logger logger = LogManager.getLogger(HomeController.class);

    @GetMapping("/")
    public String home() {
        logger.info("[travelwonpick] home api 요청");
        return "redirect:/index.html";
    }
}