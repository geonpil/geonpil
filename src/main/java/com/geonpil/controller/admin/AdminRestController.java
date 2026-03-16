package com.geonpil.controller.admin;

import com.geonpil.domain.admin.Banners;
import com.geonpil.domain.admin.BookPick;
import com.geonpil.service.admin.BannerService;
import com.geonpil.service.admin.BookPickService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/admin")
@RequiredArgsConstructor
public class AdminRestController {

    private final BookPickService bookPickService;
    private final BannerService bannerService;


    @PostMapping("/book-picks")
    public ResponseEntity<String> addBookPicks(@RequestBody BookPick bookPick){

        System.out.println("isbn 확인" + bookPick.getIsbn());
        bookPickService.saveBookPick(bookPick);

        return ResponseEntity.ok("입력완료");
    }

    @PutMapping("/book-picks/{id}")
    public ResponseEntity<String> delBookPicks(@PathVariable("id") String bookPickId){

        bookPickService.deleteBookPick(bookPickId);

        return ResponseEntity.ok("입력완료");
    }

    @PostMapping("/banners")
    public ResponseEntity<String> addBanner(@RequestBody Banners banner) {

        try {
            bannerService.validateVisibleLimit(banner);
            bannerService.saveBanner(banner);
            return ResponseEntity.ok("배너가 등록되었습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("배너 등록 중 오류가 발생했습니다.");
        }
    }

    @GetMapping("/banners/{id}")
    public ResponseEntity<Banners> getBanner(@PathVariable Long id) {
        Banners banner = bannerService.getBannerById(id);
        if (banner == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(banner);
    }

    @PutMapping("/banners/{id}")
    public ResponseEntity<String> updateBanner(@PathVariable Long id, @RequestBody Banners banner) {
        try {
            banner.setId(id);
            bannerService.validateVisibleLimit(banner);
            bannerService.updateBanner(banner);
            return ResponseEntity.ok("배너가 수정되었습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("배너 수정 중 오류가 발생했습니다.");
        }
        
    }

    @DeleteMapping("/banners/{id}")
    public ResponseEntity<String> deleteBanner(@PathVariable Long id) {
        bannerService.deleteBanner(id);
        return ResponseEntity.ok("배너가 삭제되었습니다.");
    }
}