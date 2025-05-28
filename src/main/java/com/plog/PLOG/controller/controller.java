package com.plog.PLOG.controller;

import com.plog.PLOG.dto.BoardDTO;
import com.plog.PLOG.dto.CustomUserDetails;
import com.plog.PLOG.dto.LocationDTO;
import com.plog.PLOG.dto.dto;
import com.plog.PLOG.entity.BoardEntity;
import com.plog.PLOG.repository.BoardRepository;
import com.plog.PLOG.repository.repository;
import com.plog.PLOG.service.BoardService;
import com.plog.PLOG.service.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class controller {
    @Autowired
    public service s;
    @Autowired
    public repository u_r;
    @Autowired
    public BoardService b_s;
    @Autowired
    private BoardRepository b_r;

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    @GetMapping("/")
    public String home(Model model) {

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        String nickname = s.getCurrentName();
        model.addAttribute("name", name);
        model.addAttribute("nickname", nickname);
        List<BoardDTO> BOARDLIST = b_s.readallBoards();
        model.addAttribute("BOARDLIST", BOARDLIST);
        model.addAttribute("kakaoApiKey", kakaoApiKey);
        return "home";
    }


    @GetMapping("/login")
    public String login(){
        if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                && !SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser")) {
            return "redirect:/";
        }
        return "login";
    }

    @GetMapping("/signup")
    public String signup(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken)) {

            return "redirect:/";
        }
        return "signup";
    }

    @PostMapping("/signupProc")
    public String signupproc(dto d){

            s.toEntity(d);
            return "redirect:/login";

    }

    @GetMapping("/user/update/{username}")
    public String updatePage(@PathVariable("username") String username, Model model) {

        if (s.isAccess(username)) {
            dto dto = s.readOneUser(username);
            String nickname = s.getCurrentName();
            model.addAttribute("USER", dto);
            model.addAttribute("nickname", nickname);
            return "update";
        }
        return "redirect:/login";
    }

    @PostMapping("/user/update/{username}")
    public String updateProcess(@PathVariable("username") String username, dto updatedDto) {

        if (s.isAccess(username)) {

            s.updateOneUser(updatedDto, username);
            return "redirect:/";
        }
        return "redirect:/login";
    }

    @GetMapping("/user/my_blog/{username}")
    public String myBlog(@PathVariable("username") String username, Model model){

        if(s.isAccess(username)){
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            Long userId = s.getCurrentUserId();
            String nickname = s.getCurrentName();
            model.addAttribute("name", name);
            model.addAttribute("nickname", nickname);
            List<BoardDTO> BOARDLIST = b_s.readOneALLBoard(userId);
            model.addAttribute("BOARDLIST", BOARDLIST);
            return "my_blog";
        }

        return "redirect:/";
    }

    @GetMapping("/user/my_blog/{username}/write")
    public String writeBlog(@PathVariable("username") String username, Model model){
        if(s.isAccess(username)){
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            model.addAttribute("name", name);
            String nickname = s.getCurrentName();
            model.addAttribute("nickname", nickname);
            model.addAttribute("kakaoApiKey", kakaoApiKey);
            return "my_blog_write";
        }

        return "redirect:/";
    }

    @PostMapping("/user/my_blog/{username}/write")
    public String writeBlogProc(@PathVariable("username") String username, @ModelAttribute BoardDTO boardDTO) {

        if (s.isAccess(username)) {
            b_s.createOneBoard(boardDTO, boardDTO.getLocations());
            return "redirect:/user/my_blog/" + username;
        }

        return "redirect:/";
    }

    @GetMapping("/board/{boardid}")
    public String readboard(@PathVariable("boardid") Long boardid, Model model){
        List<LocationDTO> locations = b_s.readBoard(boardid);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        String role = auth.getAuthorities().iterator().next().getAuthority();
        BoardEntity board = b_r.findById(boardid).orElseThrow();
        String title = board.getTitle();
        String author = board.getEntity().getName();
        String authorID = board.getEntity().getUsername();


        model.addAttribute("name", name);
        model.addAttribute("locations", locations);
        model.addAttribute("title", title);
        model.addAttribute("author", author);
        String nickname = s.getCurrentName();
        model.addAttribute("nickname", nickname);

        model.addAttribute("authorID", authorID);
        model.addAttribute("role", role);


        return "read_board";
    }

    @GetMapping("/board/{boardid}/update")
    public String boardupdate(@PathVariable("boardid") Long boardid, Model model){

        if(b_s.isAccess(boardid)){
            List<LocationDTO> locations = b_s.readBoard(boardid);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String name = auth.getName();
            String role = auth.getAuthorities().iterator().next().getAuthority();
            BoardEntity board = b_r.findById(boardid).orElseThrow();
            String title = board.getTitle();
            String author = board.getEntity().getName();
            String authorID = board.getEntity().getUsername();


            model.addAttribute("name", name);
            model.addAttribute("locations", locations);
            model.addAttribute("title", title);
            model.addAttribute("author", author);
            String nickname = s.getCurrentName();
            model.addAttribute("nickname", nickname);

            model.addAttribute("authorID", authorID);
            model.addAttribute("role", role);
            return "update_board";
        }
        else{
            return "redirect:/";
        }
    }

    @PostMapping("/board/{boardid}/update")
    public String boardupdateProc(@PathVariable("boardid") Long boardid, BoardDTO dtos){
        if(b_s.isAccess(boardid)){
            b_s.updateBoard(boardid, dtos.getLocations());
            return "redirect:/board/{boardid}";
        }
        return "redirect:/";
    }

    @PostMapping("/board/{boardid}/delete")
    public String boardDeleteProc(@PathVariable("boardid") Long boardid){
        if(b_s.isAccess(boardid)){
            b_s.deleteboard(boardid);
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            return "redirect:/user/my_blog/" + username;
        }
        return "redirect:/";
    }

}
