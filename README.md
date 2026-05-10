# spring-gift

## 기능 목록 및 개선 계획

### 1. Category

#### 구조 개선
- [x] CategoryController의 비즈니스 로직을 CategoryService로 분리
- [x] 카테고리 조회 책임을 Service 계층으로 이동
- [ ] CategoryResponse 변환 책임을 Service 또는 정적 팩토리로 일관되게 정리
- [x] 카테고리 수정 시 존재 여부 확인 로직을 Service로 이동
- [x] 생성/수정/삭제 메서드에 트랜잭션 경계 설정

#### 테스트
- [x] 카테고리 목록 조회 테스트
- [x] 카테고리 생성 성공 테스트
- [x] 카테고리 수정 성공 테스트
- [x] 존재하지 않는 카테고리 수정 실패 테스트
- [x] 카테고리 삭제 성공 테스트
