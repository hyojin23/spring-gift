package gift.option;

import gift.category.Category;
import gift.option.exception.DuplicateOptionNameException;
import gift.option.exception.OptionDeletionNotAllowedException;
import gift.option.exception.OptionNotFoundException;
import gift.option.exception.OptionProductNotFoundException;
import gift.option.exception.OptionValidationException;
import gift.product.Product;
import gift.product.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OptionServiceTest {

    private final OptionRepository optionRepository = mock(OptionRepository.class);
    private final ProductRepository productRepository = mock(ProductRepository.class);
    private final OptionService optionService = new OptionService(optionRepository, productRepository);

    @Test
    @DisplayName("상품을 찾지 못하면 옵션 상품 미존재 예외를 던진다")
    void getOptionsProductNotFound() {
        when(productRepository.findById(999999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> optionService.getOptions(999999L))
            .isInstanceOf(OptionProductNotFoundException.class);
    }

    @Test
    @DisplayName("중복 옵션명으로 생성하면 중복 옵션명 예외를 던진다")
    void createOptionDuplicateName() {
        Product product = product(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(optionRepository.existsByProductIdAndName(1L, "블랙")).thenReturn(true);

        assertThatThrownBy(() -> optionService.createOption(1L, new OptionRequest("블랙", 10)))
            .isInstanceOf(DuplicateOptionNameException.class);
    }

    @Test
    @DisplayName("옵션명 검증에 실패하면 옵션명 검증 예외를 던진다")
    void createOptionInvalidName() {
        assertThatThrownBy(() -> optionService.createOption(1L, new OptionRequest("블랙@", 10)))
            .isInstanceOf(OptionValidationException.class);
    }

    @Test
    @DisplayName("삭제할 옵션을 찾지 못하면 옵션 미존재 예외를 던진다")
    void deleteOptionNotFound() {
        Product product = product(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(optionRepository.countByProductId(1L)).thenReturn(2L);
        when(optionRepository.findByIdAndProductId(999999L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> optionService.deleteOption(1L, 999999L))
            .isInstanceOf(OptionNotFoundException.class);
    }

    @Test
    @DisplayName("다른 상품의 옵션을 삭제하려 하면 옵션 미존재 예외를 던진다")
    void deleteOptionFromDifferentProduct() {
        Product product = product(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(optionRepository.countByProductId(1L)).thenReturn(2L);
        when(optionRepository.findByIdAndProductId(5L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> optionService.deleteOption(1L, 5L))
            .isInstanceOf(OptionNotFoundException.class);
    }

    @Test
    @DisplayName("상품에 속한 옵션을 삭제한다")
    void deleteOption() {
        Product product = product(1L);
        Option option = option(product);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(optionRepository.countByProductId(1L)).thenReturn(2L);
        when(optionRepository.findByIdAndProductId(5L, 1L)).thenReturn(Optional.of(option));

        optionService.deleteOption(1L, 5L);

        verify(optionRepository).delete(option);
    }

    @Test
    @DisplayName("마지막 옵션을 삭제하려 하면 옵션 삭제 제한 예외를 던진다")
    void deleteLastOption() {
        Product product = product(3L);
        when(productRepository.findById(3L)).thenReturn(Optional.of(product));
        when(optionRepository.countByProductId(3L)).thenReturn(1L);

        assertThatThrownBy(() -> optionService.deleteOption(3L, 5L))
            .isInstanceOf(OptionDeletionNotAllowedException.class);
    }

    private Product product(Long id) {
        Product product = new Product("상품", 1000, "https://example.com/product.jpg", category());
        ReflectionTestUtils.setField(product, "id", id);
        return product;
    }

    private Category category() {
        return new Category("카테고리", "#FFFFFF", "https://example.com/category.jpg", "설명");
    }

    private Option option(Product product) {
        return new Option(product, "옵션", 10);
    }
}
