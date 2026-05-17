# Data Model: Option 상품 범위 조회 리팩토링

## Option

옵션 삭제 대상 엔티티입니다.

### Fields

- `id`: 옵션 식별자
- `product`: 옵션이 속한 상품
- `name`: 옵션 이름
- `quantity`: 옵션 재고 수량

### Relevant Rules

- 옵션 삭제는 요청한 상품에 속한 옵션에 대해서만 수행합니다.
- 삭제 요청의 `optionId`가 요청한 `productId`에 속하지 않으면 옵션 미존재로 처리합니다.
- 상품에 남은 옵션이 1개 이하이면 삭제 대상 옵션 조회 전에 삭제 제한 예외가 발생합니다.

## OptionRepository

### Existing Methods

- `List<Option> findByProductId(Long productId)`: 상품의 옵션 목록을 조회합니다.
- `boolean existsByProductIdAndName(Long productId, String name)`: 상품 내 옵션명 중복 여부를 확인합니다.
- `long countByProductId(Long productId)`: 상품에 속한 옵션 개수를 조회합니다.

### New Method

- `Optional<Option> findByIdAndProductId(Long optionId, Long productId)`: 요청 상품에 속한 삭제 대상 옵션을 조회합니다.

### Deletion Lookup Usage

`OptionService.deleteOption`은 삭제 대상 옵션을 찾을 때 product-scoped lookup method를 사용합니다.

```java
Option option = optionRepository.findByIdAndProductId(optionId, productId)
    .orElseThrow(OptionNotFoundException::new);
```

## Behavioral Compatibility

- DB schema는 변경하지 않습니다.
- API response schema는 변경하지 않습니다.
- 기존 Option 예외 계층과 글로벌 예외 매핑을 그대로 사용합니다.
