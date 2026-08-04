export function errorMessage(error: unknown): string {
  const candidate = error as { error?: { message?: string } };
  return candidate?.error?.message ?? "Não foi possível concluir a operação.";
}
