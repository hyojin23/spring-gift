# .specify 디렉터리 안내

`.specify/` 디렉터리는 spec-kit 실행을 위한 설정, 템플릿, 스크립트, 워크플로우 파일을 보관합니다.
일반적인 코드 리뷰에서 우선 확인해야 하는 문서가 아니라, AI와 도구가 SDD 문서를 생성하고 작업 흐름을 실행하기 위한 보조 리소스입니다.

## 주요 용도

```text
templates/     # spec, plan, tasks 등 문서 생성을 위한 템플릿
scripts/       # spec-kit 작업을 보조하는 PowerShell 스크립트
workflows/     # spec-kit 워크플로우 정의
memory/        # 프로젝트 작업 원칙과 규칙
extensions/    # 확장 기능 설정과 명령
integrations/  # 외부 도구 연동 메타데이터
```

## 리뷰 기준

리뷰어는 보통 `.specify/` 내부 파일을 모두 확인할 필요가 없습니다.
SDD 문서의 내용과 실제 구현을 검토할 때는 `specs/` 하위의 각 작업 문서를 우선 확인하면 됩니다.

`.specify/` 내부 변경은 다음 경우에만 중점적으로 확인합니다.

- spec-kit 템플릿이나 생성 규칙을 변경한 경우
- SDD 작업 흐름, 스크립트, 확장 설정을 변경한 경우
- AI가 따르는 프로젝트 작업 원칙을 변경한 경우

