# Data Model: Option 삭제 검증 조회 최적화

## Option

옵션 삭제 검증의 대상 엔티티입니다.

### Fields

- `id`: 옵션 식별자
- `product`: 옵션이 속한 상품
- `name`: 옵션 이름
- `quantity`: 옵션 재고 수량

### Relevant Rules

- 상품은 최소 1개의 옵션을 가져야 합니다.
- 상품에 남은 옵션이 1개 이하이면 옵션 삭제를 허용하지 않습니다.
- 삭제 요청의 `optionId`가 요청한 `productId`에 속하지 않으면 옵션 미존재로 처리합니다.

## OptionRepository

### Existing Methods

- `List<Option> findByProductId(Long productId)`: 상품의 옵션 목록을 조회합니다.
- `boolean existsByProductIdAndName(Long productId, String name)`: 상품 내 옵션명 중복 여부를 확인합니다.

### New Method

- `long countByProductId(Long productId)`: 상품에 속한 옵션 개수를 조회합니다.

### Validation Usage

`OptionService.validateCanDelete`는 삭제 가능 여부를 판단할 때 `countByProductId`를 사용합니다.

```java
if (optionRepository.countByProductId(productId) <= 1) {
    throw new OptionDeletionNotAllowedException();
}
```

## Behavioral Compatibility

- DB schema는 변경하지 않습니다.
- API response schema는 변경하지 않습니다.
- 기존 Option 예외 계층과 글로벌 예외 매핑을 그대로 사용합니다.
