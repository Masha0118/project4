package com.mysite.sbb.comparison;

import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/comparison")
public class ComparisonController {

    private final ComparisonHistoryService comparisonHistoryService;
    private final UserService userService;

    @PostMapping("/save")
    public ResponseEntity<?> saveComparison(@RequestBody List<ComparisonProductDTO> products) {
        SiteUser currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body("{\"message\": \"인증되지 않은 사용자입니다.\"}");  // Unauthorized
        }

        try {
            // 비교 이력을 저장
            ComparisonHistory savedHistory = comparisonHistoryService.saveComparisonHistory(currentUser, products);

            // 응답 객체 생성
            ComparisonHistoryResponse response = new ComparisonHistoryResponse(savedHistory);

            // 성공 응답 반환
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            // 예외 발생 시 오류 메시지 로깅
            e.printStackTrace();

            // 오류 응답 반환
            return ResponseEntity.status(500).body("{\"message\": \"비교 이력을 저장하는 중 오류가 발생했습니다.\"}");
        }
    }


    @GetMapping("/history")
    public ResponseEntity<List<ComparisonHistoryResponse>> getComparisonHistory() {
        SiteUser currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body(null);  // Unauthorized
        }

        List<ComparisonHistory> historyList = comparisonHistoryService.getComparisonHistories(currentUser);
        List<ComparisonHistoryResponse> responseList = historyList.stream()
                .map(ComparisonHistoryResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    @DeleteMapping("/delete/{historyId}")
    public ResponseEntity<?> deleteComparisonGroup(@PathVariable("historyId") Long historyId) {
        try {
            comparisonHistoryService.deleteComparisonHistory(historyId);
            return ResponseEntity.ok("{\"message\": \"비교 그룹이 삭제되었습니다.\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"비교 그룹 삭제 중 오류가 발생했습니다.\"}");
        }
    }
}
