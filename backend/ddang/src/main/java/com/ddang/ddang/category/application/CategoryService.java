package com.ddang.ddang.category.application;

import com.ddang.ddang.category.application.dto.response.ReadCategoryDto;
import com.ddang.ddang.category.domain.Category;
import com.ddang.ddang.category.domain.repository.CategoryRepository;
import com.ddang.ddang.category.infrastructure.exception.UninitializedCategoryException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<ReadCategoryDto> readAllMain() {
        final List<Category> mainCategories = categoryRepository.findMainAllByMainCategoryIsNull();

        if (mainCategories.isEmpty()) {
            throw new UninitializedCategoryException("등록된 메인 카테고리가 없습니다.");
        }

        return mainCategories.stream()
                             .map(ReadCategoryDto::from)
                             .toList();
    }

    public List<ReadCategoryDto> readAllSubByMainId(final Long mainId) {
        final List<Category> subCategories = categoryRepository.findSubAllByMainCategoryId(mainId);

        if (subCategories.isEmpty()) {
            throw new UninitializedCategoryException("지정한 메인 카테고리에 해당하는 서브 카테고리가 없습니다.");
        }

        return subCategories.stream()
                            .map(ReadCategoryDto::from)
                            .toList();
    }
}
