package gift.category;

import gift.category.exception.CategoryNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryDeletionPolicy categoryDeletionPolicy;

    public CategoryService(CategoryRepository categoryRepository, CategoryDeletionPolicy categoryDeletionPolicy) {
        this.categoryRepository = categoryRepository;
        this.categoryDeletionPolicy = categoryDeletionPolicy;
    }

    public List<CategoryResponse> getCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        Category saved = categoryRepository.save(request.toEntity());
        return CategoryResponse.from(saved);
    }

    @Transactional
    public CategoryResponse updateCategory(Category category, CategoryRequest request) {

        category.update(request.name(), request.color(), request.imageUrl(), request.description());

        categoryRepository.save(category);

        return CategoryResponse.from(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = findCategory(id);
        categoryDeletionPolicy.validateDeletable(id);
        categoryRepository.delete(category);
    }

    public Category findCategory(Long id) {
        return categoryRepository.findById(id).orElseThrow(CategoryNotFoundException::new);
    }
}
