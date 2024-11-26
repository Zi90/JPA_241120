package com.ezen.boot_JPA.controller;

import com.ezen.boot_JPA.dto.BoardDTO;
import com.ezen.boot_JPA.dto.BoardFileDTO;
import com.ezen.boot_JPA.dto.FileDTO;
import com.ezen.boot_JPA.dto.PagingVO;
import com.ezen.boot_JPA.handler.FileHandler;
import com.ezen.boot_JPA.handler.FileRemoveHandler;
import com.ezen.boot_JPA.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@RequestMapping("/board/*")
@RequiredArgsConstructor
@Controller
public class BoardController {
    private final BoardService boardService;
    private final FileHandler fileHandler;

    @GetMapping("/register")
    public void register(){}

/*    @PostMapping("/register")
    public String register(BoardDTO boardDTO){
        log.info(">>> boardDTO >>> {}", boardDTO);
        // insert, update, delete => return 1 row
        // jpa insert, update, delete => return id
        Long bno = boardService.insert(boardDTO);
        log.info(">>> insert >>> {}", bno > 0 ? "OK" : "FAIL");
        return "/index";
    }*/

    @PostMapping("/register")
    public String Register(BoardDTO boardDTO, @RequestParam(name = "files", required = false)
                           MultipartFile[] files){
        List<FileDTO> flist = null;
        if(files != null && files[0].getSize() > 0){
            // 파일 핸들러 작업
            flist = fileHandler.uploadFiles(files);
        }
        long bno = boardService.insert(new BoardFileDTO(boardDTO, flist));
        return "/index";
    }

    /*@GetMapping("/list")
    public void list(Model model){
        // paging이 없는 케이스
        List<BoardDTO> list = boardService.getList();
        model.addAttribute("list", list);
    }*/

/*    @GetMapping("/list")
    public void list(Model model, @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo){
        // 화면에서 들어오는 pageNo = 1 / 0으로 처리가 되어야 함
        // 화면에서 들어오는 pageNo = 2 / 1로 처리가 되어야 함
        log.info(">>> pageNo >>> {}", pageNo);
        pageNo = (pageNo == 0 ? 0 : pageNo - 1);
        log.info(">>> pageNo >>> {}", pageNo);
        Page<BoardDTO> list = boardService.getList(pageNo);

        log.info(">>> list >>> {}", list.toString());
        log.info(">>> totalCount >>> {}", list.getTotalElements()); // 전체 글 수
        log.info(">>> totalPage >>> {}", list.getTotalPages()); // 전체 페이지 수 => realEndPage
        log.info(">>> pageNumber >>> {}", list.getNumber()); // 전체 페이지 번호 => pageNo
        log.info(">>> pageSize >>> {}", list.getSize()); // 한 페이지에 표시되는 길이 => qty
        log.info(">>> next >>> {}", list.hasNext()); // next 여부
        log.info(">>> prev >>> {}", list.hasPrevious()); // prev 여부

        PagingVO pgvo = new PagingVO(list, pageNo);
        log.info(">>> pgvo >>> {}", pgvo.toString());
        model.addAttribute("list", list);
        model.addAttribute("pgvo", pgvo);
    }*/

    @GetMapping("/list")
    public void list(Model model, @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
                     @RequestParam(value = "type", required = false) String type,
                     @RequestParam(value = "keyword", required = false) String keyword) {
        pageNo = (pageNo == 0 ? 0 : pageNo - 1);
        Page<BoardDTO> list = boardService.getList(pageNo, type, keyword); // type, keyword 추가하여 서비스임플로 보내기
        PagingVO pgvo = new PagingVO(list, pageNo, type, keyword);
        model.addAttribute("list", list);
        model.addAttribute("pgvo", pgvo);
    }

    @GetMapping("/detail")
    public void modify(Model model, @RequestParam("bno") Long bno){
//        BoardDTO boardDTO = boardService.getDetail(bno);
        BoardFileDTO boardFileDTO = boardService.getDetail(bno);
        model.addAttribute("boardFileDTO", boardFileDTO);
    }

/*    @PostMapping("/modify")
    public String modify(BoardDTO boardDTO, RedirectAttributes redirectAttributes){
        Long bno = boardService.modify(boardDTO);
        redirectAttributes.addAttribute("bno", boardDTO.getBno());
        return "redirect:/board/detail";
    }   */

    @PostMapping("/modify")
    public String modify(BoardDTO boardDTO, @RequestParam(name = "files", required = false) MultipartFile[] files, RedirectAttributes redirectAttributes){
        List<FileDTO> flist = null;
        if(files != null && files[0].getSize() > 0){
            flist = fileHandler.uploadFiles(files);
        }
        Long bno = boardService.modify(new BoardFileDTO(boardDTO, flist));
        redirectAttributes.addAttribute("bno", boardDTO.getBno());
        return "redirect:/board/detail";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("bno") Long bno){
        boardService.delete(bno);
        return "redirect:/board/list";
    }

    @ResponseBody
    @DeleteMapping("/file/{uuid}")
    public String fileRemove(@PathVariable("uuid") String uuid){
        FileDTO fvo = boardService.getFile(uuid);
        long bno = boardService.fileRemove(uuid);
        FileRemoveHandler fr = new FileRemoveHandler();
        boolean isDel = fr.deleteFile(fvo);
        return (bno > 0 && isDel) ? "1" : "0";
    }
}
