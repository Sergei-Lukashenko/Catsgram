package ru.yandex.practicum.catsgram.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
public class HomeController {

//    @RequestMapping(value = "/home", method = RequestMethod.GET) // можно и так
//    @GetMapping("/home") // или так -  это проще и удобнее, чем предыдущее
    @GetMapping    // но это лучше всего, если клас аннотирован @RequestMapping с начальным путем
    public String homePage() {
        return "<h1>Приветствуем вас, в приложении Котограм<h1>";
    }
}
