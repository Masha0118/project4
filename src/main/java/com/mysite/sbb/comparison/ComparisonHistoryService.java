package com.mysite.sbb.comparison;

import com.mysite.sbb.user.SiteUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ComparisonHistoryService {

    private final ComparisonHistoryRepository comparisonHistoryRepository;
    private final ComparisonProductRepository comparisonProductRepository;

    @Transactional
    public ComparisonHistory saveComparisonHistory(SiteUser user, List<ComparisonProductDTO> productDTOs) {
        ComparisonHistory history = new ComparisonHistory();
        history.setUser(user);
        history.setComparisonDate(LocalDateTime.now());

        // 먼저 History를 저장
        comparisonHistoryRepository.save(history);

        // DTO를 Product로 변환하고 History에 추가
        List<ComparisonProduct> products = productDTOs.stream()
                .map(dto -> {
                    ComparisonProduct product = new ComparisonProduct();
                    product.setProductId(dto.getProductId());
                    product.setTitle(dto.getTitle());
                    product.setImage(dto.getImage());
                    product.setPrice(dto.getPrice());
                    product.setLink(dto.getLink());
                    product.setComparisonHistory(history); // 관계 설정
                    return product;
                })
                .collect(Collectors.toList());

        // Products를 History에 추가
        history.setProducts(products);

        // Product를 저장
        comparisonProductRepository.saveAll(products);

        // 다시 History를 저장하여 Product 리스트가 포함되도록 업데이트
        comparisonHistoryRepository.save(history);

        return history;
    }

    public List<ComparisonHistory> getComparisonHistories(SiteUser user) {
        return comparisonHistoryRepository.findByUser(user);
    }

    @Transactional
    public void deleteComparisonHistory(Long historyId) {
        ComparisonHistory history = comparisonHistoryRepository.findById(historyId)
                .orElseThrow(() -> new RuntimeException("비교 그룹을 찾을 수 없습니다."));
        comparisonHistoryRepository.delete(history);
    }
}
