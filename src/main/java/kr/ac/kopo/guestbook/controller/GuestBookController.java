package kr.ac.kopo.guestbook.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/guestbook") //templates에 guestbook 디렉토리를 만들어줘야함. 이름 같게
@Log4j2
public class GuestBookController {

    @GetMapping({"/", "/list"})
    public String list(){
        log.info("방명록 목록 화면");
        return "/guestbook/list";
    }
}
