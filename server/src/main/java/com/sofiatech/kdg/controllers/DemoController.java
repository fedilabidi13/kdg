package com.sofiatech.kdg.controllers;

import com.sofiatech.kdg.dto.HttpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
@RequiredArgsConstructor
@CrossOrigin
public class DemoController {
    @GetMapping
    public ResponseEntity<HttpResponse> sayHi(){
        return ResponseEntity.ok(new HttpResponse("Access granted for users"));
    }
}
