import { HttpErrorResponse } from '@angular/common/http';

export interface FieldError {
  field: string;
  reason: string;
}

export interface ApiErrorBody {
  traceId: string;
  message: string;
  errors: FieldError[];
}

/** Extrai uma mensagem exibível do payload de erro padronizado do backend (traceId/message/errors[]). */
export function extractErrorMessage(error: unknown): string {
  if (error instanceof HttpErrorResponse) {
    const body = error.error as Partial<ApiErrorBody> | undefined;
    if (body?.errors?.length) {
      return body.errors.map((fieldError) => fieldError.reason).join(' ');
    }
    if (body?.message) {
      return body.message;
    }
  }
  return 'Ocorreu um erro inesperado. Tente novamente.';
}
