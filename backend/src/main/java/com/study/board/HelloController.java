//package com.study.board;
//
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//public class HelloController {
//    @GetMapping("/hello")
//    public String hello(){
//        return "hello";
//    }
//    @GetMapping("/posts/{id}")
//    public String get(@PathVariable Long id){
//        return "글 번호 : " + id;
//    }
////    @GetMapping("/posts")
////    public String list(@RequestParam int page, @RequestParam int size){
////        return page + "페이지, " + size + "개씩";
////    }
//    @GetMapping("/posts")
//    public String list(
//            @RequestParam(defaultValue = "1") int page,
//            @RequestParam(defaultValue = "10") int size) {
//        return page + "페이지, " + size + "개씩";
//    }
//
//    record PostCreateRequest(String title, String content) {}
//
//    @PostMapping("/posts")
//    public String create(@RequestBody PostCreateRequest request) {
//        return request.title() + " / " + request.content();
//    }
//
//    @PostMapping("/hello")
//    public String helloPost(){
//        return "hello POST";
//    }
//    @DeleteMapping("/hello")
//    public String helloDelete() {
//        return "hello DELETE";
//    }
//}
