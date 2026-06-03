# .specify 디렉터리 안내

`.specify/` 디렉터리는 spec-kit 기반 SDD(Spec Driven Development)를 적용할 때 사용한
최소한의 프로젝트 기준과 문서 템플릿을 보관합니다.
일반적인 코드 리뷰에서 우선 확인해야 하는 문서가 아니라, 리팩터링 기준과 문서 생성 형식을
추적하기 위한 보조 리소스입니다.

## 주요 용도

```text
templates/     # spec, plan, tasks 등 문서 생성을 위한 템플릿
memory/        # 프로젝트 작업 원칙과 규칙
```

## 리뷰 기준

리뷰어는 보통 `.specify/` 내부 파일을 모두 확인할 필요가 없습니다.
SDD 문서의 내용과 실제 구현을 검토할 때는 `specs/` 하위의 각 작업 문서를 우선 확인하면 됩니다.

`.specify/` 내부 변경은 다음 경우에만 중점적으로 확인합니다.

- spec-kit 템플릿이나 생성 규칙을 변경한 경우
- AI가 따르는 프로젝트 작업 원칙을 변경한 경우

## 보관 기준

spec-kit 실행 과정에서 생성되는 agent, prompt, script, workflow, extension, integration 파일은
리뷰 대상에 비해 노이즈가 커서 저장소에 보관하지 않습니다.

저장소에는 리팩터링 의사결정과 리뷰에 직접 도움이 되는 최소 문서만 남깁니다.

